package exe.tigrulya.relohome.handler.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

interface DataSourceFactory {
    fun create(): DataSource
}

class HikariPooledDataSourceFactory(
    private val jdbcUrl: String,
    private val username: String? = null,
    private val password: String? = null
) : DataSourceFactory {
    override fun create(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password

        return HikariDataSource(config)
    }
}
