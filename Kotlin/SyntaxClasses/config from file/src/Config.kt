class Config(fileName: String) {
    private val configValues: Map<String, String> = getConfigValues(fileName)

    operator fun provideDelegate(
        thisRef: Any?,
        property: kotlin.reflect.KProperty<*>
    ): kotlin.properties.ReadOnlyProperty<Any?, String> {
        val key = property.name
        require(configValues.contains(key)) { "Key: $key was not found in the config values" }
        return kotlin.properties.ReadOnlyProperty { _, _ ->
            configValues[key] ?: throw IllegalArgumentException("Invalid config value for key: $key")
        }
    }

    companion object {
        private fun getConfigValues(fileName: String): Map<String, String> {
            val resource = getResource(fileName) ?: throw IllegalArgumentException("Invalid config file: $fileName")

            return resource.bufferedReader().useLines { lines ->
                lines
                    .filter { it.isNotBlank() }
                    .map { s -> s.split("=").map { it.trim() } }
                    .filter { it.size == 2 }
                    .associate { (key, value) -> key to value }
            }
        }
    }
}


