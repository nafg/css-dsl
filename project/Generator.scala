import java.io.{InputStreamReader, Reader, StringReader}
import java.net.URL

import scala.collection.immutable.SortedSet

import com.steadystate.css.parser.selectors.ClassConditionImpl
import com.steadystate.css.parser.{CSSOMParser, SACParserCSS3}
import org.w3c.css.sac._
import org.w3c.dom.css.CSSStyleRule
import treehugger.forest._
import definitions._
import treehuggerDSL._


class Generator(packageName: String, moduleName: String, cssUrl: URL) {
  def getClasses(condition: Condition): List[String] = condition match {
    case comb: CombinatorCondition => getClasses(comb.getFirstCondition) ++ getClasses(comb.getSecondCondition)
    case neg: NegativeCondition    => getClasses(neg.getCondition)
    case cls: ClassConditionImpl   => List(cls.getValue)
    case _: ContentCondition       => Nil
    case _: PositionalCondition    => Nil
    case _: LangCondition          => Nil
    case _: AttributeCondition     => Nil
  }

  def getClasses(selector: Selector): List[String] = selector match {
    case desc: DescendantSelector         => getClasses(desc.getAncestorSelector) ++ getClasses(desc.getSimpleSelector)
    case sib: SiblingSelector             => getClasses(sib.getSelector) ++ getClasses(sib.getSiblingSelector)
    case cond: ConditionalSelector        => getClasses(cond.getSimpleSelector) ++ getClasses(cond.getCondition)
    case neg: NegativeSelector            => getClasses(neg.getSimpleSelector)
    case _: ProcessingInstructionSelector => Nil
    case _: CharacterDataSelector         => Nil
    case _: ElementSelector               => Nil
  }

  def camelize(s: String) = {
    val s2 = s.split("-").map(_.capitalize).mkString
    s2.take(1).toLowerCase + s2.drop(1)
  }

  def defs(reader: Reader): Seq[Tree] = {
    val parser = new CSSOMParser(new SACParserCSS3())
    val rules = parser.parseStyleSheet(new InputSource(reader), null, null).getCssRules
    val classes =
      (0 until rules.getLength)
        .flatMap { i =>
          rules.item(i) match {
            case rule: CSSStyleRule =>
              val selectors = parser.parseSelectors(new InputSource(new StringReader(rule.getSelectorText)))
              (0 until selectors.getLength)
                .flatMap(j => getClasses(selectors.item(j)))
            case _                  =>
              Nil
          }
        }
    val sorted = SortedSet(classes: _*)
    val idents =
      sorted
        .map(s => s -> camelize(s))
        .map {
          case (cls, iden) if iden.charAt(0).isDigit => (cls, s"`$iden`")
          case other                                 => other
        }
    idents
      .toSeq
      .map { case (cls, iden) =>
        LAZYVAL(iden) := THIS DOT "op" APPLY LIT(cls): Tree
      }
  }

  def setClass(clz: Tree) = getModule("^") DOT "cls" INFIX ":=" APPLY clz

  def mkTree(defs: Seq[Tree]) = {
    def op =
      DEFINFER("op")
        .withFlags(Flags.PROTECTED)
        .withParams(PARAM("clz", TYPE_REF(StringClass)): ValDef)

    val `TagOf[N]` = TYPE_REF("TagOf") TYPE_OF TYPE_REF("N")

    val block =
      BLOCK(
        IMPORT("scala.language.implicitConversions"),
        IMPORT("japgolly.scalajs.react.vdom.html_<^._"),
        IMPORT("japgolly.scalajs.react.vdom", "TagOf", "TopNode"),
        OBJECTDEF(moduleName) := BLOCK(
          TRAITDEF("Classes").withTypeParams(TYPEVAR("A")) := BLOCK(
            defs :+ (op.withType(TYPE_REF("A")): Tree)
          ),

          OBJECTDEF("C").withParents(TYPE_REF("Classes") TYPE_OF TYPE_REF("TagMod")) := BLOCK(
            op.withFlags(Flags.OVERRIDE) := setClass(REF("clz"))
          ),

          CLASSDEF("convertableToTagOfExtensionMethods")
            .withTypeParams(TYPEVAR("T"), TYPEVAR("N") UPPER TYPE_REF("TopNode"))
            .withParams(PARAM("self", TYPE_REF("T")), PARAM("toTagOf", TYPE_FUNCTION(TYPE_REF("T"), `TagOf[N]`)))
            .withParents(TYPE_REF("Classes") TYPE_OF `TagOf[N]`) :=
            BLOCK(
              op.withFlags(Flags.OVERRIDE).withType(`TagOf[N]`) :=
                REF("toTagOf") APPLY REF("self") DOT "apply" APPLY setClass(REF("clz"))
            ),

          DEFINFER("convertableToTagOfExtensionMethods")
            .withFlags(Flags.IMPLICIT)
            .withTypeParams(TYPEVAR("T"), TYPEVAR("N") UPPER TYPE_REF("TopNode"))
            .withParams(PARAM("self", TYPE_REF("T")))
            .withParams(PARAM("toTagOf", TYPE_FUNCTION(TYPE_REF("T"), `TagOf[N]`)).withFlags(Flags.IMPLICIT)) :=
            NEW(
              TYPE_REF("convertableToTagOfExtensionMethods").TYPE_OF(TYPE_REF("T"), TYPE_REF("N")),
              REF("self"),
              REF("toTagOf")
            )
        )
      )

    block.inPackage(packageName)
  }

  def apply(): String = treeToString(mkTree(defs(new InputStreamReader(cssUrl.openStream())))) + "\n"
}
