import akka.http.scaladsl.model.{StatusCode, StatusCodes}

// TODO: Make creation out of the companion object impossible
final case class ApiError private(statusCode: StatusCode, message: String)

object ApiError {
  val generic: ApiError = ApiError(StatusCodes.InternalServerError, "Unknown error.")
  val todoNotFound: ApiError = new ApiError(StatusCodes.NotFound, "Todo not found.")
}
