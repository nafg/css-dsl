addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
addSbtPlugin("io.github.nafg.mergify" % "sbt-mergify-github-actions" % "0.6.0")
libraryDependencies += "io.github.nafg.scalac-options" %% "scalac-options" % "0.2.0"
