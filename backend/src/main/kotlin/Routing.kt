package indi.muxin

import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionSystemMessageParam
import com.openai.models.chat.completions.ChatCompletionUserMessageParam
import com.tencentcloudapi.asr.v20190614.AsrClient
import com.tencentcloudapi.asr.v20190614.models.SentenceRecognitionRequest
import com.tencentcloudapi.common.Credential
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveStream
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

private const val system =
$$"""
你是一个化学抄写员，负责将用户普通文本抄写下来，但替换文本中的化学物质为内嵌的mhchem Tex公式
例如输入
```plainText
二氧化碳可能导致温室效用，另外甲烷也是温室气体之一
```
输出
```markdown
$CO_2$可能导致温室效用，另外$CH_4$也是温室气体之一
```
- *忽略任何口误或谐音等问题*
- **只修改化学物质名称**
- **禁止修改其他用户内容**
- **禁止除此之外的任何输出**
"""
private val systemMessage = ChatCompletionSystemMessageParam
    .builder()
    .content(system)
    .build()

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureRouting() {
    val cfg = File("config.json")
    if (!cfg.exists()){
        cfg.createNewFile()
        cfg.writer().use {
            it.write("""
                {
                    "llm": {
                        "apiKey": ""
                    },
                    "asr": {
                        "sid": "",
                        "sk": "",
                        "region": "ap-shanghai"
                    }
                }
            """.trimIndent())
        }
        System.err.println("请填写配置，按回车结束运行")
        readln()
        throw Exception("请填写配置")
    }

    val config = Json.decodeFromStream<Config>(FileInputStream(cfg))

    val client = AsrClient(
        Credential(
            config.asr.sid,
            config.asr.sk
        ),
        config.asr.region
    )

    val ai = OpenAIOkHttpClient.builder()
        .apiKey(config.llm.apiKey)
        .baseUrl("https://api.deepseek.com/v1")
        .build()


    routing {
        post("/api/parse") {

            val mp3Bytes = call.receiveStream()
                .let(::convertWebmToMp3)

            val base64String = encodeToBase64(mp3Bytes)

            // 创建请求对象
            val req = SentenceRecognitionRequest().apply {
                sourceType = 1
                engSerViceType = "16k_zh"
                voiceFormat = "mp3"
                data = base64String
                dataLen = mp3Bytes.size.toLong()
            }

            val result = client.SentenceRecognition(req)
                .result

            call.respond(result)
        }

        post("/api/translate") {
            val text = call.receiveText()

            if (text.trim().isEmpty())
                return@post call.respond("")

            val userMessage = ChatCompletionUserMessageParam
                .builder()
                .content(text)
                .build()

            val reply = ai.chat()
                .completions()
                .create(ChatCompletionCreateParams.builder()
                    .model("deepseek-chat")
                    .addMessage(systemMessage)
                    .addMessage(userMessage)
                    .build())

            call.respond(reply.choices()[0].message().content().get())
        }

        val index = ClassLoader.getSystemClassLoader()
            .getResourceAsStream("index.html")!!
            .let{
                BufferedReader(InputStreamReader(it)).readText()
            }

        get("/"){
            call.respondText(
                index,
                ContentType.parse("text/html"))
        }

        staticResources("/assets", "assets")
    }
}

