package message_queue

import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object Consumer extends ZIOAppDefault {

  val channel: ZIO[Scope, Throwable, Channel] = for {
    connection <- Amqp.connect(URI.create("amqp://rabbitmq:rabbitmq@localhost:5672"))
    channel <- Amqp.createChannel(connection)
  } yield channel

  val myApp: ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- channel
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

  override def run: ZIO[Any, Throwable, Unit] =
    myApp
}
