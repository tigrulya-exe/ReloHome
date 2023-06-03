package exe.tigrulya.relohome.connector.listam

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val connector = ListAmConnector()
    val flatPosts = connector.fetch()
    flatPosts.collect { println(it) }
}
