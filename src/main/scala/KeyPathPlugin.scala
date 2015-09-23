import sbt._
import sbt.Keys._
import sbt.complete._
import DefaultParsers._
/**
 * @author pfnguyen
 */
object KeyPathPlugin extends AutoPlugin {
  override def trigger = allRequirements

  override def requires = empty

  override def buildSettings = Seq(commands += pathCommand)

  val pathParser: State => Parser[(ScopedKey[_],ScopedKey[_])] =
    (s: State) => Act.requireSession(s, (token(Space) ~> Act.scopedKeyParser(s)) ~ (token(Space) ~> Act.scopedKeyParser(s)))

  val pathAction: (State, (ScopedKey[_], ScopedKey[_])) => State = {
    case (st,(start,end)) =>
      val extracted = Project.extract(st)
      import extracted._
      val basedir = new File(Project.session(st).current.build)
      val graph = Project.settingGraph(structure, basedir, start)
      st.log.info(s"Path: ${graph.name}")
      st.log.info(s"Looking for: ${showKey(start)} <- ${showKey(end)}")
      val result = dfs(graph, end, st)
      if (result.isEmpty) {
        st.log.error("No paths found")
      } else {
        val s = result.map { r =>
          r.mkString("\n  +-- ")
        }
        st.log.info(s.mkString("\n\n"))
      }
      st
  }

  def dfs(graph: SettingGraph,
          end: ScopedKey[_],
          st: State,
          visited: Set[String] = Set.empty,
          path: List[String] = Nil,
          paths: List[List[String]] = Nil)(implicit display: Show[ScopedKey[_]]): List[List[String]] = {
    import language.postfixOps
    val key = graph.definedIn.getOrElse(graph.name)
    if (visited(key)) {
      paths
    } else if (key == display(end)) {
      (key :: path).reverse :: paths
    } else {
      val visited2 = visited + key
      val graph2 = if (graph.depends.isEmpty) {
        val basedir = new File(Project.session(st).current.build)
        Parser(Act.requireSession(st,
          Act.scopedKeyParser(Project.extract(st))))(key).resultEmpty.toEither.fold(
            _ => graph,
            Project.settingGraph(Project.extract(st).structure, basedir, _))
      } else graph
      graph2.depends flatMap { d =>
        if (!visited2(d.definedIn.getOrElse(d.name)))
          dfs(d, end, st, visited2, key :: path, paths)
        else Nil
      } toList
    }
  }

  val pathCommand = Command("keypath",
    ("keypath", "inspect dependency paths between 2 keys"),
    "Specify 2 keys, first, the key which depends on the other key, second, the key that you want the path to")(pathParser)(pathAction)
}
