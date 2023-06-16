import zio._

$if(add_message_queue.truthy)$
import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import message_queue.*
import java.net.URI
$endif$

$if(add_sql_orm.truthy)$
import sql_orm.services.ExampleService
import sql_orm.domain.ExampleEntity
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import io.getquill.*
import java.util.UUID.randomUUID
$endif$

object Main extends ZIOAppDefault {

  $if(add_message_queue.truthy)$

  val channel: ZIO[Scope, Throwable, Channel] = for {
    host <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_HOST", "localhost"))
    port <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_PORT", "5672").toInt)
    user <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_USER", "guest"))
    password <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_PASSWORD", "guest"))
    uri <- ZIO.succeed(URI.create(s"amqp://\$user:\$password@\$host:\$port"))
    connection <- Amqp.connect(uri)
    channel <- Amqp.createChannel(connection)
  } yield channel

  $endif$

  override def run =
    for {

      $if(add_sql_orm.truthy)$
      _ <- ZIO.serviceWithZIO[ExampleService](es =>
        (
          es.insertOne(randomUUID.toString, 1) <*>
            es.insertOne(randomUUID.toString, 2) <*>
            es.insertOne(randomUUID.toString, 3)
          )
          *> es.updateMany(quote {
          _.value <= 2
        }, 4)
          *> es.deleteMany(quote {
          _ => true
        })
      )
        .provide(
          ExampleService.live,
          Quill.Postgres.fromNamingStrategy(SnakeCase),
          Quill.DataSource.fromPrefix("db.default")
        )
        .debug("Result: ")
      $endif$

      $if(add_http_server.truthy) $
      h <- HttpServer.run.fork
      $endif$
      $if(add_message_queue.truthy) $
      // Run a message queue example
      channel <- channel // Create a channel
      p <- ExampleProducer.produce(channel).fork // Start a producer that will send a message every second
      c <- ExampleListener.listen(channel).fork // Start a consumer that will print the messages it receives
      _ <- p.zip(c).join
      $endif$
      $if(add_http_server.truthy) $
      _ <- h.join
      $endif$
    } yield 0
}
