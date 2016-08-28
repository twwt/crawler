//import controllers.Application
//import org.specs2.mutable._
//import org.specs2.runner._
//import org.junit.runner._
//import play.api.mvc.{Result, Results}
//import play.api.test._
//import play.api.test.Helpers._
//
//import scala.concurrent.Future
//
///**
//  * Add your spec here.
//  * You can mock out a whole application including requests, plugins etc.
//  * For more information, consult the wiki.
//  */
//@RunWith(classOf[JUnitRunner])
//class ApplicationSpec extends PlaySpecification with Results{
//
//  "Application" should {
//    class TestController extends Application
//
//    "Example Page#index" should {
//      "should be valid" in {
//        val controller = new TestController()
//        val result: Future[Result] = controller.index().apply(FakeRequest())
//        val bodyText: String = contentAsString(result)
//        bodyText must be equalTo "ok"
//      }
//    }
//
//    "send 404 on a bad request" in new WithApplication {
//      route(FakeRequest(GET, "/boum")) must beNone
//    }
//
//    "render the index page" in new WithApplication {
//      val home = route(FakeRequest(GET, "/")).get
//
//      status(home) must equalTo(OK)
//      contentType(home) must beSome.which(_ == "text/html")
//      contentAsString(home) must contain("Your new application is ready.")
//    }
//  }
//}
