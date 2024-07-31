import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, FileIO}
import akka.stream.Materializer
import akka.util.ByteString
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import scala.io.StdIn
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object FileConsumer extends App {
  implicit val system: ActorSystem = ActorSystem("FileConsumerSystem")
  implicit val materializer: Materializer = Materializer(system)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:29092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val kafkaSource = Consumer
    .plainSource(consumerSettings, Subscriptions.topics("real-time-data"))
    .map(_.value)
  
  // Define a file path to write the messages to
  val filePath = Paths.get("kafka-messages.txt")

  // Define the Sink to write messages to the file
  val fileSink = Sink.foreach[String] { message =>
    val messageToWrite = message + "\n" // Add a newline character after each message
    val bytes = ByteString(messageToWrite)
    val fileWriter = FileIO.toPath(filePath, Set(StandardOpenOption.CREATE, StandardOpenOption.APPEND))
    akka.stream.scaladsl.Source.single(bytes).runWith(fileWriter)
  }

  // Run the source with the defined sink
  kafkaSource.runWith(fileSink)

  println("Consumer started and writing to file. Press Enter to exit...")
  StdIn.readLine() // Keeps the application running until Enter is pressed

  system.terminate()
}
