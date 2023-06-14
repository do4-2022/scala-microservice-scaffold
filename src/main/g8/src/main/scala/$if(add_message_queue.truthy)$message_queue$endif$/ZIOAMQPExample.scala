package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object ZIOAMQPExample extends ZIOAppDefault {

  val channel: ZIO[Scope, Throwable, Channel] = for {
    host <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_HOST", "localhost"))
    port <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_PORT", "5672").toInt)
    user <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_USER", "guest"))
    password <- ZIO.succeed(sys.env.getOrElse("RABBITMQ_PASSWORD", "guest"))
    uri <- ZIO.succeed(URI.create(s"amqp://\$user:\$password@\$host:\$port"))
    connection <- Amqp.connect(uri)
    channel <- Amqp.createChannel(connection)
  } yield channel

  val myApp: ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {

        channel <- channel
        p <- ExampleProducer.produce(channel).fork
        c <- ExampleConsumer.listen(channel).fork
        _ <- p.zip(c).join
      } yield ()
    }

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    myApp
}
