import sbt.Keys._
import sbt._


object GeneratorPlugin extends AutoPlugin {
  object autoImport {
    val cssPackageName = settingKey[String]("The package to use")
    val cssModuleName = settingKey[String]("The name of the object to create")
    val cssUrl = settingKey[URL]("The URL of the CSS file to use")

    val cssGen = taskKey[Seq[File]]("Generate the DSL")
  }

  import autoImport._


  override def projectSettings = Seq(
    cssGen := {
      val pkg = cssPackageName.value
      val mod = cssModuleName.value
      val url = cssUrl.value
      val outputDir = (sourceManaged in Compile).value
      val generator = new Generator(pkg, mod, CssExtractor.getClassesFromURL(url))
      val file = outputDir / pkg.replace('.', '/') / (mod + ".scala")
      IO.write(file, generator())
      Seq[File](file)
    },
    sourceGenerators in Compile += cssGen
  )
}
