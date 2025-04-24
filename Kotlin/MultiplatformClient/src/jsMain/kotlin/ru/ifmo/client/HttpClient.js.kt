package ru.ifmo.client

private enum class Platform { Node, Browser }

private val platform: Platform
    get() {
        val hasNodeApi = js(
            """
            (typeof process !== 'undefined'
                && process.versions != null
                && process.versions.node != null) ||
            (typeof window !== 'undefined'
                && typeof window.process !== 'undefined'
                && window.process.versions != null
                && window.process.versions.node != null)
            """
        ) as Boolean
        return if (hasNodeApi) Platform.Node else Platform.Browser
    }

actual fun HttpClient(): HttpClient = when (platform) {
    Platform.Browser -> FetchClient()
    Platform.Node -> NodeFetchClient()
}
