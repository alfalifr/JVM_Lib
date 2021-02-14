package sidev.lib.jvm.tool.`fun`

import sidev.lib.`val`.SuppressLiteral
import sidev.lib.exception.IllegalStateExc
import sidev.lib.jvm.tool.`val`.InputStreamReadMode
import java.io.*
import java.net.HttpURLConnection
import java.net.URLConnection
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import kotlin.math.min

val URLConnection.contentLengthLong_: Long
    get()= (getHeaderField("Content-Length") /*?: getHeaderField("content-length")*/)?.toLong() ?: -1

val URLConnection.acceptRange: Boolean
    get()= (getHeaderField("Accept-Ranges") /*?: getHeaderField("accept-ranges")*/).let {
        it != null && !it.equals("none", true)
    }

val URLConnection.requestedFileName: String?
    get()= (getHeaderField("Content-Disposition") /*?: getHeaderField("content-disposition")*/)?.let {
        val keyword= "filename="
        //val len= keyword.length
        val index= it.indexOf(keyword, ignoreCase = true)
        if(index >= 0){
            val begin= it.indexOf('"', index)
            val end= it.indexOf('"', index +1)
            it.substring(begin, end)
        } else null
    }

private fun URLConnection.checkConnection(): Boolean {
    if(this is HttpURLConnection){
        if(responseCode != HttpURLConnection.HTTP_OK) return false
    }
    //`inputStream.available()` tidak bisa dijadikan sbg patokan kalo di Android.
/*
    val inputStream= inputStream
    val available= inputStream.available()
    if(available == 0)
        return inputStream.read()
    return 0
 */
    return true
}

/**
 * Membaca stream byte dari `this.extension` [URLConnection] dala 3 [mode] [InputStreamReadMode].
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.readStreamBufferByte(
    bufferByte: ByteArray, offset: Int = 0, len: Int = bufferByte.size,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
){
    if(!checkConnection()) return

    val contentLen= contentLengthLong_
    var i= 0L
    var readByteLen: Int

    while(inputStream.read(bufferByte, offset, len).also { readByteLen = it } > 0){
        onProgression?.invoke(readByteLen, i++, contentLen)
    }
}
fun URLConnection.readStreamBufferByte(
    bufferLen: Int = 1024,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
) = readStreamBufferByte(ByteArray(bufferLen), 0, bufferLen, onProgression)
/**
 * Membaca stream byte dari `this.extension` [URLConnection] dala 3 [mode] [InputStreamReadMode].
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.saveBufferByteToFile(
    bufferByte: ByteArray,
    fileOutput: File,
    append: Boolean = true,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
) {
    if(!checkConnection()) return
    if(!fileOutput.parentFile!!.exists()) throw IllegalStateExc(
        currentState = "!fileOutput.parentFile.exists()",
        expectedState = "fileOutput.parentFile.exists()",
        detMsg = "`fileOutput.parentFile` (${fileOutput.parentFile}) tidak ada di sistem file"
    )

    val fos= FileOutputStream(fileOutput, append)
    val bos= BufferedOutputStream(fos, bufferByte.size)

    readStreamBufferByte(bufferByte) { readByteLen, current, len ->
        bos.write(bufferByte, 0, readByteLen)
        onProgression?.invoke(readByteLen, current, len)
    }
    bos.close()
}
fun URLConnection.saveBufferByteToFile(
    bufferByte: ByteArray,
    fileOutputName: String,
    append: Boolean = true,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
) = saveBufferByteToFile(bufferByte, File(fileOutputName), append, onProgression)

fun URLConnection.saveBufferByteToFile(
    fileOutput: File,
    append: Boolean = true,
    bufferLen: Int = 1024,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
) = saveBufferByteToFile(ByteArray(bufferLen), fileOutput, append, onProgression)

fun URLConnection.saveBufferByteToFile(
    fileOutputName: String,
    append: Boolean = true,
    bufferLen: Int = 1024,
    onProgression: ((readByteLen: Int, current: Long, len: Long) -> Unit)? = null
) = saveBufferByteToFile(ByteArray(bufferLen), File(fileOutputName), append, onProgression)

/**
 * Membaca stream byte dari `this.extension` [URLConnection] dala 3 [mode] [InputStreamReadMode].
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
private fun URLConnection.readStream_internal(
    charset: Charset?, // = Charset.defaultCharset(),
    mode: InputStreamReadMode,
    offset: Long, //= 0,
    onProgression: (obj: Any?, current: Long, len: Long) -> Unit,
    //autoReset: Boolean = true
) {
    if(!checkConnection()) return

    val inputStream= inputStream

    if(offset > 0) {
        if(!acceptRange) throw IllegalStateExc(
            currentState = "accept-ranges: none",
            expectedState = "accept-ranges: `!none`",
            detMsg = "Url = $url tidak mendukung request konten parsial, offset= $offset"
        )
        setRequestProperty("Range", "bytes=$offset-")
    }

    @Suppress(SuppressLiteral.NAME_SHADOWING)
    val charset = charset ?: run {
        val httpEncoding= contentEncoding
        if(httpEncoding != null) {
            try { Charset.forName(contentEncoding) }
            catch (e: UnsupportedCharsetException) { null }
        } else null
    } ?: Charset.defaultCharset()
    val len = contentLengthLong_

    var i= 0L

    //val sb= StringBuilder()
    when(mode){
        InputStreamReadMode.LINE, InputStreamReadMode.CHAR -> {
            val in_= BufferedReader(InputStreamReader(inputStream, charset))
            //InputStreamReader(inputStream, charset).read()
            if(mode == InputStreamReadMode.LINE){
                var lineStr: String?
                while(in_.readLine().also { lineStr = it } != null){
                    //sb += "$lineStr\n"
                    //prine("lineStr= $lineStr")
                    onProgression(lineStr, i++, len)
                }
            } else {
                var char: Char?
                while(in_.read().also { char = it.toChar() } >= 0){
                    //sb += char!!
                    //prine("char= $char")
                    onProgression(char, i++, len)
                }
            }
        }
        InputStreamReadMode.BYTE -> {
            //val bytes= ArrayList<Int>(8192 * 8)
            var byte: Int
            while(inputStream.read().also { byte= it } >= 0){
                //bytes += byte
                //sb += byte
                //prine("byte= $byte")
                onProgression(byte, i++, len)
            }
            //val arr= bytes.toArray()
        }
    }
}

fun URLConnection.readStreamLine(
    charset: Charset? = null, // = Charset.defaultCharset(),
    offset: Long = 0,
    onProgression: (line: String, current: Long, len: Long) -> Unit,
    //autoReset: Boolean = true
) = readStream_internal(charset, InputStreamReadMode.LINE, offset) { obj, current, len ->
    onProgression(obj as String, current, len)
}

fun URLConnection.readStreamChar(
    charset: Charset? = null, // = Charset.defaultCharset(),
    offset: Long = 0,
    onProgression: (char: Char, current: Long, len: Long) -> Unit,
    //autoReset: Boolean = true
) = readStream_internal(charset, InputStreamReadMode.CHAR, offset) { obj, current, len ->
    onProgression(obj as Char, current, len)
}

fun URLConnection.readStreamByte(
    charset: Charset? = null, // = Charset.defaultCharset(),
    offset: Long = 0,
    onProgression: (byte: Byte, current: Long, len: Long) -> Unit,
    //autoReset: Boolean = true
) = readStream_internal(charset, InputStreamReadMode.BYTE, offset) { obj, current, len ->
    onProgression((obj as Int).toByte(), current, len)
}


/**
 * Hampir sama dg [readStream_internal], namun mengembalikan response byte sebagai string.
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
private fun URLConnection.responseStr_internal(
    charset: Charset?, // = Charset.defaultCharset(),
    mode: InputStreamReadMode,
    offset: Long,
    onProgression: ((obj: Any?, current: Long, len: Long) -> Unit)?,
    //autoReset: Boolean = true
): String? {
    if(!checkConnection()) return null

    val inputStream= inputStream

    if(offset > 0){
        if(!acceptRange) throw IllegalStateExc(
            currentState = "accept-ranges: none",
            expectedState = "accept-ranges: `!none`",
            detMsg = "Url = $url tidak mendukung request konten parsial, offset= $offset"
        )
        setRequestProperty("Range", "bytes=$offset-")
    }

    @Suppress(SuppressLiteral.NAME_SHADOWING)
    val charset = charset ?: run {
        val httpEncoding= contentEncoding
        if(httpEncoding != null) {
            try { Charset.forName(contentEncoding) }
            catch (e: UnsupportedCharsetException) { null }
        } else null
    } ?: Charset.defaultCharset()
    val len = contentLengthLong_

    var i= 0L

    val sb= StringBuilder()
    when(mode){
        InputStreamReadMode.LINE, InputStreamReadMode.CHAR -> {
            val in_= BufferedReader(InputStreamReader(inputStream, charset))
            //InputStreamReader(inputStream, charset).read()
            if(mode == InputStreamReadMode.LINE){
                var lineStr: String?
                while(in_.readLine().also { lineStr = it } != null){
                    sb += "$lineStr\n"
                    //prine("lineStr= $lineStr")
                    onProgression?.invoke(lineStr, i, len)
                    i += 1
                }
            } else {
                var char: Char?
                while(in_.read().also { char = it.toChar() } >= 0){
                    sb += char!!
                    //prine("char= $char")
                    onProgression?.invoke(char, i, len)
                    i += 1
                }
            }
        }
        InputStreamReadMode.BYTE -> {
            if(len <= Int.MAX_VALUE){
                val bytes= ArrayList<Int>(8192 * 8)
                var byte: Int
                while(inputStream.read().also { byte= it } >= 0){
                    bytes += byte
                    //sb += byte
                    //prine("byte= $byte")
                    onProgression?.invoke(byte, i, len)
                    i += 1
                }
                //val arr= bytes.toArray()
                val bytesArr= ByteArray(bytes.size){ bytes[it].toByte() }
/*
            System.arraycopy(
                bytes.toArray(),
                0,
                bytesArr,
                0,
                bytesArr.size
            )
 */
                return String(bytesArr, charset)
            } else {
                //prinw("Panjang")
                val bytes= ArrayList<Int>(Int.MAX_VALUE)
                var byte: Int
                //var progress= Int.MAX_VALUE.toLong()
                val maxInt= Int.MAX_VALUE.toLong()
                var remain= len -1
                while(remain > 0){
                    bytes.clear()
                    val limit= min(maxInt, remain)
                    for(u in i .. limit){
                        bytes += inputStream.read().also { byte = it }
                        //sb += byte
                        //prine("byte= $byte")
                        onProgression?.invoke(byte, i, len)
                        i += 1
                    }
                    //progress += limit +1
                    remain -= limit +1
                    //val arr= bytes.toArray()
                    val bytesArr= ByteArray(bytes.size){ bytes[it].toByte() }
                    sb += String(bytesArr, charset)
                }
            }
        }
    }
    return if(i > 0) sb.toString() else null
}

/**
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.responseStr(
    charset: Charset?= null,
    offset: Long = 0,
    mode: InputStreamReadMode = InputStreamReadMode.LINE,
    onProgression: ((current: Long, len: Long) -> Unit)?= null,
    //autoReset: Boolean = true
): String? = responseStr_internal(
    charset, mode, offset,
    if(onProgression != null) { _, current, len -> onProgression(current, len) }
    else null
)

/**
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.readResponseLine(
    charset: Charset?= null,
    offset: Long = 0,
    onProgression: ((line: String, current: Long, len: Long) -> Unit)?= null,
    //autoReset: Boolean = true
): String? = responseStr_internal(
    charset, InputStreamReadMode.LINE, offset,
    if(onProgression != null) { obj, current, len -> onProgression(obj as String, current, len) }
    else null
)
/**
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.readResponseChar(
    charset: Charset?= null,
    offset: Long = 0,
    onProgression: ((char: Char, current: Long, len: Long) -> Unit)?= null,
    //autoReset: Boolean = true
): String? = responseStr_internal(
    charset, InputStreamReadMode.CHAR, offset,
    if(onProgression != null) { obj, current, len -> onProgression(obj as Char, current, len) }
    else null
)
/**
 * [onProgression] `current` dimulai dari 0 dan `len` adalah eksklusif (Tidak masuk ke `current`).
 * [offset] dalam satuan `Byte`. [offset] berguna untuk download parsial, biasanya digunakan untuk resume download.
 */
fun URLConnection.readResponseByte(
    charset: Charset?= null,
    offset: Long = 0,
    onProgression: ((byte: Int, current: Long, len: Long) -> Unit)?= null,
    //autoReset: Boolean = true
): String? = responseStr_internal(
    charset, InputStreamReadMode.BYTE, offset,
    if(onProgression != null) { obj, current, len -> onProgression(obj as Int, current, len) }
    else null
)

fun URLConnection.responseByteArray(): ByteArray = inputStream.readAllBytes()