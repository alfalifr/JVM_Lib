package sidev.lib.jvm.tool.`fun`

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLConnection
import java.nio.charset.Charset

fun URLConnection.responseStr(charset: Charset = Charset.defaultCharset()): String {
    val in_= BufferedReader(InputStreamReader(inputStream, charset))

    var res= ""
    var lineStr: String?
    while(in_.readLine().also { lineStr = it } != null){
        res += "$lineStr\n"
    }
    return res
}

fun URLConnection.responseByteArray(): ByteArray = inputStream.readAllBytes()