package exe.tigrulya.relohome.handler.db.migration

import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.helpers.DbUrlConnectionCommandStep
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.ui.LoggerUIService
import javax.sql.DataSource

class LiquibaseMigrationManager(
    private val dataSource: DataSource,
    private val changelogPath: String = "db/changelog/changelog-root.xml"
) : MigrationManager {
    private val scopeConfig: Map<String, Any> =
        mapOf(Scope.Attr.ui.name to LoggerUIService())

    override fun migrate() {
        dataSource.connection.use { connection ->
            JdbcConnection(connection).use { jdbcConnection ->
                val db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(jdbcConnection)

                val updateCommand = CommandScope(*UpdateCommandStep.COMMAND_NAME)
                    .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, db)
                    .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changelogPath)

                Scope.child(scopeConfig, updateCommand::execute)
            }
        }
    }
}