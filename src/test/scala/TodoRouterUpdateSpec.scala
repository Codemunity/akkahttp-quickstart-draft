import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterUpdateSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val todos = Seq(
    Todo("id1", "title1", "desc1", done=true),
    Todo("id2", "title2", "desc2", done=false)
  )

  "A TodoRouter" should {

    "reject invalid todos" in {
      val updateTodo = UpdateTodo(
        "",
        "",
        done = true
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Put("/todos/id2", updateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    "return NotFound with non existing todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val updateTodo = UpdateTodo(
        "new title",
        "description",
        done = true
      )
      val router = new TodoRouter(repository)

      Put("/todos/hello", updateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "update valid todos" in {
      val updateId = "id2"
      val updateTodo = UpdateTodo(
        "new title",
        "description",
        done = true
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Put(s"/todos/$updateId", updateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

  }

}
