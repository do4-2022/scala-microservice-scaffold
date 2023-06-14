package message_queue

import com.rabbitmq.client.Delivery
import nl.vroste.zio.amqp.*
import nl.vroste.zio.amqp.model.*
import zio.*

import java.net.URI

object ExampleListener extends Listener {

  val queueName = "my_queue"
  val consumerName = "my_consumer"
  
  val callback = (record, channel) => {
    val deliveryTag = record.getEnvelope.getDeliveryTag
    Console.printLine(
      s"Received \$deliveryTag: \${new String(record.getBody)}"
    ) *>
      channel.ack(DeliveryTag(deliveryTag))
  }

}
