import zio._

object Main extends ZIOAppDefault {
  override def run =
    for {
      $if(add_http_server.truthy) $
      _ <- HttpServer.run
      $endif$
    } yield 0
}
