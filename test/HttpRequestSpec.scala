import models.{Config, HttpRequest, HttpRequestError, StatusCodeError}
import org.jsoup.Connection.Response
import org.scalatest.{FlatSpec, Matchers}
import scalaz._
import Scalaz._

/**
  * Created by taishi on 8/14/16.
  */
class HttpRequestSpec extends FlatSpec with Matchers {

  val config = Config("http://qiita.com/tags/Scala")

  it should "scrapeList" in {
    val scrapeLinks: List[\/[String, List[String]]] = for {
      tag <- List("Java")
//      tag <- List("Java", "PlayFramework", "Scala")
      httpRequest = new HttpRequest(Config(s"http://qiita.com/tags/$tag"))
      scrape = httpRequest.scrapeLinks("html//article[@class='publicItem tableList_item']//div[@class='publicItem_body']/h2/a","http://qiita.com")(_)
      response <- httpRequest.get
    } yield {
      response.rightMap(scrape)
    }
    scrapeLinks should equal(true)
  }
}