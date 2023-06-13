package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object Producer extends ZIOAppDefault {

  val channel: ZIO[Scope, Throwable, Channel] = for {
    connection <- Amqp.connect(URI.create("amqp://rabbitmq:rabbitmq@localhost:5672"))
    channel <- Amqp.createChannel(connection)
  } yield channel

  val myApp: ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- channel
        producer: ZIO[Any, Throwable, Long] =
          Random.nextUUID
            .flatMap(uuid =>
              channel
                .publish(ExchangeName("my_exchange"), uuid.toString.getBytes)
                .unit
            )
            .schedule(Schedule.spaced(1.seconds))
        p <- producer
      } yield ()
    }

  override def run: ZIO[Any, Throwable, Unit] =
    myApp
}
