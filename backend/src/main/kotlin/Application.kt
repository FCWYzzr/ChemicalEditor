package indi.muxin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.Serializable

@Serializable
data class LLMConfig(
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
    val llm: LLMConfig,
    val asr: ASRConfig
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}
