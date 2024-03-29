package exe.tigrulya.relohome.util

data class LazyMap<K, V>(val initializer: (K) -> V) {

    private val map = mutableMapOf<K, V>()

    operator fun get(key: K): V? = map.getOrPut(key) { initializer.invoke(key) }
}

