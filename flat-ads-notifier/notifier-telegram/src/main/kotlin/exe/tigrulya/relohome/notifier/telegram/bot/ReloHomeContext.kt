package exe.tigrulya.relohome.notifier.telegram.bot

import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.bot.state.LocalizationManager
import exe.tigrulya.relohome.notifier.telegram.bot.state.UserStatesManager
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer

class ReloHomeContext(
    val userHandlerGateway: UserHandlerGateway,
    val userStatesManager: UserStatesManager,
    val keyboardProvider: MainKeyboardProvider,
    val searchOptionsDeserializer: SearchOptionsDeserializer,
    val localization: LocalizationManager,
) : LocalizationManager by localization,
    UserStatesManager by userStatesManager