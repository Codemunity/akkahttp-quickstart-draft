import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterDeleteSpec extends WordSpec with Matchers with ScalatestRouteTest {

  private val todos = Seq(
    Todo("id1", "title1", "desc1", done=true),
    Todo("id2", "title2", "desc2", done=false)
  )

  "A TodoRouter" should {

    "return NotFound with non existing todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Delete("/todos/hello") ~> router.route ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "delete existing todos" in {
      val updateId = "id2"
      val todos = Seq(
        Todo("id1", "title1", "desc1", done=true),
        Todo(updateId, "title2", "desc2", done=false)
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Delete(s"/todos/$updateId") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
      }
    }

  }

}
