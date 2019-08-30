import java.net.URL

import scala.collection.JavaConverters._
import scala.collection.immutable.SortedSet

import com.helger.commons.collection.impl.ICommonsList
import com.helger.commons.io.IHasReader
import com.helger.css.decl._
import com.helger.css.reader.{CSSReader, CSSReaderSettings}


object CssExtractor {
  def getClassesFromSelectors(sels: ICommonsList[CSSSelector]): Iterator[String] =
    sels.iterator().asScala.flatMap(_.getAllMembers.iterator().asScala)
      .flatMap {
        case n: CSSSelectorMemberNot                 => getClassesFromSelectors(n.getAllSelectors)
        case s: CSSSelectorSimpleMember if s.isClass => Iterator(s.getValue.stripPrefix("."))
        case _                                       => Iterator.empty
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
