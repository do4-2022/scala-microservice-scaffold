package message_queue

import com.rabbitmq.client.Delivery
import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.ZIO

trait Listener {

  val queueName: String
  val consumerName: String
  val callback: (Delivery, Channel) => ZIO[Any, Throwable, Any]

  def listen(channel: Channel): ZIO[Any, Throwable, Unit] =
    ZIO.scoped {
      for {
        channel <- ZIO.succeed(channel)
        queue <- ZIO.succeed(QueueName(queueName))
        consumerTag <- ZIO.succeed(ConsumerTag(consumerName))
        consumer: ZIO[Any, Throwable, Unit] =
          channel
            .consume(
              queue = queue,
              consumerTag = consumerTag
            )
            .mapZIO {
              record => callback(record, channel)
            }
            .runDrain
        c <- consumer
      } yield ()
    }
}
