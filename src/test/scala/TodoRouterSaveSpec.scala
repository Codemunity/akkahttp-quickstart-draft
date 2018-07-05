import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterSaveSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  "A TodoRouter" should {

    "reject invalid todos" in {
      val createTodo = CreateTodo("", "")
      val repository = new InMemoryTodoRepository()

      val router = new TodoRouter(repository)

      Post("/todos", createTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "save valid todos" in {
      val createTodo = CreateTodo("title", "description")
      val repository = new InMemoryTodoRepository()

      val router = new TodoRouter(repository)

      Post("/todos", createTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val todo = responseAs[Todo]
        todo.title shouldBe createTodo.title
        todo.description shouldBe createTodo.description
      }
    }

  }

}
