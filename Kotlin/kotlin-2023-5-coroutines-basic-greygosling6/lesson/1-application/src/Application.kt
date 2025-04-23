import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*

fun CoroutineScope.runApplication(
    runUI: suspend () -> Unit,
    runApi: suspend () -> Unit,
): List<Job> {
    val apiJob =
        launch {
            while (isActive) {
                try {
                    runApi()
                    break
                } catch (e: Exception) {
                    delay(1.seconds)
                }
            }
        }

    val uiJob =
        launch {
            try {
                runUI()
            } catch (e: Exception) {
                apiJob.cancel()
                throw e
            }
        }

    return listOf(apiJob, uiJob)
}
