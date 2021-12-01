addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.7.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.14.0")
libraryDependencies += "io.github.nafg.mergify" %% "mergify-writer" % "0.2.2"
