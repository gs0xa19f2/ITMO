operator fun Time.plus(other: Time): Time {
    return Time(
        this.seconds + other.seconds + (this.milliseconds + other.milliseconds) / 1000,
        (this.milliseconds + other.milliseconds) % 1000
    )
}

operator fun Time.minus(other: Time): Time {
    return (this.seconds * 1000L - other.seconds * 1000L + this.milliseconds - other.milliseconds).milliseconds
}

operator fun Time.times(other: Int): Time {
    return Time(
        this.seconds * other + (this.milliseconds * other) / 1000,
        (this.milliseconds * other) % 1000
    )
}

var Int.milliseconds: Time
    get() = Time(this / 1000L, this % 1000)
    set(value) {}

var Int.seconds: Time
    get() = Time(this.toLong(), 0)
    set(value) {}

var Int.minutes: Time
    get() = Time(this.toLong() * 60, 0)
    set(value) {}

var Int.hours: Time
    get() = Time(this.toLong() * 3600, 0)
    set(value) {}

var Long.milliseconds: Time
    get() = Time(this / 1000L, (this % 1000).toInt())
    set(value) {}

var Long.seconds: Time
    get() = Time(this, 0)
    set(value) {}

var Long.minutes: Time
    get() = Time(this * 60, 0)
    set(value) {}

var Long.hours: Time
    get() = Time(this * 3600, 0)
    set(value) {}
