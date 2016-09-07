package controllers

import java.lang.Long
import java.util
import javax.inject.Inject
import java.io.{File, FileInputStream, FileOutputStream}
import com.moandjiezana.toml.Toml
import models.{Config, HttpRequest}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.mvc._
import play.api.data.Forms.{date, default, list, longNumber, nonEmptyText, number, text}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.i18n.MessagesApi
import java.nio.file.{Files, Paths}
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import scala.collection.JavaConversions._
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz._
import Scalaz._

class Application @Inject()(messagesApi: MessagesApi) extends Controller with ApplicationTrait {
  implicit def message(implicit request: RequestHeader) = messagesApi.preferred(request)
}

object Application extends Controller with ApplicationTrait {
}

trait ApplicationTrait {
  this: Controller =>

  val configForm = Form(
    mapping(
      "configs" -> list[Config](
        mapping("url" -> text,
          "xPath" -> text,
          "baseUrl" -> text,
          "UA" -> default(text, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36"),
          "timeoutMs" -> default(number, 20000),
          "referrer" -> default(text, "http://www.google.com"),
          "sleepTimeMs" -> default(number, 1000)
        )(formapply)(formunapply))
    )(formsapply)(formsunapply)
  )

  private def formapply(url: String, xPath: String, baseUrl: String, UA: String, timeoutMs: Int, referrer: String, sleepTimeMs: Int) =
    Config(url, xPath, baseUrl, UA, timeoutMs, referrer, sleepTimeMs)

  private def formsapply(configs: List[Config]) = configs

  private def formunapply(n: Config) = Some((n.url, n.xPath, n.baseUrl, n.UA, n.timeoutMs, n.referrer, n.sleepTimeMs))

  private def formsunapply(n: List[Config]) = Some((n))

  def index() = Action {
    Ok(views.html.main("test", configForm))
  }

  def upload() = Action {
    Ok(views.html.upload())
  }

  def faceUploadForm() = Action {
    Ok(views.html.faceUpload())
  }

  def postUpload = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      val toml: Toml = new Toml().read(picture.ref.file)
      val url: String = toml.getString("url")
      val baseUrl: String = toml.getString("baseUrl")
      val xPathes: util.List[String] = toml.getList("xPath.list")
      val UA: String = toml.getString("UA")
      val timeoutMs: Int = toml.getLong("timeoutMs").toInt
      val referrer: String = toml.getString("referrer")
      val sleepTimeMs: Int = toml.getLong("sleepTimeMs").toInt
      val configs: List[Config] = xPathes.map(xpath => Config(url, xpath, baseUrl, UA, timeoutMs, referrer, sleepTimeMs)).toList
      foldCrawl(configs)
      Ok("Success")
    }.getOrElse {
      println("fail")
      Ok("Fail")
    }
  }

  sealed abstract class FileUploadError(val message: String)

  case object TooLongFileName extends FileUploadError("ファイル名が長すぎます")

  case object NotNumberFileName extends FileUploadError("ファイル名が数値ではありません")

  def faceUpload = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      val file = picture.ref.file
      val fileName = picture.filename
      def rename(fileName: String, file: File): \/[FileUploadError, Boolean] = {
        val extension = fileName.split("""\.""").last
        println(fileName)
        val ignoreExtensionFileName = fileName.split("""\.""").init.mkString("")
        ignoreExtensionFileName match {
          case s if 100 < s.length => -\/(TooLongFileName)
          case s if !Try(s.toInt).toOption.isDefined =>
            println(s)
            -\/(NotNumberFileName)
          case s =>
            val mills = new DateTime().getMillis().toString
            \/-(Try(file.renameTo(new File(s"faceImages/${mills}_|-|_${s.toInt}.$extension"))).getOrElse(false))
        }
      }
      rename(fileName, file).fold(e => Ok(views.html.index(List(e.message))), b => Ok(views.html.index(List(b.toString))))
    }.getOrElse {
      BadRequest(views.html.index(List("fail")))
    }
  }

  def foldCrawl(configs: List[Config], depth: Int = 0): Unit = {
    for {
      config <- configs
      httpRequest = new HttpRequest(config)
      scrape = HttpRequest.scrapeLinks(config.xPath, config.baseUrl)(_)
    } yield {
      httpRequest.get.rightMap(scrape).fold(e => e.toString, urls => foldCrawl(urls.map(u => config.copy(url = u)), depth + 1))
    }
  }

  def submit = Action.async { implicit request =>
    configForm.bindFromRequest.fold(
      formWithErrors => {
        Future(BadRequest(views.html.main(formWithErrors.toString, formWithErrors)))
      },
      configs => {
        foldCrawl(configs)
        Future(Ok(views.html.index(List("a"))))
      }
    )
  }
}