import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.Materializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn
import java.util.concurrent.CountDownLatch

object StreamProcessor extends App {
  implicit val system: ActorSystem = ActorSystem("ReactiveStreamProcessing")
  implicit val materializer: Materializer = Materializer(system)

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:29092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // Create an InfluxDB client for InfluxDB 1.x
  val influxDB: InfluxDB = InfluxDBFactory.connect("http://localhost:8086", "username", "password")
  influxDB.setDatabase("mydatabase") // Set the database

  val kafkaSource: Source[String, Consumer.Control] = Consumer
    .plainSource(consumerSettings, Subscriptions.topics("real-time-data"))
    .map(_.value)

  val processingFlow = kafkaSource
    .map { value =>
      // Perform analytics here
      val processedValue = s"Processed: $value"
      
      // Write to InfluxDB
      val point = Point.measurement("analytics")
        .addField("value", processedValue)
        .build()
      influxDB.write(point)
      
      processedValue
    }
    .to(Sink.foreach(println))

  processingFlow.run()

  // Keep the application running
  println("Stream processing started. Press Enter to exit...")
  val latch = new CountDownLatch(1)
  
  // Add a shutdown hook to close resources properly
  sys.addShutdownHook {
    println("Shutting down...")
    system.terminate()
    influxDB.close()
    latch.countDown()
  }

  // Block the main thread
  latch.await()
}
