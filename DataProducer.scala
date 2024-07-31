import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object DataProducer extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:29092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val topic = "real-time-data"

  for (i <- 1 to 1000) {
    val key = s"key-$i"
    val value = s"value-$i"
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record)
    Thread.sleep(1000) // Simulate real-time data with a delay
  }

  producer.close()
}