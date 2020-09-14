package sidev.lib.jvm.tool.util

import java.io.*
import java.util.*

object FileUtil{
    //jika file sudah ada, maka isi akan ditimpa
    fun salin(fileAsal: File, fileTujuan: File){
//            val fileToCopy = File("c:/temp/testoriginal.txt")
        val inputStream = FileInputStream(fileAsal)
        val inChannel = inputStream.channel

//            val newFile = File("c:/temp/testcopied.txt")
        if(!fileTujuan.exists())
            buatFileKosong(fileTujuan)

        val outputStream = FileOutputStream(fileTujuan)
        val outChannel = outputStream.channel

        inChannel.transferTo(0, fileAsal.length(), outChannel)

        inputStream.close()
        outputStream.close()
    }

    fun buatFileKosong(file: File){
        file.parentFile.mkdirs()
        val pw= PrintWriter(file)
        pw.print("")
        pw.close()
    }

    fun fileTersedia(fileBaru: File, keterangan: String= "", pjgDigit: Int= 3): File{
        var urutan= 1
        val namaFile= fileBaru.absolutePath
        val indekAkhirTitik= namaFile.lastIndexOf(".")
        val namaFilePrefix= namaFile.substring(0, indekAkhirTitik)
        val ekstensiFile= namaFile.substring(indekAkhirTitik)
        val tambahanKeterangan=
            if(keterangan.isNotEmpty()) "_$keterangan"
            else ""

        var fileDicek= fileBaru
        while(fileDicek.exists())
            fileDicek = File("${namaFilePrefix}_${++urutan}$tambahanKeterangan$ekstensiFile")

        var strUrutan= StringUtil.angka(urutan)
        strUrutan= StringUtil.angkaString(strUrutan, pjgDigit)
        fileDicek = File("${namaFilePrefix}_$strUrutan$tambahanKeterangan$ekstensiFile")

        return fileDicek
    }


    fun simpan(pathFile: String, isi: String, fileYgSama: Boolean= true): Boolean{
        val fileOutput= File(pathFile)
        return simpan(
            fileOutput,
            isi,
            fileYgSama
        )
    }
    fun simpan(file: File, isi: ByteArray, fileYgSama: Boolean= true): Boolean{
        return simpan(
            file,
            String(isi),
            fileYgSama
        )
    }
    fun simpan(file: File, isi: String, fileYgSama: Boolean= true): Boolean {
        return simpanTulis_dalam(
            file,
            isi,
            fileYgSama,
            false
        )
    }

    fun simpanln(pathFile: String, isi: String, fileYgSama: Boolean= true): Boolean{
        val fileOutput= File(pathFile)
        return simpanln(
            fileOutput,
            isi,
            fileYgSama
        )
    }
    fun simpanln(file: File, isi: ByteArray, fileYgSama: Boolean= true): Boolean{
        return simpanln(
            file,
            String(isi),
            fileYgSama
        )
    }
    fun simpanln(file: File, isi: String, fileYgSama: Boolean= true): Boolean {
        return simpanTulis_dalam(
            file,
            isi,
            fileYgSama,
            true
        )
    }

    private fun simpanTulis_dalam(file: File, isi: String, fileYgSama: Boolean, gantiBaris: Boolean): Boolean{
        if(!file.exists())
            file.parentFile.mkdirs()
        try {
            val fw= FileWriter(file, fileYgSama)
            val pw= PrintWriter(fw)
            if(gantiBaris)
                pw.println(isi)
            else
                pw.print(isi)
            pw.close()
            return true
        } catch (error: Exception) {
            return false
        }
    }


    fun bacaStrDariFile(pathFile: String): String?{
        val fileOutput= File(pathFile)
        return bacaStrDariFile(
            fileOutput
        )
    }
    fun bacaStrDariFile(file: File): String?{
        if(!file.exists()) return null
        try{
            val input= Scanner(file)
            var str= ""
            while(input.hasNextLine())
                str += "${input.nextLine()}\n"
            return str
        }catch(error: Exception){
            return null
        }
    }

    fun isDirLocal(dir: String): Boolean{
        return File(dir).absoluteFile.exists()
    }
}