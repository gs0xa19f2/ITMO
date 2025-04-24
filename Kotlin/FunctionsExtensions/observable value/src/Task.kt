class Observable<T>(initial: (T) -> Unit) {

    private var listener: ((T) -> Unit)? = initial

    fun invoke(value: T) {
        listener?.invoke(value)
    }

    fun cancel() {
        listener = null
    }
}

interface Value<T> {

    var value: T

    fun observe(listener: (T) -> Unit): Observable<T>
}

class MutableValue<T>(initial: T) : Value<T> {

    private val listeners = mutableListOf<Observable<T>>()

    override var value: T = initial
        set(value) {
            field = value
            listeners.forEach {
                try {
                    it.invoke(value)
                } catch (e: Exception) {
                    println("Callback exception: ${e.message}")
                }
            }
        }

    override fun observe(listener: (T) -> Unit): Observable<T> {
        return Observable(listener).also {
            try {
                it.invoke(value)
            } catch (e: Exception) {
                println("Callback exception: ${e.message}")
            }
            listeners.add(it)
        }
    }
}

fun main() {
    val mutableValue = MutableValue(initial = "initial")
    val cancellation = mutableValue.observe { println(it) }

    mutableValue.value = "updated"
    cancellation.cancel()
    mutableValue.value = "final"
}

