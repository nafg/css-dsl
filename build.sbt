ThisBuild / organization := "io.github.nafg.css-dsl"
ThisBuild / version := "0.2.0"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / scalacOptions += "-feature"

name := "css-dsl"
skip in publish := true

val commonSettings = Seq(
  libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.2.3"
)

lazy val bootstrap3 =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bootstrap3",
      cssModuleName := "TB",
      cssUrl := new URL("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")
    )

lazy val bootstrap4 =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bootstrap4",
      cssModuleName := "TB",
      cssUrl := new URL("https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css")
    )

lazy val bulma =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bulma",
      cssModuleName := "Bulma",
      cssUrl := new URL("https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.1/css/bulma.css")
    )
