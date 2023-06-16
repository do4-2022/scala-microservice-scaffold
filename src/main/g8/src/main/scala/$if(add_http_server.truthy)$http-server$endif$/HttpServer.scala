import zio._
import zio.http._
import zio.Console._

object HttpServer {
  private val PORT = sys.env.getOrElse("HTTP_SERVER_PORT", "8080").toInt

  private val healthCheck: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] { case Method.GET -> Root / "health" =>
      Response.json("{\"status\": \"ok\"}")
    }

  val run = for {
    _ <- printLine(s"Server is running on port \$PORT")
    _ <- Server.serve(healthCheck ++ User.routes).provide(Server.default)
  } yield ()
}
