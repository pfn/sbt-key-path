package com.hanhuy.sbt.keypath
import sbt._
import sbt.Keys._
import sbt.complete._
import DefaultParsers._
import language.existentials
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
      st.log.info(s"Searching for: ${showKey(start)} <- ${showKey(end)}")
      val result = SearchGraph(graph, end, st).traverse
      println()
      if (result.isEmpty) {
        st.log.error(s"${showKey(start)} does not depend on ${showKey(end)}")
      } else {
        val s = result.map { r =>
          r.mkString("\n  +-- ")
        }
        st.log.info(s.mkString("\n\n"))
      }
      st
  }


  val pathCommand = Command("keypath",
    ("keypath", "inspect dependency paths between 2 keys"),
    "Specify 2 keys, first, the key which depends on the other key, second, the key that you want the path to")(pathParser)(pathAction)
}

case class SearchGraph(graph: SettingGraph, end: ScopedKey[_], st: State)(implicit display: Show[ScopedKey[_]]) {
  def traverse = dfs(graph, end, st)
  private[this] var visited = Set.empty[String]
  private[this] var expanded = Map.empty[String,SettingGraph]
  private[this] def dfs(graph: SettingGraph,
          end: ScopedKey[_],
          st: State,
          path: List[String] = Nil,
          paths: List[List[String]] = Nil): List[List[String]] = {
    import language.postfixOps
    val key = graph.definedIn.getOrElse(graph.name)
    if (visited(key)) {
      paths
    } else if (key == display(end)) {
      (key :: path).reverse :: paths
    } else {
      visited = visited + key
      val graph2 = if (graph.depends.isEmpty) {
        val g2 = expanded.getOrElse(key, {
          val basedir = new File(Project.session(st).current.build)
          print(".")
          Parser(Act.requireSession(st,
            Act.scopedKeyParser(Project.extract(st))))(key).resultEmpty.toEither.fold(
              _ => graph,
              Project.settingGraph(Project.extract(st).structure, basedir, _))
        })
        expanded = expanded + ((key,g2))
        g2
      } else graph
      graph2.depends flatMap { d =>
        if (!visited(d.definedIn.getOrElse(d.name)))
          dfs(d, end, st, key :: path, paths)
        else Nil
      } toList
    }
  }
}
