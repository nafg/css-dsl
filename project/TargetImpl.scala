import scala.meta._


sealed trait TargetImpl {
  def imports: List[Import]
  def support: List[Stat] = Nil
  def C: Defn.Object
  def implicitClass: Defn.Class
}
object TargetImpl {
  object ScalaJsReact extends TargetImpl {
    override def imports = List(
      q"import japgolly.scalajs.react.vdom.html_<^._",
      q"import japgolly.scalajs.react.vdom.{TagOf, TopNode}"
    )

    override def C =
      q"""
        object C extends CachedClasses[TagMod] {
          protected override def op(clz: String) = ^.cls := clz
        }
        """

    override def implicitClass =
      q"""
        implicit class ConvertableToTagOfExtensionMethods[T, N <: TopNode](self: T)(implicit toTagOf: T => TagOf[N])
            extends Classes[TagOf[N]] {
          protected override def op(clz: String): TagOf[N] = toTagOf(self).apply(^.cls := clz)
        }
        """
  }

  object ZioHttpTemplate2 extends TargetImpl {
    override def imports = List(
      q"import zio.http.template2.Dom"
    )

    override def support = List(
      q"final case class ClassName(value: String) extends AnyVal",
      q"implicit def classNameToString(className: ClassName): String = className.value",
      q"implicit def classNamesToStrings(classNames: Iterable[ClassName]): Iterable[String] = classNames.map(_.value)"
    )

    override def C =
      q"""
        object C extends CachedClasses[ClassName] {
          protected override def op(clz: String): ClassName = ClassName(clz)
        }
        """

    override def implicitClass = {
      // Keep the literal synthetic: parsed string literals require a tokenizer when Scalameta 4.16 renders the tree.
      val classAttribute = Lit.String("class")
      q"""
        implicit class DomElementExtensionMethods(self: Dom.Element)
            extends Classes[Dom.Element] {
          protected override def op(clz: String): Dom.Element = {
            val classes = self.attributes.get($classAttribute) match {
              case Some(Dom.AttributeValue.MultiValue(values, _)) => values
              case Some(value)                                    => Vector(value.toString)
              case None                                           => Vector.empty
            }
            self(Dom.multiAttr($classAttribute, classes :+ clz))
          }
        }
        """
    }
  }

  object Scalatags extends TargetImpl {
    override def imports = List(
      q"import scalatags.Text.all._"
    )

    override def C =
      q"""
        object C extends CachedClasses[Modifier] {
          protected override def op(clz: String) = cls := clz
        }
        """

    override def implicitClass =
      q"""
        implicit class ConvertableToTagOfExtensionMethods[O <: String](self: ConcreteHtmlTag[O])
            extends Classes[ConcreteHtmlTag[O]] {
          protected override def op(clz: String): ConcreteHtmlTag[O] =
            self(cls := clz)
        }
        """
  }
}
