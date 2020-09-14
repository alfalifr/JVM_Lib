package sidev.lib.jvm.tool.manager

import sidev.lib.collection.asMap
import sidev.lib.collection.removeValue

/*
import sidev.lib.universal.`fun`.asMap
import sidev.lib.universal.`fun`.removeValue
import sidev.lib.universal.intfc.ListenerManager_
import sidev.lib.universal.intfc.SifCallable
import sidev.lib.universal.intfc.SifParameter
 */

typealias ListenerType<R> = (Map<String, Any?>) -> R

abstract class ListenerManager<Rl, Rm> {
    val listeners: Map<String, ListenerType<Rl>> = HashMap()
    private val castedListenerMap: HashMap<String, ListenerType<Rl>>
        get()= listeners as HashMap<String, ListenerType<Rl>>

    /** Bentuk lain dari [addListener] sehingga bentuk SAM constructor dapat dilakukan dg mudah. */
    fun addListener(tag: String = listeners.size.toString(), forceReplaceWithSameTag: Boolean = true, l: ListenerType<Rl>): Boolean{
        return if(forceReplaceWithSameTag || tag !in listeners.keys){
            castedListenerMap[tag]= l
            true
        } else false
    }

    /** @return `true` jika [l] berhasil dihapus dari [listeners], `false` jika [l] tidak ada pada [listeners]. */
    fun removeListener(l: ListenerType<Rl>): Boolean = castedListenerMap.removeValue(l)

    /** @return [L] dg [tag] yg dihapus dari [listeners], `null` jika tidak ada listener dg [tag] pada [listeners]. */
    fun removeListenerByTag(tag: String): ListenerType<Rl>? = castedListenerMap.remove(tag)

    /** Memanggil semua listener pada [listeners] dg argumen [args]. */
    fun iterateListeners(vararg args: Pair<String, Any?>): Rm{
        val listenerOutput= listeners.map { Pair(it.key, it.value(args.asMap())) }.asMap()
        return processOutput(listenerOutput)
    }

    /** Memproses semua output listener setelah dipanggil dari [iterateListeners] berupa [listenerOutput] menjadi [Rl]. */
    abstract fun processOutput(listenerOutput: Map<String, Rl>): Rm
}

fun  createSimpleListener(): ListenerManager<Unit, Unit>
    = object : ListenerManager<Unit, Unit>(){
    override fun processOutput(listenerOutput: Map<String, Unit>) {}
}
