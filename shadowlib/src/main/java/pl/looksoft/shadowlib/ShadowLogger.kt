package pl.looksoft.shadowlib

interface ShadowsLogger {
    fun log(text: String)
}

val EmptyLogger = object : ShadowsLogger {
    override fun log(text: String) {
    }
}