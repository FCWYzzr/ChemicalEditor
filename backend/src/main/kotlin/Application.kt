package indi.muxin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekConfig(
    val apiKey: String
)

@Serializable
data class ASRConfig(
    val sid: String,
    val sk: String,
    val region: String
)

@Serializable
data class Config(
    val deepseek: DeepSeekConfig,
    val asr: ASRConfig
)

fun main() {
    embeddedServer(Netty, 20120, "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}
