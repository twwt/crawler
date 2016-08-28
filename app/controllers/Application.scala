package controllers

import javax.inject.Inject
import models.Config
import play.api.data.Form
import play.api.mvc._
import play.api.data.Forms.mapping
import play.api.data.Forms.{date, default, longNumber, nonEmptyText, number, text}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.i18n.MessagesApi
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application @Inject()(messagesApi: MessagesApi) extends Controller with ApplicationTrait {
  implicit def message(implicit request: RequestHeader) = messagesApi.preferred(request)
}

object Application extends Controller with ApplicationTrait {
}

trait ApplicationTrait {
  this: Controller =>

  val configForm = Form(
    mapping(
      "url" -> text,
      "UA" -> default(text, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36"),
      "timeoutMs" -> default(number, 20000),
      "referrer" -> default(text, "http://www.google.com"),
      "sleepTimeMs" -> default(number, 1000)
    )(formapply)(formunapply)
  )

  private def formapply(url: String, UA: String, timeoutMs: Int, referrer: String, sleepTimeMs: Int) =
    Config(url, UA, timeoutMs, referrer, sleepTimeMs)

  private def formunapply(n: Config) = Some((n.url, n.UA, n.timeoutMs, n.referrer, n.sleepTimeMs))

  def index() = Action {
    Ok(views.html.main("test", configForm))
  }

  def submit = Action.async { implicit request =>
    configForm.bindFromRequest.fold(
      formWithErrors => {
        println(formWithErrors)
        Future(BadRequest(views.html.main(formWithErrors.toString, formWithErrors)))
      },
      config => {
        println(config)
        Future(Ok(views.html.index("Success")))
      }
    )
  }
}