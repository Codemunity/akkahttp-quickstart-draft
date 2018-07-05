import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

trait TodoRepository {
  def save(createTodo: CreateTodo): Future[Todo]
  def update(id: String, updateTodo: UpdateTodo): Future[Unit]
  def delete(id: String): Future[Unit]
  def all(): Future[Seq[Todo]]
  def done(): Future[Seq[Todo]]
  def pending(): Future[Seq[Todo]]
}

object TodoRepository {
  case object TodoNotFound extends Throwable
}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq.empty)(implicit ec: ExecutionContext) extends TodoRepository {
  import TodoRepository._

  private var todos: Vector[Todo] = initialTodos.toVector

  override def save(createTodo: CreateTodo): Future[Todo] = Future.successful {
    val id = UUID.randomUUID().toString
    val todo = Todo(id, createTodo.title, createTodo.description, done=false)
    todos = todos :+ todo
    todo
  }

  override def update(id: String, updateTodo: UpdateTodo): Future[Unit] =
    if (!todos.exists(_.id == id)) {
      Future.failed(TodoNotFound)
    }
    else {
      todos = todos.map { t =>
        if (t.id == id) {
          Todo(t.id, updateTodo.title, updateTodo.description, updateTodo.done)
        } else {
          t
        }
      }
      Future.successful()
    }

  override def delete(id: String): Future[Unit] =
    if (!todos.exists(_.id == id)) {
      Future.failed(TodoNotFound)
    }
    else {
      todos = todos.filterNot(_.id == id)
      Future.successful()
    }

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filterNot(_.done))
}