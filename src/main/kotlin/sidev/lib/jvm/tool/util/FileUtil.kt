package sidev.lib.jvm.tool.util

import sidev.lib.console.prine
import java.io.*
import java.util.*

object FileUtil{
    const val FILE_TEST_NAME= "fileTest"

    //jika file sudah ada, maka isi akan ditimpa
    fun copy(src: File, dest: File){
//            val fileToCopy = File("c:/temp/testoriginal.txt")
        val inputStream = FileInputStream(src)
        val inChannel = inputStream.channel

//            val newFile = File("c:/temp/testcopied.txt")
        if(!dest.exists())
            createEmptyFile(dest)

        val outputStream = FileOutputStream(dest)
        val outChannel = outputStream.channel

        inChannel.transferTo(0, src.length(), outChannel)

        inputStream.close()
        outputStream.close()
    }

    fun createEmptyFile(file: File){
        file.parentFile.mkdirs()
        val pw= PrintWriter(file)
        pw.print("")
        pw.close()
    }

    /**
     * Mengetes apakah [dir] dapat dijadikan direktori untuk menulis file.
     */
    fun canWriteTo(dir: File): Boolean{
        return try {
            val testFile= File("${dir.absolutePath}/$FILE_TEST_NAME")
            if(dir.exists() || dir.mkdirs()){
                val pw= PrintWriter(testFile)
                pw.print("")
                pw.close()
                testFile.delete()
                true
            } else false
        } catch (e: FileNotFoundException){
            prine("Tidak dapat menulis pada direktori: ${dir.absolutePath}")
            false
        }
    }

    /**
     * Mengambil file dg nama yg serupa dg [newFile] yg ditambah dg [additional].
     * File yg dikembalikan merupakan instance File dg nama `fileBaru.absolutePath`.
     * Nama file ditambah dg urutan dan [additional] jika sudah terdapat file serupa
     * dg [newFile].
     */
    @JvmOverloads
    fun getAvailableFile(newFile: File, additional: String= "", digitLen: Int= 3): File{
        var urutan= 1
        val namaFile= newFile.absolutePath
        val indekAkhirTitik= namaFile.lastIndexOf(".")
        val namaFilePrefix= namaFile.substring(0, indekAkhirTitik)
        val ekstensiFile= namaFile.substring(indekAkhirTitik)
        val tambahanKeterangan=
            if(additional.isNotEmpty()) "_$additional"
            else ""

        var fileDicek= newFile
        while(fileDicek.exists())
            fileDicek = File("${namaFilePrefix}_${++urutan}$tambahanKeterangan$ekstensiFile")

        var strUrutan= StringUtil.angka(urutan)
        strUrutan= StringUtil.angkaString(strUrutan, digitLen)
        fileDicek = File("${namaFilePrefix}_$strUrutan$tambahanKeterangan$ekstensiFile")

        return fileDicek
    }


    @JvmOverloads
    fun save(filePath: String, content: String, inSameFile: Boolean= true): Boolean{
        val fileOutput= File(filePath)
        return save(
            fileOutput,
            content,
            inSameFile
        )
    }
    @JvmOverloads
    fun save(file: File, content: ByteArray, inSameFile: Boolean= true): Boolean{
        return save(
            file,
            String(content),
            inSameFile
        )
    }
    @JvmOverloads
    fun save(file: File, content: String, inSameFile: Boolean= true): Boolean {
        return internalWriteTo(
            file,
            content,
            inSameFile,
            false
        )
    }

    @JvmOverloads
    fun saveln(pathFile: String, content: String, inSameFile: Boolean= true): Boolean{
        val fileOutput= File(pathFile)
        return saveln(
            fileOutput,
            content,
            inSameFile
        )
    }
    @JvmOverloads
    fun saveln(file: File, content: ByteArray, inSameFile: Boolean= true): Boolean{
        return saveln(
            file,
            String(content),
            inSameFile
        )
    }
    @JvmOverloads
    fun saveln(file: File, content: String, inSameFile: Boolean= true): Boolean {
        return internalWriteTo(
            file,
            content,
            inSameFile,
            true
        )
    }

    private fun internalWriteTo(file: File, content: String, inSameFile: Boolean, newLine: Boolean): Boolean{
        if(!file.exists())
            file.parentFile.mkdirs()
        return try {
            val fw= FileWriter(file, inSameFile)
            val pw= PrintWriter(fw)
            if(newLine)
                pw.println(content)
            else
                pw.print(content)
            pw.close()
            true
        } catch (error: Exception) {
            false
        }
    }

    fun readStrFrom(filePath: String): String?{
        val fileOutput= File(filePath)
        return readStrFrom(fileOutput)
    }
    fun readStrFrom(file: File): String?{
        if(!file.exists()) return null
        return try{
            val input= Scanner(file)
            var str= ""
            while(input.hasNextLine())
                str += "${input.nextLine()}\n"
            str
        } catch(error: Exception){
            null
        }
    }

    fun dirExists(dir: String): Boolean{
        return File(dir).absoluteFile.exists()
    }
}