class SequentialProcessor(private val handler: (String) -> String) : TaskProcessor {
    override suspend fun process(argument: String): String {
        TODO("equivalent of `return handler(argument)`, but sequential")
    }
}
