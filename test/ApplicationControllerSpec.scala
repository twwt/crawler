package test

import org.specs2.mutable._
import controllers.{Application, ApplicationTrait}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.Json
import play.api.test._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
object ExampleControllerSpec extends PlaySpecification with Results {

  class TestController() extends Controller with ApplicationTrait

  "Example Page#index" should {
    "should be valid" in {
      val controller = new TestController()
      val result = controller.index().apply(FakeRequest())
      contentAsString(result) must be equalTo "ok"
    }
  }
}