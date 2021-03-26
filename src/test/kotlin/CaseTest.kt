import org.junit.Test
import sidev.lib.async.whileAndWait
import sidev.lib.collection.joinToString
import sidev.lib.console.prin
import sidev.lib.console.prine
import sidev.lib.jvm.tool.`fun`.contentLengthLong_
import sidev.lib.jvm.tool.`fun`.readStreamBufferByte
import sidev.lib.jvm.tool.`fun`.requestedFileName
import java.io.*
import java.net.URL

class CaseTest {
    @Test
    fun macOsDownloadTest(){
        download(
            "http://download626.mediafire.com/1wcjdvtjbs1g/pph3v402wi4vjo0/macOS+Mojave+10.14.6+%2818G103%29.iso",
            "D:\\DataFast\\Download\\Program\\OS",
            "Mac OS Mojave 10.14.6.iso"
        )
    }
    @Test
    fun borutoDownloadTest(){
        download(
            "https://www84.zippyshare.com/d/xVDv6qeV/914122/Bor-191-360p-SAMEHADAKU.VIP.mkv",
            "D:\\DataFast\\Download\\Program\\OS",
            "Bor-191-360p-samehadaku.vip.mkv"
        )
    }

    //@Test
    fun download(url: String, dir: String, defaultFileName: String){
        val bufferLen = 2_000_000
        val urlStr = url //"http://download626.mediafire.com/1wcjdvtjbs1g/pph3v402wi4vjo0/macOS+Mojave+10.14.6+%2818G103%29.iso"

        val url= URL(urlStr)
        val byteArray = ByteArray(bufferLen)
        var conn= url.openConnection()
        //conn.getHeaderField("Content-Length")
        val length = conn.contentLengthLong_

        prin(conn.headerFields.joinToString())

        val fileName = conn.requestedFileName ?: defaultFileName //"Mac OS Mojave 10.14.6.iso"
        val fileDir= dir //"D:\\DataFast\\Download\\Program\\OS"
        val fileStr= "$fileDir\\$fileName"
        val file = File(fileStr)
        val tempFile= File("${file.parent}\\${file.nameWithoutExtension + ".temp"}")

        prin("file= $file fileStr= $fileStr")

        if(file.exists()){
            prin("File $file udah ada. Gak jadi donlot.")
            return
        }
        if(!tempFile.exists()){
            tempFile.parentFile.mkdirs()
            tempFile.createNewFile()
        }
        var writtenLen = tempFile.length()
        if(writtenLen > 0){
            conn = url.openConnection()
            conn.addRequestProperty("Range", "bytes=${writtenLen}-$length") // starts with 0 ends with `length` (exclusive)
            prin("Resuming file $tempFile ...  ($writtenLen / $length)")
            //conn.headerFields["Content-Range"] = listOf()
        }
/*
        val fileOs= FileOutputStream(tempFile, true)
        val bos= BufferedOutputStream(fileOs, byteArray.size)
        val dos= DataOutputStream(bos)
        val by = "halo".toByteArray()
        dos.write(by)
        dos.flush()
        dos.close()
 */

///*


        prin("===AFTER==== " +conn.headerFields.joinToString())
        prin("Donwloading file $file ...")
        prin("With temp file $tempFile ...")
        whileAndWait(exceptionWaitCheck = {
            prine("Terjadi error saat iterasi, e= $it")
            it.printStackTrace()
            it::class.java.packageName == "java.net"
        }) { condition, itr ->
            val fileOs= FileOutputStream(tempFile, true)
            val bos= BufferedOutputStream(fileOs, byteArray.size)
            //val dos= DataOutputStream(bos)

            conn.readStreamBufferByte(byteArray) { readByteLen, current, len ->
                bos.write(byteArray, 0, readByteLen)
                writtenLen += readByteLen
                condition.value = writtenLen < length
                prin("Writting ... ${(writtenLen.toDouble() / length * 100).format(5)} %  (read= $readByteLen, $writtenLen / $length)")
            }
            bos.flush()
            bos.close()
        }
        tempFile.renameTo(file)
// */
        prin("File $file selesai didonlot!")
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)