import scala.meta._


sealed trait TargetImpl {
  def imports: List[Import]
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
        object C extends Classes[TagMod] {
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

  object Scalatags extends TargetImpl {
    override def imports = List(
      q"import scalatags.Text.all._"
    )

    override def C =
      q"""
        object C extends Classes[Modifier] {
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
