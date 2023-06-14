package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object ZIOAMQPExample extends ZIOAppDefault {

  val channel: ZIO[Scope, Throwable, Channel] = for {
    connection <- Amqp.connect(URI.create("amqp://rabbitmq:rabbitmq@localhost:5672"))
    channel <- Amqp.createChannel(connection)
  } yield channel

  val myApp: ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- channel
        p <- Producer.produce(channel).fork
        c <- Consumer.listen(channel).fork
        _ <- p.zip(c).join
      } yield ()
    }

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    myApp
}
