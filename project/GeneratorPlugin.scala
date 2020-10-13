import sbt.Keys._
import sbt._


object GeneratorPlugin extends AutoPlugin {
  object autoImport {
    case class CssDslConfig(name: String,
                            prefixes: Set[Option[String]],
                            version: String,
                            versionedUrl: String => String) {
      def scalaPackage = name.toLowerCase.filter(Character.isJavaIdentifierPart)
    }
    val cssDslConfig = settingKey[CssDslConfig]("The settings for generating the CSS DSL")
    val cssVariant = settingKey[TargetImpl]("The target")
    val cssGen = taskKey[Seq[File]]("Generate the DSL")
  }

  import autoImport._


  override def projectSettings = Seq(
    cssGen := {
      val cfg = cssDslConfig.value
      val variant = cssVariant.value
      val outputDir = (Compile / sourceManaged).value
      val url = cfg.versionedUrl(cfg.version)
      streams.value.log.info(s"Processing $url...")
      val classes = CssExtractor.getClassesFromURL(new URL(url))
      for (prefix <- cfg.prefixes.toSeq) yield {
        val name = prefix.getOrElse("").capitalize + "Dsl"
        val generator = new Generator(cfg.scalaPackage, name, prefix, classes, variant)
        val file = outputDir / "cssdsl" / cfg.scalaPackage.replace('.', '/') / s"$name.scala"
        IO.write(file, generator())
        file
      }
    },
    Compile / sourceGenerators += cssGen
  )
}
