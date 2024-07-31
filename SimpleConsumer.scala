import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import scala.io.StdIn

object SimpleConsumer extends App {
  implicit val system: ActorSystem = ActorSystem("SimpleConsumerSystem")
  implicit val materializer: Materializer = Materializer(system)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:29092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val kafkaSource = Consumer
    .plainSource(consumerSettings, Subscriptions.topics("real-time-data"))
    .map(_.value)

  kafkaSource
    .runWith(Sink.foreach(println)) // Simple sink to print messages

  println("Consumer started. Press Enter to exit...")
  StdIn.readLine() // Keeps the application running until Enter is pressed

  system.terminate()
}
