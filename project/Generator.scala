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

  private def ident(cls: String) = {
    val camelized = camelize(cls)
    prefix.fold(camelized)(_ + camelized.capitalize)
  }

  def defs: List[Stat] =
    classes.toList.map { cls =>
      Defn.Val(List(Mod.Lazy()), List(Pat.Var(Term.Name(ident(cls)))), None, q"this.op($cls)")
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

          ${variant.C}

          ${variant.implicitClass}
        }
      }
    """

  def apply(): String = tree.syntax + "\n"
}
