import zio._
import zio.http._
import zio.Console._

object HttpServer {
  private val PORT = sys.env.getOrElse("HTTP_SERVER_PORT", "8080").toInt

  private val healthCheck: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] { case Method.GET -> Root / "health" =>
      Response.text("OK")
    }

  private val app: App[Any] =
    Http.collect[Request] { case Method.GET -> Root / "hello" =>
      Response.text("Hello World!")
    }

  val run = for {
    _ <- printLine(s"Server is running on port \$PORT")
    _ <- Server.serve(healthCheck ++ app).provide(Server.default)
  } yield ()
}
