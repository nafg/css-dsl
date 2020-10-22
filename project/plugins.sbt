val sjsVer = sys.env.getOrElse("SCALAJS_VERSION", "1.3.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % sjsVer)

sjsVer.split("\\.") match {
  case Array("0", "6",             _) => addSbtPlugin("ch.epfl.scala" % s"sbt-scalajs-bundler-sjs06" % "0.19.0")
  case Array("1", "0" | "1" | "2", _) => addSbtPlugin("ch.epfl.scala" % s"sbt-scalajs-bundler"       % "0.19.0")
  case Array("1", "3",             _) => addSbtPlugin("ch.epfl.scala" % s"sbt-scalajs-bundler"       % "0.20.0")
}

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
