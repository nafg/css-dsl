val sjsVer = sys.env.getOrElse("SCALAJS_VERSION", "1.2.0")
addSbtPlugin("ch.epfl.scala" % s"sbt-scalajs-bundler${if (sjsVer.startsWith("0.6")) "-sjs06" else ""}" % "0.19.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % sjsVer)

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
