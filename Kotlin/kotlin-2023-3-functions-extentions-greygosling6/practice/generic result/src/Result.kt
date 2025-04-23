sealed class Result<out S, out E> {
    data class Ok<out S>(val value: S) : Result<S, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()

    companion object {
        fun <S> ok(value: S): Result<S, Nothing> {
            return Ok(value)
        }

        fun <E> error(error: E): Result<Nothing, E> {
            return Error(error)
        }
    }
}

