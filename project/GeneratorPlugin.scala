import sbt.Keys._
import sbt._


object GeneratorPlugin extends AutoPlugin {
  object autoImport {
    val cssPackageName = settingKey[String]("The package to use")
    val cssUrl = settingKey[URL]("The URL of the CSS file to use")
    val cssPrefixes = settingKey[Seq[Option[String]]]("Prefix variants")
    val cssGen = taskKey[Seq[File]]("Generate the DSL")
  }

  import autoImport._


  override def projectSettings = Seq(
    cssGen := {
      val pkg = cssPackageName.value
      val url = cssUrl.value
      val outputDir = (sourceManaged in Compile).value
      val classes = CssExtractor.getClassesFromURL(url)
      for (prefix <- cssPrefixes.value.distinct) yield {
        val name = prefix.getOrElse("").capitalize + "Dsl"
        val generator = new Generator(pkg, name, prefix, classes)
        val file = outputDir / "cssdsl" / pkg.replace('.', '/') / s"$name.scala"
        IO.write(file, generator())
        file
      }
    },
    sourceGenerators in Compile += cssGen
  )
}
