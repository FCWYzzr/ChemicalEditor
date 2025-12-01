package indi.muxin


import java.io.File
import java.util.Base64
import ws.schild.jave.Encoder
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes
import java.io.FileOutputStream
import java.io.InputStream

// 音频格式转换函数：使用JAVE库将webm格式转换为mp3
fun convertWebmToMp3(webmData: InputStream): ByteArray {
    val tempInput = File.createTempFile("audio", ".webm")
    val tempOutput = File.createTempFile("audio", ".mp3")

    webmData.transferTo(FileOutputStream(tempInput))

    val encoder = Encoder()
    encoder.encode(MultimediaObject(tempInput), tempOutput, EncodingAttributes().apply{
        setInputFormat("webm")
        setOutputFormat("mp3")
        setAudioAttributes(AudioAttributes().apply {
            setBitRate(128000)
            setChannels(2)
            setSamplingRate(16000)
        })
    })
    return tempOutput.readBytes()
}

// Base64编码函数
fun encodeToBase64(data: ByteArray): String {
    return Base64.getEncoder().encodeToString(data)
}