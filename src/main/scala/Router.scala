import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

trait Router {
  def route: Route
}

class TodoRouter(todoRepository: TodoRepository) extends Router with CustomDirectives {
  import io.circe.generic.auto._

  def route: Route = pathPrefix("todos") {
    get {
      handleFailureWithDefault(todoRepository.all()) { todos =>
        complete(todos)
      }
    } ~ post {
      entity(as[CreateTodo]) { createTodo =>
        validateTodo(createTodo) {
          handleFailureWithDefault(todoRepository.save(createTodo)) { todo =>
            complete(todo)
          }
        }
      }
    } ~ path("done") {
      handleFailureWithDefault(todoRepository.done()) { todos =>
        complete(todos)
      }
    } ~ path("pending") {
      handleFailureWithDefault(todoRepository.pending()) { todos =>
        complete(todos)
      }
    } ~ path(Segment) { id =>
      put {
        entity(as[UpdateTodo]) { updateTodo =>
          validateTodo(updateTodo) {
            handleFailureWithNotFound(todoRepository.update(id, updateTodo)) { todo =>
              complete(todo)
            }
          }
        }
      } ~ delete {
        handleFailureWithNotFound(todoRepository.delete(id)) { todo =>
          complete(todo)
        }
      }
    }
  }

}