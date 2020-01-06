import java.net.URL

import scala.collection.JavaConverters._
import scala.collection.immutable.SortedSet

import com.helger.commons.collection.impl.ICommonsList
import com.helger.commons.io.IHasReader
import com.helger.css.decl._
import com.helger.css.reader.{CSSReader, CSSReaderSettings}


object CssExtractor {
  private val hex = (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  def unescape(s: String) = {
    def loop(ch: List[Char]): List[Char] = ch match {
      case Nil          => Nil
      case '\\' :: rest =>
        val (digits, beyond) = rest.span(hex)
        val c = Integer.parseInt(digits.mkString, 16).toChar
        beyond match {
          case '\r' :: '\n' :: more        => c :: loop(more)
          case w :: more if w.isWhitespace => c :: loop(more)
          case more                        => c :: loop(more)
        }
      case c :: rest    => c :: loop(rest)
    }

    loop(s.toList).mkString
  }

  def unquote(s: String) = s.stripPrefix("\"").stripSuffix("\"")

  def getClassesFromSelectors(sels: ICommonsList[CSSSelector]): Iterator[String] =
    sels.iterator().asScala.flatMap(_.getAllMembers.iterator().asScala)
      .flatMap {
        case n: CSSSelectorMemberNot                             => getClassesFromSelectors(n.getAllSelectors)
        case s: CSSSelectorSimpleMember if s.isClass             => Iterator(unescape(s.getValue.stripPrefix(".")))
        case a: CSSSelectorAttribute if a.getAttrName == "class" =>
          val v = unquote(a.getAttrValue)
          if (v.endsWith("-")) Iterator.empty else Iterator(v)
        case _                                                   => Iterator.empty
      }

  def getClassesFromRules(rules: ICommonsList[ICSSTopLevelRule]): Iterator[String] =
    rules.iterator().asScala.flatMap {
      case r: CSSStyleRule => getClassesFromSelectors(r.getAllSelectors)
      case m: CSSMediaRule => getClassesFromRules(m.getAllRules)
      case _               => Iterator.empty
    }

  def getClassesFromSheet(sheet: CascadingStyleSheet): Iterator[String] =
    getClassesFromRules(sheet.getAllRules)

  def getClassesFromURL(url: URL): SortedSet[String] = {
    val reader: IHasReader = () => new java.io.InputStreamReader(url.openStream())
    val sheet = CSSReader.readFromReader(reader, new CSSReaderSettings())
    SortedSet(getClassesFromSheet(sheet).toSeq: _*)
  }
}
