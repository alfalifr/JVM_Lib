import org.junit.Test
//import sidev.lib.async.whileAndWait
import sidev.lib.collection.joinToString
import sidev.lib.console.prin
import sidev.lib.console.prine
import sidev.lib.jvm.tool.`fun`.*
import sidev.lib.jvm.tool.util.FileUtil
import sidev.lib.jvm.tool.util.TimeUtil
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class StdTest {
    @Test
    fun urlTest(){
        val url= URL("https://www.google.com/robots.txt")
        val conn= url.openConnection()

        prin("conn.contentLengthLong_= ${conn.contentLengthLong_}")

        prin("=== res1 ===")
        val res1= conn.responseStr() != null
        prin("res1= $res1")

        prin("=== res2 ===")
        val res2= conn.responseStr() != null
        prin("res2= $res2")

        prin("=== res5 ===")
        val res5= url.openConnection().readStreamLine { line, current, len ->
            prine("line= $line current= $current, len= $len")
        }
        prin("res5= $res5")

        prin("=== res3 ===")
        val res3= url.openConnection().readResponseByte { byte, current, len ->
            prine("byte= $byte current= $current, len= $len")
        }
        prin("res3= $res3")

        prin("=== res4 ===")
        val res4= url.openConnection().readResponseChar { char, current, len ->
            prine("char= $char current= $current, len= $len")
        }
        prin("res4= $res4")
/*
        prin("=== res6 ===")
        val res6= url.openConnection().responseStr(offset = 32)
        prin("res6= $res6")
// */

        val isSame= res3 == res4
        prin("isSame= $isSame")
    }

    @Test
    fun saveToFileTest(){
        val fileName= "ali imran_json 2"
        val urlStr= "https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy/6236" //"https://www.google.com/robots.txt" //"https://api.banghasan.com/quran/format/json/surat/3" //
        val url= URL(urlStr)
        val conn= url.openConnection() as HttpURLConnection
        //conn.requestMethod = "POST"

        val dir= "_output"
        val file= File("$dir/${conn.requestedFileName ?: fileName}")
        file.parentFile.mkdirs()
        //file.createNewFile()

        prin("conn.requestMethod= ${conn.requestMethod}")

        conn.saveBufferByteToFile(file, false)
    }

    @Test
    fun headerTest(){
        val urlStr= "https://www.everyayah.com/data/Husary_64kbps/002282.mp3" //"https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy/289" //
        val url= URL(urlStr)
        val conn= url.openConnection()
        prin(conn.contentLengthLong_)
        prin(conn.acceptRange)
        prin(conn.headerFields.joinToString(separator = "\n"))
    }

    @Test
    fun downSizeTest(){
        val urlPref= "https://www.everyayah.com/data/Husary_64kbps" //"https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy"
        //val range= 1 .. 6236
        var total= 0L
        val timestamp= TimeUtil.timestamp(pattern = "dd-MM-yyyy_HH.mm.ss")
        val file= File("_output/Alafasy Audio Bit Size - cdn.alquran.cloud - $timestamp - .csv")
        prin(FileUtil.saveln(file, "ayat;len;cumulative;", false))
/*
        whileAndWait({ it.index < 6236 }, 2000, {
            val bool= it is java.net.ConnectException
            if(bool){
                prine("Terjadi error = $it")
                it.printStackTrace()
            }
            bool
        }) {
            val i= it.index + 1
            val urlStr= "$urlPref/$i"
            val url= URL(urlStr)
            val conn= url.openConnection()
            val len= conn.contentLengthLong
            total += len
            FileUtil.saveln(file, "$i;$len;$total;", true)
            prin("i= $i len= $len total= $total")
        }
// */
/*
        for(i in range){
            val urlStr= "$urlPref/$i"
            val url= URL(urlStr)
            val conn= url.openConnection()
            val len= conn.contentLengthLong
            total += len
            FileUtil.saveln(file, "$len;$total;", true)
            prin("i= $i len= $len total= $total")
        }
 */
    }

    @Test
    fun validNameTest(){
        val fileName= "alfatihah_json"
        val path= "_output/$fileName"
        val path2= "_output/$fileName?"
        val path3= "_output/$fileName."
        val path4= "_output/$fileName>"
        val path5= "_output/$fileName}"

        val file= File(path)

        prin(path.isValidFileName)
        prin(path2.isValidFileName)
        prin(path3.isValidFileName)
        prin(path4.isValidFileName)
        prin(path5.isValidFileName)
    }
}