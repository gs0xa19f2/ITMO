import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<Cutoff>.resultsFlow(): Flow<Results> {
    var currentResults = Results(emptyMap())
    return map { newCutoff ->
        currentResults = Results(currentResults.results + (newCutoff.number to newCutoff.time))
        currentResults
    }
}
