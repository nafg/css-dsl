publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some("bintray" at "https://api.bintray.com/maven/naftoligug/maven/css-dsl")

sys.env.get("BINTRAYKEY").toSeq.map { key =>
  credentials in ThisBuild += Credentials(
    "Bintray API Realm",
    "api.bintray.com",
    "naftoligug",
    key
  )
}
