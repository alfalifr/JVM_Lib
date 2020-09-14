package sidev.lib.jvm.tool.util

object KReflexUtil {
    fun setField(owner: Any, fieldName: String, value: Any?){
        owner::class.java.fields
    }
}