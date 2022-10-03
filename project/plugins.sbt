addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.10.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
addSbtPlugin("io.github.nafg.mergify" % "sbt-mergify-github-actions" % "0.6.0")
libraryDependencies += "io.github.nafg.scalac-options" %% "scalac-options" % "0.2.0"
