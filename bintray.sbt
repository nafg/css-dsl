ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := Some("bintray" at "https://api.bintray.com/maven/naftoligug/maven/css-dsl")

sys.env.get("BINTRAYKEY").toSeq.map { key =>
  ThisBuild / credentials += Credentials(
    "Bintray API Realm",
    "api.bintray.com",
    "naftoligug",
    key
  )
}
