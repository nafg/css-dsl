import scala.collection.immutable.SortedSet
import scala.meta._


class Generator(packageName: String, moduleName: String, classes: SortedSet[String]) {
  private def camelize(s: String) = {
    val s2 = s.split("-").map(_.capitalize).mkString
    s2.take(1).toLowerCase + s2.drop(1)
  }

  def defs: List[Stat] =
    classes.toList.map { cls =>
      Defn.Val(List(Mod.Lazy()), List(Pat.Var(Term.Name(camelize(cls)))), None, q"this.op($cls)")
    }

  def allDefs = defs :+ q"protected def op(clz: String): A"

  def tree: Tree =
    q"""
      package ${Term.Name(packageName)} {

        import scala.language.implicitConversions
        import japgolly.scalajs.react.vdom.html_<^._
        import japgolly.scalajs.react.vdom.{TagOf, TopNode}

        object ${Term.Name(moduleName)} {
          trait Classes[A] {
            ..$allDefs
          }

          object C extends Classes[TagMod] {
            protected override def op(clz: String) = ^.cls := clz
          }

          implicit class ConvertableToTagOfExtensionMethods[T, N <: TopNode](self: T)(implicit toTagOf: T => TagOf[N])
              extends Classes[TagOf[N]] {
            protected override def op(clz: String): TagOf[N] = toTagOf(self).apply(^.cls := clz)
          }
        }
      }
    """

  def apply(): String = tree.syntax + "\n"
}
