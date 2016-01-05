import bintray.Keys._

name := "key-path"

organization := "com.hanhuy.sbt"

version := "0.3"

scalacOptions ++= Seq("-deprecation","-Xlint","-feature")

sbtPlugin := true

// bintray
bintrayPublishSettings

repository in bintray := "sbt-plugins"

publishMavenStyle := false

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := None

// scripted
//scriptedSettings

//scriptedLaunchOpts ++= "-Xmx1024m" ::
//  "-Dplugin.version=" + version.value ::
//  Nil
