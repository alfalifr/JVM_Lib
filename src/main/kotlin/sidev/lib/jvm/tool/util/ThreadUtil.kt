package sidev.lib.jvm.tool.util

//import android.os.Handler
//import android.util.Log
import sidev.lib.console.prine
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.RunnableFuture
import java.util.concurrent.ThreadPoolExecutor

object ThreadUtil{
    object Pool{
        val processorCount= Runtime.getRuntime().availableProcessors()
        private var threadPoolService= Executors.newFixedThreadPool(processorCount) as ThreadPoolExecutor
//        private val daftarTugas= HashMap<String, ArrayList<() -> Unit>>()

        //@return juga menghasilkan RunnableFuture<T>
        //simpan @return untuk membatalkan tugas di waktu kemudian dengan @method batalkan()
        fun <T> submit(tugas: () -> T?): Future<T> {
            if(threadPoolService.isShutdown)
                threadPoolService = Executors.newFixedThreadPool(processorCount) as ThreadPoolExecutor
            return threadPoolService.submit(tugas)
        }

        //@param juga menerima parameter hasil @return @method kumpulkan()
        fun <T> cancel(tugas: RunnableFuture<T>){
            threadPoolService.remove(tugas)
        }

        //mematikan ThreadPool yang lama dan meng-instansiasi object yang baru
        fun recycle(): List<Runnable>{
            return threadPoolService.shutdownNow()
        }
    }
/*
    fun delayRun(millis: Long, func: () -> Unit){
        Handler().postDelayed(func, millis)
    }
 */

    fun printCurrentStackTrace(){
        for(stack in Thread.currentThread().stackTrace)
            prine(stack)
    }

    fun logCurrentStackTrace(){
        Thread.currentThread().stackTrace.forEachIndexed{ i, stack ->
            val funName= stack.methodName
            val className= stack.className
            prine("ThreadUtil: i= $i $className.$funName()")
        }
    }

    fun getStackFunName(ind: Int): String {
        val stackTrace= Thread.currentThread().stackTrace
        val stack= stackTrace[ind]
        val funName= stack.methodName
        val className= stack.className
/*
        for(stack in stackTrace){
            val funName= stack.methodName
            val className= stack.className
            Log.e("ThreadUtil", "$className.$funName")
        }
 */
        return "$className.$funName()"
    }

    fun getCurrentCallerFunName(backInd: Int= 0): String {
        val tempRes= getStackFunName(6 +backInd)
        val thisFunName= "getCurrentCallerFunName"
//        Log.e("ThreadUtil", "getCurrentCallerFunName tempRes= $tempRes")
        if(tempRes.endsWith("$thisFunName\$default()")){
//            Log.e("ThreadUtil", "getCurrentCallerFunName = endsWith \$default()")
            return getStackFunName(6 +backInd +1)
        }
/*
        else{
            Log.e("ThreadUtil", "getCurrentCallerFunName = NOT!!! endsWith \$default()")
        }
 */
        return tempRes
    }
    fun getCurrentFunName(): String {
        return getStackFunName(4)
    }
}