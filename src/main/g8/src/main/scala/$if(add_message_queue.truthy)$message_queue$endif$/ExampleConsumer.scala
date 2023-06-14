package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object ExampleConsumer {

  def listen(channel: Channel): ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- ZIO.succeed(channel)
        consumer: ZIO[Any, Throwable, Unit] =
          channel
            .consume(
              queue = QueueName("my_queue"),
              consumerTag = ConsumerTag("my_consumer")
            )
            .mapZIO { record =>
              val deliveryTag = record.getEnvelope.getDeliveryTag
              Console.printLine(
                s"Received \$deliveryTag: \${new String(record.getBody)}"
              ) *>
                channel.ack(DeliveryTag(deliveryTag))
            }
            .runDrain
        c <- consumer
      } yield ()
    }

}
