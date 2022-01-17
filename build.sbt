import scala.sys.process.stringToProcess

import _root_.io.github.nafg.scalacoptions._


def myScalacOptions(version: String) =
  ScalacOptions.all(version)(
    (opts: options.Common) => opts.feature ++ opts.deprecation,
  )

inThisBuild(
  List(
    organization := "io.github.nafg.css-dsl",
    scalaVersion := "3.1.0",
    crossScalaVersions := Seq("2.13.8", scalaVersion.value),
    scalacOptions ++= myScalacOptions(scalaVersion.value),
    versionScheme := Some("early-semver")
  )
)

name := "css-dsl"
publish / skip := true

def npmView(pkg: String, field: String)(parse: Stream[String] => String) =
  parse(s"npm view $pkg $field".lineStream)

def latestTag(pkg: String) = npmView(pkg, "dist-tags.latest")(_.head)

val npmViewVersionRegex = ".*'(.*)'".r

def latestIn(pkg: String, versionMajor: Int) =
  npmView(s"$pkg@$versionMajor", "version")(_.last match { case npmViewVersionRegex(v) => v })

def scalaJsReactSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "2.0.0",
  cssVariant := TargetImpl.ScalaJsReact,
  cssDslConfig := config
)

def scalatagsSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.11.0",
  cssVariant := TargetImpl.Scalatags,
  cssDslConfig := config
)

val bootstrap3Config =
  CssDslConfig(
    "Bootstrap 3",
    Set(None, Some("bs"), Some("bs3")),
    latestIn("bootstrap", 3),
    "https://stackpath.bootstrapcdn.com/bootstrap/" + _ + "/css/bootstrap.min.css"
  )

val bootstrap4Config =
  CssDslConfig(
    "Bootstrap 4",
    Set(None, Some("bs"), Some("bs4")),
    latestIn("bootstrap", 4),
    "https://cdn.jsdelivr.net/npm/bootstrap@" + _ + "/dist/css/bootstrap.min.css"
  )

val bootstrap5Config =
  CssDslConfig(
    "Bootstrap 5",
    Set(None, Some("bs"), Some("bs5")),
    latestIn("bootstrap", 5),
    "https://cdn.jsdelivr.net/npm/bootstrap@" + _ + "/dist/css/bootstrap.min.css"
  )

val bulmaConfig =
  CssDslConfig(
    "Bulma",
    Set(None, Some("b")),
    latestTag("bulma"),
    "https://cdn.jsdelivr.net/npm/bulma@" + _ + "/css/bulma.min.css"
  )

val semanticUiConfig =
  CssDslConfig(
    "Semantic UI",
    Set(Some("s")),
    latestTag("semantic-ui"),
    "https://cdn.jsdelivr.net/npm/semantic-ui@" + _ + "/dist/semantic.min.css"
  )

val fomanticUiConfig =
  CssDslConfig(
    "Fomantic UI",
    Set(Some("f")),
    latestTag("fomantic-ui"),
    "https://cdn.jsdelivr.net/npm/fomantic-ui@" + _ + "/dist/semantic.min.css"
  )

val fontawesomeUiConfig =
  CssDslConfig(
    "Font Awesome",
    Set(None, Some("fa")),
    latestTag("@fortawesome/fontawesome-free"),
    "https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@" + _ + "/css/all.min.css"
  )

lazy val bootstrap3_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bootstrap3Config))
lazy val bootstrap3_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bootstrap3Config))

lazy val bootstrap4_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bootstrap4Config))
lazy val bootstrap4_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bootstrap4Config))

lazy val bootstrap5_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bootstrap5Config))
lazy val bootstrap5_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bootstrap5Config))

lazy val bulma_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bulmaConfig))
lazy val bulma_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bulmaConfig))

lazy val semanticui_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(semanticUiConfig))
lazy val semanticui_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(semanticUiConfig))

lazy val fomanticui_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(fomanticUiConfig))
lazy val fomanticui_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(fomanticUiConfig))

lazy val fontawesome_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(fontawesomeUiConfig))
lazy val fontawesome_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(fontawesomeUiConfig))
