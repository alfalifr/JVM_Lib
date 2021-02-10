package sidev.lib.jvm.tool.`fun`

import java.io.File
import java.io.IOException

/**
 * Menguji apakah `this.extension` [String] valid untuk dijadikan sebagai fileName / fileDirectory.
 * Hasil pengujian mungkin bergantung dari platform sitem file.
 */
val String.isValidFileName: Boolean
    get(){
        val file= File(this)

        if(file.exists())
            return true

        return try {
            val created= file.createNewFile()
            file.delete()
            created
        } catch (e: IOException){
            false
        }
    }