package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object Producer{

  def produce(channel: Channel): ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- ZIO.succeed(channel)
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
}
