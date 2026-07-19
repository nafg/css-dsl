import scala.collection.immutable.SortedSet
import scala.meta._


class Generator(packageName: String,
                moduleName: String,
                prefix: Option[String],
                classes: SortedSet[String],
                variant: TargetImpl) {
  private def camelize(s: String) = {
    val s2 = s.split(Array('-', ' ')).map(_.capitalize).mkString
    s2.take(1).toLowerCase + s2.drop(1)
  }

  private lazy val identifiers = {
    val classesByIdentifier = classes.groupBy(camelize)
    val result = classes.iterator.map { cls =>
      val camelized = camelize(cls)
      val identifier =
        if (classesByIdentifier(camelized).size > 1 && cls != camelized)
          prefix.fold(cls)(_ + "-" + cls)
        else
          prefix.fold(camelized)(_ + camelized.capitalize)
      cls -> identifier
    }.toMap
    val duplicates = result.values.groupBy(identity).collect {
      case (identifier, values) if values.size > 1 => identifier
    }
    require(duplicates.isEmpty, s"Generated duplicate identifiers: ${duplicates.mkString(", ")}")
    result
  }

  private def ident(cls: String) = identifiers(cls)

  def defs: List[Stat] =
    classes.toList.map { cls =>
      val name = Term.Name(ident(cls))
      q"def $name: A = this.op($cls)"
    }

  def cachedDefs: List[Stat] =
    classes.toList.map { cls =>
      val name = Pat.Var(Term.Name(ident(cls)))
      q"override lazy val $name: A = this.op($cls)"
    }

  def allDefs = defs :+ q"protected def op(clz: String): A"

  def tree: Tree =
    q"""
      package cssdsl.${Term.Name(packageName)} {

        import scala.language.implicitConversions
        ..${variant.imports}

        object ${Term.Name(moduleName)} {
          trait Classes[A] {
            ..$allDefs
          }

          trait CachedClasses[A] extends Classes[A] {
            ..$cachedDefs
          }

          ..${variant.support}
          ${variant.C}

          ${variant.implicitClass}
        }
      }
    """

  def apply(): String = tree.syntax + "\n"
}
