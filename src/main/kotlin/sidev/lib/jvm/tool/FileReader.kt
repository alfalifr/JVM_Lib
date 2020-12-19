package sidev.lib.jvm.tool

import sidev.lib.check.assertNotNull
import java.io.File
import java.nio.charset.Charset
import java.util.*

class FileReader internal constructor() {
    constructor(dir: String, charset: Charset = Charset.defaultCharset()): this(){
        this.dir= dir
        this.charset= charset
    }

    lateinit var charset: Charset
    var file: File?= null
        private set
    var dir: String?= null
        set(v){
            field= v
            file= null
            if(v != null){
                val file= File(v)
                if(file.exists())
                    this.file= file
            }
        }

    @JvmOverloads
    fun fileNotNull(msg: String= ""): Nothing? {
        return assertNotNull(
            file,
            "$msg, file == null !!"
        )
    }

    @JvmOverloads
    fun readLine(line: Int= 0, range: IntRange?= null): String {
        fileNotNull("readLine()")
        val scanner= Scanner(file!!, charset)

        val rangeLimit= range ?: line .. line
        val rangeItr= 0 until rangeLimit.last

        var out= ""

        for(i in rangeItr){
            if(!scanner.hasNextLine())
                break
            val lineStr= scanner.nextLine()!!

            if(i in rangeLimit){
                out += lineStr +"\n"
            }
        }
        return out
    }

    fun readAll(): String {
        fileNotNull("readAll()")
        val scanner= Scanner(file!!, charset)

        var out= ""

        while(scanner.hasNextLine()){
            val lineStr= scanner.nextLine()!!
            out += lineStr +"\n"
        }
        return out
    }

    @JvmOverloads
    fun iterateLine(startLine: Int= -1, range: IntRange?= null, f: (String) -> Unit){
        fileNotNull("iterateLine()")
        val scanner= Scanner(file!!, charset)

        if(startLine >= 0 || range != null){
            val rangeLimit= range ?: startLine .. Int.MAX_VALUE
            val rangeItr= 0 until rangeLimit.last

            for(i in rangeItr){
                if(!scanner.hasNextLine())
                    break
                val lineStr= scanner.nextLine()!!

                if(i in rangeLimit){
                    f(lineStr)
                }
            }
        } else {
            while(scanner.hasNextLine()){
                val lineStr= scanner.nextLine()!!
                f(lineStr)
            }
        }
    }
}