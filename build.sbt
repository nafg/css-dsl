ThisBuild / organization := "io.github.nafg.css-dsl"
ThisBuild / version := "0.4.0"
ThisBuild / crossScalaVersions := Seq("2.12.8", "2.13.0")
ThisBuild / scalaVersion := (ThisBuild / crossScalaVersions).value.last
ThisBuild / scalacOptions += "-feature"

name := "css-dsl"
skip in publish := true

val commonSettings = Seq(
  libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.4.2"
)

lazy val bootstrap3 =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bootstrap3",
      cssModuleName := "TB",
      cssUrl := new URL("https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css")
    )

lazy val bootstrap4 =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bootstrap4",
      cssModuleName := "TB",
      cssUrl := new URL("https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css")
    )

lazy val bulma =
  project
    .enablePlugins(ScalaJSPlugin, GeneratorPlugin)
    .settings(
      commonSettings,
      cssPackageName := "bulma",
      cssModuleName := "Bulma",
      cssUrl := new URL("https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.css")
    )
