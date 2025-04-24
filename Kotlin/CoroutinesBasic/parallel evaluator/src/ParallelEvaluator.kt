import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

class ParallelEvaluator {
    suspend fun run(
        task: Task,
        n: Int,
        context: CoroutineContext,
    ) {
        try {
            coroutineScope {
                (0 until n).forEach { i ->
                    launch(context) {
                        task.run(i)
                    }
                }
            }
        } catch (e: Exception) {
            throw TaskEvaluationException(e)
        }
    }
}
