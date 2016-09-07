import models.{Config, HttpRequest, HttpRequestError, StatusCodeError}
import org.jsoup.Connection.Response
import org.scalatest.{FlatSpec, Matchers}
import scalaz._
import Scalaz._

/**
  * Created by taishi on 8/14/16.
  */
class HttpRequestSpec extends FlatSpec with Matchers {

  //  it should "scrapeList fail UrlNotExist" in {
  //    val scrapeLinks: List[\/[Map[models.HttpRequestError, String], List[String]]] = for {
  //      tag <- List("Java")
  //      //      tag <- List("Java", "PlayFramework", "Scala")
  //      httpRequest = new HttpRequest(Config(s"http://qiiasdhfajksldfjahsdlfjasta.com/tags/$tag"))
  //      scrape = httpRequest.scrapeLinks("html//article[@class='publicItem tableList_item']//div[@class='publicItem_body']/h2/a", "http://qiita.com")(_)
  //    } yield {
  //      httpRequest.get.rightMap(scrape)
  //    }
  //    scrapeLinks should equal(false)
  //  }
  //  it should "scrapeList fail StatusCodeError" in {
  //    val scrapeLinks: List[\/[Map[models.HttpRequestError, String], List[String]]] = for {
  //      tag <- List("Javaasdfasdfdasdf")
  //      //      tag <- List("Java", "PlayFramework", "Scala")
  //      httpRequest = new HttpRequest(Config(s"http://qiita.com/tags/$tag"))
  //      scrape = httpRequest.scrapeLinks("html//article[@class='publicItem tableList_item']//div[@class='publicItem_body']/h2/a", "http://qiita.com")(_)
  //    } yield {
  //      httpRequest.get.rightMap(scrape)
  //    }
  //    scrapeLinks should equal(false)
  //  }
  //  it should "Restart用、get error url List" in {
  //    val scrapeLinks: List[\/[Map[models.HttpRequestError, String], List[String]]] = for {
  //      tag <- List("Haskell", "Javaasdfasdfdasdf", "Scalaasdfasdfdasdf", " JacaScriptasdfasdfdasdf", "Scala")
  //      httpRequest = new HttpRequest(Config(s"http://qiita.com/tags/$tag"))
  //      scrape = httpRequest.scrapeLinks("html//article[@class='publicItem tableList_item']//div[@class='publicItem_body']/h2/a", "http://qiita.com")(_)
  //    } yield {
  //      httpRequest.get.rightMap(scrape)
  //    }
  //    scrapeLinks.filter(_.isLeft) should equal(false)
  //  }

  it should "scrapeList success" in {
    val scrapeLinks: List[\/[Map[models.HttpRequestError, String], List[String]]] = for {
      tag <- List("Scala")
      httpRequest = new HttpRequest(Config(s"http://qiita.com/tags/$tag"))
      scrape = httpRequest.scrapeLinks("html//article[@class='publicItem tableList_item']//div[@class='publicItem_body']/h2/a", "http://qiita.com")(_)
    } yield {
      httpRequest.get.rightMap(scrape)
    }
    scrapeLinks should equal(false)
  }
}