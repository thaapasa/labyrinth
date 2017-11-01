scalaVersion := "2.12.4"

organization := "fi.haapatalo.labyrinth"

// Project name
name := "labyrinth"

// Project version
version := "0.1"

// Set Scala compiler options
scalacOptions ++= Seq("-feature", "-deprecation", "-target:jvm-1.8")

// Set Java compiler options
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Do not create source directories for different scala versions
crossPaths := false

// Set initial commands in console sessions
initialCommands in console := "import fi.haapatalo.labyrinth._"

// For running processes
fork in run := true

// Get rid of Java source directories (compile only scalaSource)
// If Java sources are needed, remove this definition
unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

// Get rid of Java test source directories
unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil

// This skips automatic dependency resolution when version is changed.
// Remember to run "update" in sbt when necessary!
// skip in (ThisBuild, update) := true
