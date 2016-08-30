package models

import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scalaz._
import Scalaz._
import org.jsoup.{Connection, Jsoup}
import us.codecraft.xsoup.Xsoup

import scala.collection.immutable.IndexedSeq
import scala.util.Try


/**
  * Created by taishi on 8/20/16.
  */
sealed abstract class HttpRequestError(error: String)

case object StatusCodeError extends HttpRequestError("StatusCode Error.")

case object UrlNotExist extends HttpRequestError("このURLは存在しない可能性があります")

case class Node(node: Option[Node], urls: List[String])

case class Config(url: String, UA: String = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36",
                  timeoutMs: Int = 20000, referrer: String = "http://www.google.com", sleepTimeMs: Int = 1000)

class HttpRequest(config: Config) {

  type StatusCodeErrorMessage = Map[HttpRequestError, String]

  implicit class RichDocument(document: Document) {
    def xSelect(xpath: String): Elements = {
      Xsoup.compile(xpath).evaluate(document).getElements
    }

    def links(xPath: String): IndexedSeq[String] = {
      val elements: Elements = Xsoup.compile(xPath).evaluate(document).getElements
      for {
        size <- Range(0, elements.size())
      } yield {
        elements.get(size).absUrl("href")
      }
    }
  }

  val get: \/[StatusCodeErrorMessage, Response] = {
    Thread.sleep(config.sleepTimeMs)
    Try(Jsoup.connect(config.url).maxBodySize(10000000).userAgent(config.UA).timeout(config.timeoutMs).referrer(config.referrer).ignoreHttpErrors(true).ignoreContentType(true).followRedirects(true).execute).toOption match {
      case Some(response) => validation(response)
      case None => -\/(Map(UrlNotExist -> config.url))
    }
  }

  private def validation(response: Response): \/[StatusCodeErrorMessage, Response] = {
    response.statusCode() match {
      case statusCode if (statusCode >= 200 && statusCode < 300 || statusCode == 304) => \/-(response)
      case statusCode => -\/(Map(StatusCodeError -> response.url.getPath))
    }
  }

  def scrapeLinks(pagesXpath: String, absUrl: String)(response: Response): List[String] = {
    Jsoup.parseBodyFragment(response.body, absUrl).links(pagesXpath).toList
  }
}
