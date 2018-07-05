import TodoValidator.{Invalid, Valid}
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive0, Directive1, Directives}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait CustomDirectives extends Directives {

  def validateTodo[T <: TodoLike](todo: T): Directive0 =
    TodoValidator.validate(todo) match {
      case Valid =>
      case Invalid(message) =>
        complete(StatusCodes.BadRequest, message)
    }

  def handleFailure[T]
    (e: PartialFunction[Throwable, ApiError])
    (f: => Future[T])
    (implicit m: ToResponseMarshaller[T]): Directive1[T] = onComplete(f) flatMap {
    case Success(t) => provide(t)
    case Failure(error) =>
      val apiError = e(error)
      complete(apiError.statusCode, apiError.message)
  }

  def handleFailureWithDefault[T]: Future[T] => Directive1[T] =
    handleFailure[T] {
      case _ => ApiError.generic
    }

}
