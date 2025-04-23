import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class)
class SequentialProcessor(private val handler: (String) -> String) : TaskProcessor {
    @OptIn(DelicateCoroutinesApi::class)
    private val singleThreadContext = newSingleThreadContext("SequentialThread")

    override suspend fun process(argument: String): String =
        withContext(singleThreadContext) {
            handler(argument)
        }
}
