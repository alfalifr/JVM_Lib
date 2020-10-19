package sidev.lib.jvm.tool.`fun`

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLConnection

val URLConnection.responseStr: String
    get(){
        val in_= BufferedReader(InputStreamReader(inputStream))

        var res= ""
        var lineStr: String?
        while(in_.readLine().also { lineStr = it } != null){
            res += lineStr
        }
        return res
    }