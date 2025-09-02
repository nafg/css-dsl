import java.io.{File, StringReader}
import java.net.URL

import scala.collection.JavaConverters.*
import scala.collection.immutable.SortedSet
import scala.io.Source
import scala.util.{Failure, Success, Try}

import com.helger.commons.collection.impl.ICommonsList
import com.helger.commons.io.IHasReader
import com.helger.css.decl.*
import com.helger.css.reader.{CSSReader, CSSReaderSettings}


object CssExtractor {
  private val hex = (('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')).toSet

  private object Unicode {
    def unapply(chars: List[Char]): Option[(Char, List[Char])] = {
      val (first6, further) = chars.splitAt(6)
      val (digits, beyond) = first6.span(hex)
      if (digits.isEmpty)
        None
      else {
        val c = Integer.parseInt(digits.mkString, 16).toChar
        val rest = beyond ++ further
        val more =
          rest match {
            case '\r' :: '\n' :: more        => more
            case w :: more if w.isWhitespace => more
            case more                        => more
          }
        Some((c, more))
      }
    }
  }
  //\\[^\r\n\f0-9a-f]
  val unesapable = Set('\r', '\n', '\f') ++ ((0 to 9).map(_.toChar) ++ ('a' to 'f') ++ ('A' to 'F'))

  def unescape(s: String) = {
    def loop(ch: List[Char]): List[Char] = ch match {
      case Nil                                 => Nil
      case '\\' :: Unicode(c, beyond)          => c :: loop(beyond)
      case '\\' :: c :: rest if !unesapable(c) => c :: loop(rest)
      case c :: rest                           => c :: loop(rest)
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
          val attrValue = a.getAttrValue
          if (attrValue == null) Iterator.empty
          else {
            val v = unquote(attrValue)
            if (v.endsWith("-")) Iterator.empty else Iterator(v)
          }
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

  def getLocalCssFallback(url: URL): Option[String] = {
    val urlString = url.toString
    val fallbackFile = urlString match {
      case s if s.contains("fontawesome-free") || s.contains("font-awesome") => "fontawesome.css"
      case s if s.contains("bootstrap") && (s.contains("/3.") || s.contains("stackpath.bootstrapcdn.com")) => "bootstrap3.css" 
      case s if s.contains("bootstrap") && s.contains("@4.") => "bootstrap4.css"
      case s if s.contains("bootstrap") && s.contains("@5.") => "bootstrap5.css"
      case s if s.contains("bulma") => "bulma.css"
      case s if s.contains("semantic-ui") => "semanticui.css"
      case s if s.contains("fomantic-ui") => "fomanticui.css"
      case _ => return None
    }
    
    val cacheFile = new File(s"project/css-cache/$fallbackFile")
    if (cacheFile.exists()) {
      Try(Source.fromFile(cacheFile).mkString) match {
        case Success(content) => Some(content)
        case Failure(_) => None
      }
    } else {
      None
    }
  }

  def getClassesFromURL(url: URL): SortedSet[String] = {
    // First try to download from the actual URL
    Try {
      val reader: IHasReader = () => new java.io.InputStreamReader(url.openStream())
      val sheet = CSSReader.readFromReader(reader, new CSSReaderSettings())
      SortedSet(getClassesFromSheet(sheet).toSeq *)
    } match {
      case Success(classes) => classes
      case Failure(exception) =>
        println(s"Failed to download CSS from $url: ${exception.getMessage}")
        // Fall back to local cached CSS
        getLocalCssFallback(url) match {
          case Some(cssContent) =>
            println(s"Using local fallback CSS for $url")
            val reader: IHasReader = () => new StringReader(cssContent)
            val sheet = CSSReader.readFromReader(reader, new CSSReaderSettings())
            SortedSet(getClassesFromSheet(sheet).toSeq *)
          case None =>
            println(s"No local fallback available for $url, returning empty set")
            SortedSet.empty[String]
        }
    }
  }
}
