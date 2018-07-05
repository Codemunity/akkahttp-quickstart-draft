import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await

object Main extends App {
  import scala.concurrent.duration._

  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todoapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val repository = new InMemoryTodoRepository()
  val router = new TodoRouter(repository)
  val server = new Server(router, host, port)

  Await.result(server.bind(), 3.seconds)
}
