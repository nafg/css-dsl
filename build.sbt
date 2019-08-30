ThisBuild / organization := "io.github.nafg.css-dsl"
ThisBuild / version := "0.5.0"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / scalacOptions += "-feature"
ThisBuild / scalacOptions += "-deprecation"

name := "css-dsl"
publish / skip := true

def scalaJsReactSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.4.2",
  cssVariant := TargetImpl.ScalaJsReact,
  cssDslConfig := config
)

def scalatagsSettings(config: CssDslConfig) = Seq(
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.7.0",
  cssVariant := TargetImpl.Scalatags,
  cssDslConfig := config
)

val bootstrap3Config =
  CssDslConfig(
    "bootstrap3",
    Set(None, Some("bs"), Some("bs3")),
    new URL("https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css")
  )

val bootstrap4Config =
  CssDslConfig(
    "bootstrap4",
    Set(None, Some("bs"), Some("bs4")),
    new URL("https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css")
  )

val bulmaConfig =
  CssDslConfig(
    "bulma",
    Set(None, Some("b")),
    new URL("https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.css")
  )

val semanticUiConfig =
  CssDslConfig(
    "semanticui",
    Set(Some("s")),
    new URL("https://cdn.jsdelivr.net/npm/semantic-ui@2.4.2/dist/semantic.min.css")
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
