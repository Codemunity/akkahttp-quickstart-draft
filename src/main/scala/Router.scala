import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

trait Router {
  def route: Route
}

class TodoRouter(todoRepository: TodoRepository) extends Router with CustomDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def route: Route = pathPrefix("todos") {
    path("done") {
      handleFailureWithDefault(todoRepository.done()) { todos =>
        complete(todos)
      }
    } ~ path("pending") {
      handleFailureWithDefault(todoRepository.pending()) { todos =>
        complete(todos)
      }
    } ~ get {
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
    } ~ path(Segment) { id =>
      put {
        entity(as[UpdateTodo]) { updateTodo =>
          validateTodo(updateTodo) {
            handleFailureWithNotFound(todoRepository.update(id, updateTodo)) { _ =>
              complete(StatusCodes.OK)
            }
          }
        }
      } ~ delete {
        handleFailureWithNotFound(todoRepository.delete(id)) { _ =>
          complete(StatusCodes.OK)
        }
      }
    }
  }

}