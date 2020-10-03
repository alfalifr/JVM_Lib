package sidev.lib.jvm.tool

import sidev.lib.check.notNull
import sidev.lib.collection.indexOfString
import sidev.lib.jvm.tool.util.FileUtil
import sidev.lib.text.getPrefixIn
import sidev.lib.text.getQuoted

class CsvFormatter() {
    companion object{
        val delimiter= arrayOf(
            ";", ",", "\t", " "
        )

        val quoter= arrayOf(
            "\"", "'", "*", ""
        )
    }

    constructor(dir: String): this(){
        this.dir= dir
    }

    var delimited= delimiter.last()
        private set
    var quoted= quoter.last()
        private set
    var colNumber= 0
        private set
    var header: Array<String>?= null
        private set
    var record: Array<Array<String>>?= null
        private set


    private var fileReader= FileReader()
    var dir: String?= null
        set(v){
            field= v
            fileReader.dir= v
            if(v != null)
                initConfig()
        }



    private fun dirNotNull(f: (dir: String) -> Unit){
        if(dir != null)
            f(dir!!)
    }

    private fun initConfig(){
        checkCurrentFormat()
        checkHeader()
        readRecord()
    }

    private fun checkCurrentFormat(){
        val first= fileReader.readLine()
        first.getPrefixIn(quoter)
            .notNull {
                //Knp gk pake String.first()?? Agar dapat mengakomodasi quoter lebih dari 2 karakter.
                quoted= first.substring(0, it.length)
            }

        if(quoted.isNotEmpty()){
            val startIndex= first.indexOf(quoted, quoted.length)
            val nextStr= first.substring(startIndex +quoted.length)
            nextStr.getPrefixIn(delimiter)
                .notNull {
                    //Knp gk pake String.first()?? Agar dapat mengakomodasi delimiter lebih dari 2 karakter.
                    delimited= first.substring(0, it.length)
                }
        } else {
            //Jika quoted tidak ada, maka harapan terbaik adalah menganggap
            // karakter dalam delimiter apapun yang muncul dalam first
            // adalah delimiternya
            delimited= delimiter.find { it in first }!! //first.filter { it in delimiter }.first() //.filterIn(delimiter).first()
        }
    }

    private fun checkHeader() {
        val header= getVal(0)
        this.header= header
        colNumber= header?.size ?: 0
    }

    fun readRecord(){
        val record= ArrayList<Array<String>>()

        fileReader.iterateLine(startLine = 1) { row ->
            val coloumn= ArrayList<String>()
            var start= 0

            while(start < row.length){
                val quotedVal= row.getQuoted(quoted, start, true)!!
                coloumn.add(
                    quotedVal.removeSurrounding(quoted)
                )
                start= quotedVal.length +delimited.length
            }

            if(coloumn.isNotEmpty()){
                record.add(Array(coloumn.size){ coloumn[it] })
            }
        }

        if(record.isNotEmpty())
            this.record= Array(record.size){ record[it] }
    }

    /**
     * @param col adalah indeks kolom
     *      Jika col krg dari 0, berarti satu baris diambil semua
     */
    fun getVal(row: Int, col: Int= -1, colName: String?= null): Array<String>? {
        val coloumn= ArrayList<String>()
        val first= fileReader.readLine(row)
        var start= 0

        while(start < first.length){
            val quotedVal= first.getQuoted(quoted, start, true)!!
            coloumn.add(
                quotedVal.removeSurrounding(quoted)
            )
            start= quotedVal.length +delimited.length
        }

        return if(coloumn.isNotEmpty()){
            when {
                colName != null -> {
                    val colInd= header!!.indexOfString(colName, true)
                    Array(1){ coloumn[colInd] }
                }
                col < 0 -> Array(coloumn.size){ coloumn[it] }
                else -> Array(1){ coloumn[col] }
            }
        } else
            null
    }

    fun setVal(row: Int, col: Int= -1, colName: String?= null, value: String) {
        if(record != null){
            val colInd=
                if(col >= 0) col
                else header!!.indexOfString(colName!!, true)
            record!![row][colInd]= value
        }
    }


    fun save(){
        var str= ""
        val colLimit= colNumber -1
        record?.forEach { row ->
            var rowStr= ""
            row.forEachIndexed { i, col ->
                rowStr += "$quoted$col$quoted"
                if(i < colLimit)
                    rowStr += "\n"
            }
            rowStr= rowStr.removeSuffix(delimited)
            str += rowStr +"\n"
        }
        FileUtil.save(fileReader.file!!, str, false)
    }
}