trait TodoLike {
  def title: String
  def description: String
}

case class CreateTodo(title: String, description: String) extends TodoLike
case class UpdateTodo(title: String, description: String, done: Boolean) extends TodoLike
case class Todo(id: String, title: String, description: String, done: Boolean) extends TodoLike
