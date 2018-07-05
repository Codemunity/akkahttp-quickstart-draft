object TodoValidator {

  sealed trait ValidationResult
  case object Valid extends ValidationResult
  final case class Invalid(message: String) extends ValidationResult

  def validate(todo: TodoLike): ValidationResult =
    if (todo.title.isEmpty) {
    Invalid("Title can not be empty.")
  } else if (todo.description.isEmpty) {
    Invalid("Description can not be empty.")
  } else {
    Valid
  }
}
