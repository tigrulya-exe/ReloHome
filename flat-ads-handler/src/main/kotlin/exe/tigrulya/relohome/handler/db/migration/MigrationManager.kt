package exe.tigrulya.relohome.handler.db.migration

import javax.sql.DataSource

interface MigrationManager {
    companion object {
        fun newInstance(dataSource: DataSource): MigrationManager {
            return LiquibaseMigrationManager(dataSource)
        }
    }
    fun migrate()
}