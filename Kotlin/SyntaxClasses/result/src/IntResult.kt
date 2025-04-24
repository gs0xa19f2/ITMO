sealed class IntResult {
    data class Ok(val value: Int) : IntResult()
    data class Error(val reason: String) : IntResult()

    fun getOrDefault(default: Int): Int = if (this is Error) default else (this as Ok).value

    fun getOrNull(): Int? = if (this is Error) null else (this as Ok).value

    fun getStrict(): Int = if (this is Error) throw NoResultProvided(reason) else (this as Ok).value

}

class NoResultProvided(reason: String) : NoSuchElementException(reason)

fun safeRun(unsafe: () -> Int): IntResult = try {
    IntResult.Ok(unsafe())
} catch (e: Exception) {
    IntResult.Error(e.message ?: "unknown message")
}


