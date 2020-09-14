package sidev.lib.jvm.tool.util

object ReflexUtil {
/*
======================
newInstance
======================
*/
    fun <O> newInstance(className: String): O {
        return Class.forName(className)
            .newInstance() as O
    }

    fun <O> newInstance(className: String, constParamType: Array<Class<*>>, constParamVal: Array<Any>): O {
        return Class.forName(className)
            .getConstructor(*constParamType)
            .newInstance(*constParamVal) as O
    }
/*
    inline fun <reified I> newInstance(): I {
        return Class.forName(I::class.java.name)
            .newInstance() as I
    }

    inline fun <reified I, reified C> newInstance(vararg arg: C): I {
        return Class.forName(I::class.java.name)
            .getConstructor(C::class.java)
            .newInstance(*arg) as I
    }

    inline fun <reified I, reified C1, reified C2> newInstance(arg1: Array<C1>, arg2: Array<C2>): I {
        return Class.forName(I::class.java.name)
            .getConstructor(C1::class.java, C2::class.java)
            .newInstance(*arg1, *arg2) as I
    }

    inline fun <reified I, reified C1, reified C2, reified C3> newInstance(arg1: Array<C1>, arg2: Array<C2>, arg3: Array<C3>): I {
        return Class.forName(I::class.java.name)
            .getConstructor(C1::class.java, C2::class.java, C3::class.java)
            .newInstance(*arg1, *arg2, *arg3) as I
    }

    inline fun <reified I, reified C1, reified C2, reified C3, reified C4>
            newInstance(arg1: Array<C1>, arg2: Array<C2>, arg3: Array<C3>, arg4: Array<C4>): I {
        return Class.forName(I::class.java.name)
            .getConstructor(C1::class.java, C2::class.java, C3::class.java, C4::class.java)
            .newInstance(*arg1, *arg2, *arg3, *arg4) as I
    }
 */
}