import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterListSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  "A TodoRouter" should {

    "return all the todos" in {
      val todos = Seq(
        Todo("id1", "title1", "desc1", done=true),
        Todo("id2", "title2", "desc2", done=false)
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val respTodos = responseAs[Seq[Todo]]
        respTodos shouldBe todos
      }
    }

    "return all the done todos" in {
      val todos = Seq(
        Todo("id1", "title1", "desc1", done=true),
        Todo("id2", "title2", "desc2", done=false)
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Get("/todos/done") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val respTodos = responseAs[Seq[Todo]]
        respTodos shouldBe todos.filter(_.done)
      }
    }

    "return all the pending todos" in {
      val todos = Seq(
        Todo("id1", "title1", "desc1", done=true),
        Todo("id2", "title2", "desc2", done=false)
      )
      val repository = new InMemoryTodoRepository(todos)

      val router = new TodoRouter(repository)

      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val respTodos = responseAs[Seq[Todo]]
        respTodos shouldBe todos.filterNot(_.done)
      }
    }

  }

}
