ThisBuild / organization := "io.github.nafg.css-dsl"

ThisBuild / crossScalaVersions := Seq("2.12.11", "2.13.2")
ThisBuild / scalaVersion := (ThisBuild / crossScalaVersions).value.last
ThisBuild / scalacOptions += "-feature"
ThisBuild / scalacOptions += "-deprecation"

name := "css-dsl"
publish / skip := true

def scalaJsReactSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.7.0",
  cssVariant := TargetImpl.ScalaJsReact,
  cssDslConfig := config
)

def scalatagsSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.9.1",
  cssVariant := TargetImpl.Scalatags,
  cssDslConfig := config
)

val bootstrap3Config =
  CssDslConfig(
    "bootstrap3",
    Set(None, Some("bs"), Some("bs3")),
    "3.4.1",
    "https://maxcdn.bootstrapcdn.com/bootstrap/" + _ + "/css/bootstrap.min.css"
  )

val bootstrap4Config =
  CssDslConfig(
    "bootstrap4",
    Set(None, Some("bs"), Some("bs4")),
    "4.4.1",
    "https://maxcdn.bootstrapcdn.com/bootstrap/" + _ + "/css/bootstrap.min.css"
  )

val bulmaConfig =
  CssDslConfig(
    "bulma",
    Set(None, Some("b")),
    "0.8.2",
    "https://cdnjs.cloudflare.com/ajax/libs/bulma/" + _ + "/css/bulma.css"
  )

val semanticUiConfig =
  CssDslConfig(
    "semanticui",
    Set(Some("s")),
    "2.4.2",
    "https://cdn.jsdelivr.net/npm/semantic-ui@" + _ + "/dist/semantic.min.css"
  )

val fomanticUiConfig =
  CssDslConfig(
    "fomanticui",
    Set(Some("f")),
    "2.8.4",
    "https://cdn.jsdelivr.net/npm/fomantic-ui@" + _ + "/dist/semantic.min.css"
  )

lazy val bootstrap3_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bootstrap3Config))
lazy val bootstrap3_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bootstrap3Config))

lazy val bootstrap4_scalajsreact =
  project.enablePlugins(ScalaJSPlugin, GeneratorPlugin).settings(scalaJsReactSettings(bootstrap4Config))
lazy val bootstrap4_scalatags =
  project.enablePlugins(GeneratorPlugin).settings(scalatagsSettings(bootstrap4Config))

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
