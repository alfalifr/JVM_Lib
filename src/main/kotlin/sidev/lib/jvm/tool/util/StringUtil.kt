package sidev.lib.jvm.tool.util

//import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils
import sidev.lib.text.nextNonWhitespaceChar
//import sidev.lib.universal.`fun`.nextNonWhitespaceChar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern
import kotlin.math.pow

object StringUtil{

    //pola: "{0}_{1}" dengan key: {"email":"joko@abc.com" "urutan":"001"} -> pola: "joko@abc.com_001"
    @JvmOverloads
    fun polaString(pola: String?, key: HashMap<String, String>?, namaKey: Array<String> = arrayOf()): String?{
        if(pola == null || key == null)
            return null
        var i= -1; var u= -1
        var pjgDiganti= 0
        var strBaru= pola
        while(++i < strBaru!!.length){
            if(strBaru[i] == '{'){
                u= i
                while(++u < strBaru!!.length){
                    if(strBaru[u] == '}'){
                        pjgDiganti= u-i+3
                        val strDiganti= strBaru.substring(i+1, u)
                        var strPengganti: String?
                        if(angkaKah(
                                strDiganti
                            )
                        ){
                            try { strPengganti = key.get(namaKey[strDiganti.toInt()]) }
                            catch(error: java.lang.Exception){throw Exception("polaString(pola: String, key: HashMap<String, String>, vararg namaKey: String): String \n" +
                                    "==> $strBaru -> indek='$strDiganti' MELEBIHI panjang indek namaKey (pjg=${namaKey.size})!")}
                        } else{
                            if(key.containsKey(strDiganti))
                                strPengganti= key.get(strDiganti)
                            else throw Exception("polaString(pola: String, key: HashMap<String, String>, vararg namaKey: String): String \n" +
                                    "==> key '$strDiganti' TIDAK ditemukan!")
                        }
                        strBaru= strBaru.replaceRange(i, u+1, strPengganti!!)
                        i= u +strPengganti.length -pjgDiganti -2
                        break
                    }
                }
            }
        }
        return strBaru
    }
    //{2..key..1} -> mengekstrak pembanding dalam {} sepanjanng "2 dari depan" atau "1 dari belakang" dengan kunci "key"
    //dalam kasus di atas, pjg yang digunakan untuk mengekstrak pembanding adalah "2 dari depan" karena sudah terdefinisi dari depan,
    //  bkn "1 dari belakang"
    fun ekstrakPola(pola: String?, str: String): HashMap<String, String>?{
        pola ?: return null
        val map= HashMap<String, String>()
        var i= -1; var u= -1
        var pjgNilai= 0
        var pjgYgHilang= 0
//            var pjgKeyDariBelakang= 0
        var strDicek= str
        while(++i < pola.length)
            if(pola[i] != '{')
                pjgYgHilang++
            else{
                strDicek= str.substring(pjgYgHilang)
                u= i
                while(++u < pola.length)
                    if(pola[u] == '}'){
                        i++
                        val keyStr= pola.substring(i, u)
                        val arrayKey= keyStr.split("..")
                        var key: String= arrayKey[0]
                        if(arrayKey.size == 1)
                            try{ pjgNilai= arrayKey[0].toInt()
                            } catch(error: Exception){pjgNilai= key.length}
                        else if(arrayKey.size >= 2)
                            try{ pjgNilai = arrayKey[1].toInt()
                            } catch (error: Exception){pjgNilai= arrayKey[1].length}

                        if(key.length == 0 || key.toIntOrNull() != null)
                            key= map.size.toString()
                        val nilaiDiekstrak= strDicek.substring(0, pjgNilai)
                        map[key]= nilaiDiekstrak
                        pjgYgHilang+= pjgNilai
                        i= u
                        break
                    }
            }

        if(map.size > 0)
            return map
        return null
    }
    fun polaKah(str: String): Boolean{
        var i= -1; var u= -1
        while(++i < str.length)
            if(str[i] == '{'){
                u= i
                while(++u < str.length)
                    if(str[u] == '}')
                        return true
            }
        return false
    }

    @JvmOverloads
    fun angkaKah(str: String, indek: Int= -1, sampai: Int= indek+1, basis: Int= 10): Boolean{
        var benarKah= true
        val strDicek=
            if(indek > -1) str.substring(indek, sampai)
            else str
        for(i in 0 until strDicek.length-1){
            val indekDitemukan= kumpulanAngka.indexOf(strDicek[i].toString())
            if(indekDitemukan < 0 || indekDitemukan >= basis){
                if(strDicek[i] == '.')
                    try {
                        strDicek.toDouble()
                    } catch(error: Exception){
                        benarKah= false
                    }
                else
                    benarKah= false
                break
            }
        }
        return benarKah
    }

    fun angkaString(angka: Int, pjg: Int): String{
        return angkaString(
            angka.toString(),
            pjg
        )
    }
    fun angkaString(angka: String, pjg: Int): String{
        var strAngka= angka
        val selisihPjg= pjg -strAngka.length
        for(i in selisihPjg downTo 1)
            strAngka= "0" +strAngka
        return strAngka
    }

    fun angkaDariString(str: String): ArrayList<String>?{
        val array= ArrayList<String>()
        var i= -1
        while(++i < str.length)
            if(str[i].isDigit()){
                var u= i
                while(++u < str.length+1)
                    if(u == str.length || !str[u].isDigit()) {
                        array.add(str.substring(i, u))
                        i= u
                    }
            }
        if(array.size > 0)
            return array
        return null
    }
    fun bknAngkaDariString(str: String): ArrayList<String>?{
        val array= ArrayList<String>()
        var i= -1
        while(++i < str.length)
            if(!str[i].isDigit()){
                var u= i
                while(++u < str.length+1)
                    if(u == str.length || str[u].isDigit()) {
                        array.add(str.substring(i, u))
                        i= u
                    }
            }
        if(array.size > 0)
            return array
        return null
    }

    val kumpulanAngka= arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    val BASIS_SESUAIKAN= kumpulanAngka.size

    @JvmOverloads
    fun angka(desimal: Int, basis: Int= kumpulanAngka.size): String{
        if(desimal == 0)
            return ""
        else
            return "${
                angka(
                desimal / basis,
                basis
            )
            }${kumpulanAngka[desimal%basis]}"
    }
    @JvmOverloads
    fun angka(angkaStr: String, basis: Int= 10): Int{
        var angka= 0.0
        for(i in angkaStr.length-1 downTo 0)
            angka+= kumpulanAngka.indexOf(angkaStr[i].toString()) *basis.toDouble().pow(angkaStr.length-i-1)
        return angka.toInt()
    }

    @JvmOverloads
    fun angkaDesimal(angka: Double, dibelakangKoma: Int= 2): String{
        var pola= "##."
        for(i in 0 until dibelakangKoma)
            pola += "#"
        val formater= DecimalFormat(pola)
        formater.roundingMode= RoundingMode.FLOOR
        return formater.format(angka)
    }

    fun hilangkanHarokat(lafadz: String): String{
        val p = Pattern.compile("\\p{M}")
        val m = p.matcher(lafadz)
        return m.replaceAll("")
    }

    //@param toleransi punya nilai 0-1]
    @JvmOverloads
    fun cocokan(str1: String, str2: String, toleransi: Float, ketat: Boolean= false): Boolean{
        val batas= if(toleransi in 0.0..1.0) toleransi
        else 0.toFloat()
        val persentaseCocok= StringUtils.getJaroWinklerDistance(str1, str2)
        var mirip= persentaseCocok >= batas
        if(ketat){
            val jmlKata1= str1.split(" ")
            val jmlKata2= str1.split(" ")
            mirip = mirip && jmlKata1 == jmlKata2
        }
        return mirip
    }
    fun bandingkan(str1: String, str2: String): Double{
        return StringUtils.getJaroWinklerDistance(str1, str2)
    }
/*
        fun cocokan(str1: String, str2: String, toleransi: Float, perKata: Boolean= false): Boolean{
            val batas= if(toleransi in 0.0..1.0) toleransi
                            else 0.toFloat()
            val persentaseCocok=
                    if(perKata) bandingkanKata(str1, str2)
                    else bandingkanKalimat(str1, str2)
            return persentaseCocok >= batas
        }
*/

    //membandingkan 2 string dengan rumus= 1 -jmlPerbedaan /pjgMinim
    //@return 1 jika sama
    //        mendekati 0 jika semakin berbeda
    //untuk membandingkan secara satu kalimat
    fun bandingkanKalimat(str1: String, str2: String): Float{
        val pjgMinim= Math.min(str1.length, str2.length)
        val selisihPjg= Math.abs(str1.length -str2.length)
        val array1= str1.split(" ")
        val array2= str2.split(" ")

        var jmlPerbedaan= selisihPjg
        for(i in 0 until pjgMinim)
            if(str1[i] != str2[i]) jmlPerbedaan++

//            println("pjgMinim= $pjgMinim selisihPjg= $selisihPjg jmlPerbedaan= $jmlPerbedaan")

        return 1 -jmlPerbedaan /pjgMinim .toFloat()
    }

    //untuk membandingkan per kata
    fun bandingkanKata(str1: String, str2: String): Float{
        var str1Awal= str1
        var str2Awal= str2

        var str1Akhir= ""
        var str2Akhir= ""

        var strSama= ""

//            var jmlPerbedaan= 0
//            var jmlKataSama= 0
        var jmlKataSama_dalam: Int

//            var pjgKataSama= 0
//            var pjgKataBeda= 0

        do{
            var strTerpanjang: String
            var strTerpendek: String
            var indekStrTerpendek: Int

            if(str1Awal.length < str2Awal.length){
                strTerpanjang= str2Awal
                strTerpendek= str1Awal
                indekStrTerpendek= 1
            } else{
                strTerpanjang= str2Awal
                strTerpendek= str1Awal
                indekStrTerpendek= 2
            }

//                val arrayPjg= strTerpanjang.split(" ")
            val arrayPndk= strTerpendek.split(" ")
            val pjgMinim= arrayPndk.size

            jmlKataSama_dalam= 0
            for(i in 0 until pjgMinim)
                if(strTerpanjang.contains(arrayPndk[i])){
                    strTerpanjang= strTerpanjang.replace(arrayPndk[i], "")
                    strTerpendek= strTerpendek.replace(arrayPndk[i], "")
                    strSama += " ${arrayPndk[i]}"
//                        pjgKataSama += arrayPndk[i].length
                    jmlKataSama_dalam++
                }
//                jmlKataSama+= jmlKataSama_dalam

            if(indekStrTerpendek == 1){
                str1Awal= strTerpendek
                str2Awal= strTerpanjang
            } else{
                str2Awal= strTerpendek
                str1Awal= strTerpanjang
            }
            jmlKataSama_dalam= 0
//            println("pjgMinim= $pjgMinim selisihPjg= $selisihPjg jmlPerbedaan= $jmlPerbedaan")
        } while(jmlKataSama_dalam > 0)
/*
            var indekBatas= 0
            for(i in 0 until str1Awal.length)
                if(str1Awal[i] != ' '){
                    indekBatas= i
                    break
                }
*/
/*
            while(str1Awal.contains(" "))
                str1Awal= str1Awal.replace("  ", " ")

            while(str2Awal.contains(" "))
                str2Awal= str2Awal.replace("  ", " ")

            str1Akhir= strSama +str1Awal
            str2Akhir= strSama +str2Awal
*/
        return bandingkanKalimat(
            str1Akhir,
            str2Akhir
        )
    }
    /*
   ==============================
   Pola Huruf Kapital
   ==============================
   */
    @JvmOverloads
    fun toSnakeCase(strCamelCase: String, kecilSemua: Boolean= false): String{
        var strHasil= strCamelCase
        var i= -1
        while(++i < strHasil.length)
            if(strHasil[i].isUpperCase() && i > 0){
                strHasil= strHasil.substring(0, i) +"_" +strHasil.substring(i)
                if(kecilSemua)
                    strHasil= strHasil.substring(0, i+1) +strHasil[i+1].toLowerCase() +(if(i+2 < strHasil.length) strHasil.substring(i+2) else "")
                i++
            }
        return strHasil
    }
    fun toCamelCase(strSnakeCase: String): String{
        var strHasil= strSnakeCase
        var i= -1
        while(++i < strHasil.length-1)
            if(strHasil[i] == '_' && i > 0){
                strHasil= strHasil.substring(0, i) +strHasil[i+1].toUpperCase() +(if(i+2 < strHasil.length) strHasil.substring(i+2) else "")
            }
        return strHasil
    }

/*
===========================
JSON area -> Untuk operasi simpel string JSON
===========================
 */
    /**
     * First occurrence
     */
    @JvmOverloads
    fun getJsonPrimitiveValue(jsonStr: String, key: String, occurrenceInd: Int= 0,
                              quote: Char= '"', delimiter: Char= ','): String? {
//        var keyIndFound= false
        var occFound= false
        var keyStartItr= 0
        var occItr= -1

        while(!occFound && keyStartItr < jsonStr.length){
            val keyStartInd= jsonStr.indexOf(key, keyStartItr)
            if(keyStartInd == -1) //Brarti key nya gakda di jsonStr
                break

//            Log.e("UTIL", "keyStartInd= $keyStartInd keyStartItr= $keyStartItr key= $key")

            val nextCharInd= keyStartInd +key.length
            val nextChar= try { jsonStr[nextCharInd] }
            catch (e: IndexOutOfBoundsException){ return null } //Itu artinya key nya gak punya nilai

            val expectedNextColonStartInd= if(nextChar == quote) nextCharInd+1
            else nextCharInd

            val keyIndFound= jsonStr.nextNonWhitespaceChar(expectedNextColonStartInd) == ':'
            if(keyIndFound){
//                Log.e("UTIL", "keyIndFound")
//                Log.e("UTIL", "occItr= ${occItr+1} occurrenceInd= $occurrenceInd")
                if(++occItr == occurrenceInd){
                    occFound= true
                    val colonInd= jsonStr.indexOf(":", expectedNextColonStartInd)
//                val valStartInd= colonStartInd +1
                    val nextNonWhitespaceChar= jsonStr.nextNonWhitespaceChar(colonInd+1)
                    if(nextNonWhitespaceChar != null
                        && nextNonWhitespaceChar != '['
                        && nextNonWhitespaceChar != '{'){
                        var valStartInd= jsonStr.indexOf(nextNonWhitespaceChar, colonInd+1)
                        val limitChar= if(nextNonWhitespaceChar == quote) {
                            valStartInd++
                            quote
                        } else delimiter
                        var valEndInd= jsonStr.indexOf(limitChar, valStartInd)
                        if(valEndInd == -1){ //Kemungkinan value yang ditemukan berada di akhir string
                            val lastChar= jsonStr.last()

                            valEndInd = if(lastChar == ']' || lastChar == '}')
                                jsonStr.length-1
                            else
                                jsonStr.length
                        }

//                        Log.e("UTIL", "valStartInd= $valStartInd valEndInd= $valEndInd limitChar= $limitChar")

                        val res= jsonStr.substring(valStartInd, valEndInd)
//                        Log.e("UTIL", "res= $res")
                        return res
                    }
                } else
                    keyStartItr= keyStartInd +key.length
            } else
                keyStartItr= keyStartInd +key.length
        }
        return null
    }



/*
===========================
Password
===========================
*/
    var PASWD_MIN= 6
    const val PASWD_STRENGTH_VERY_WEAK= -1 //krg dari 6 karakter
    const val PASWD_STRENGTH_WEAK= 0 //terdiri dari 6 atau lebih karakter
    const val PASWD_STRENGTH_MED= 1 //alphanumeric
    const val PASWD_STRENGTH_STRONG= 2 //alphanumeric + upper-lower
    const val PASWD_STRENGTH_VERY_STRONG= 3 //alphanumeric + upper-lower + symbol
    fun assessPassStrength(paswd: String): Int {
        val lowercaseRegex= "[a-z]".toRegex()
        val uppercaseRegex= "[A-Z]".toRegex()
        val numberRegex= "[0-9]".toRegex()
        val symbolRegex= "[\$-/:-?{-~!\"^_`\\[\\]]".toRegex()
        return if(paswd.length < PASWD_MIN) PASWD_STRENGTH_VERY_WEAK
        else if(paswd.contains(lowercaseRegex) && paswd.contains(uppercaseRegex)
            && paswd.contains(numberRegex) && paswd.contains(symbolRegex))
            PASWD_STRENGTH_VERY_STRONG
        else if(paswd.contains(lowercaseRegex) && paswd.contains(uppercaseRegex) && paswd.contains(numberRegex)
            || paswd.contains(lowercaseRegex) && paswd.contains(uppercaseRegex) && paswd.contains(symbolRegex)
            || paswd.contains(lowercaseRegex) && paswd.contains(numberRegex) && paswd.contains(symbolRegex)
            || paswd.contains(uppercaseRegex) && paswd.contains(numberRegex) && paswd.contains(symbolRegex))
            PASWD_STRENGTH_STRONG
        else if(paswd.contains(lowercaseRegex) && paswd.contains(numberRegex)
            || paswd.contains(lowercaseRegex) && paswd.contains(symbolRegex)
            || paswd.contains(lowercaseRegex) && paswd.contains(uppercaseRegex)
            || paswd.contains(uppercaseRegex) && paswd.contains(numberRegex)
            || paswd.contains(uppercaseRegex) && paswd.contains(symbolRegex)
            || paswd.contains(numberRegex) && paswd.contains(symbolRegex))
            PASWD_STRENGTH_MED
        else PASWD_STRENGTH_WEAK
/*
            else if(paswd.contains("[a-z][A-Z][0-9][\$-/:-?{-~!\"^_`\\[\\]]".toRegex())) PASWD_STRENGTH_VERY_STRONG
            else if(paswd.contains("[a-z][A-Z][0-9]".toRegex())) PASWD_STRENGTH_STRONG
            else if(
                paswd.contains("[a-z][0-9]".toRegex())
                || paswd.contains("[A-Z][0-9]".toRegex())
            ) PASWD_STRENGTH_MED
            else PASWD_STRENGTH_WEAK
 */
    }



/*
===========================
Pola String Umum
===========================
*/
    val urlRegexStr= "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"

    fun getUrl(str: String): List<String>? {
        val p = Pattern.compile(urlRegexStr, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(str)
        var result = m.find()
        var i= 0
        val list= ArrayList<String>()
        while (result) {
            val url= m.group(0)
            list.add(url)
            println("i= $i - url= $url")
            result = m.find()
            i++
        }
        return if(list.isNotEmpty()) list
        else null
    }

    fun isUrl(str: String): Boolean{
        return str.matches(urlRegexStr.toRegex())
    }


//    object Json{}
}