import org.junit.Test
import sidev.lib.console.prin
import sidev.lib.console.prine
import sidev.lib.jvm.tool.`fun`.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class StdTest {
    @Test
    fun urlTest(){
        val url= URL("https://www.google.com/robots.txt")
        val conn= url.openConnection()

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
        val fileName= "ali imran_json"
        val urlStr= "https://api.banghasan.com/quran/format/json/surat/3" //"https://cdn.alquran.cloud/media/audio/ayah/ar.alafasy/6236" //"https://www.google.com/robots.txt"
        val url= URL(urlStr)
        val conn= url.openConnection() as HttpURLConnection
        //conn.requestMethod = "POST"

        val dir= "_output"
        val file= File("$dir/${conn.requestedFileName ?: fileName}")
        file.parentFile.mkdirs()
        file.createNewFile()

        prin("conn.requestMethod= ${conn.requestMethod}")

        conn.saveBufferByteToFile(file, false)
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