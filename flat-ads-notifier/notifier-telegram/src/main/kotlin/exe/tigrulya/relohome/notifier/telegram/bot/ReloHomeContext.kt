package exe.tigrulya.relohome.notifier.telegram.bot

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import exe.tigrulya.relohome.api.user_handler.UserHandlerGateway
import exe.tigrulya.relohome.notifier.telegram.bot.state.UserStatesManager
import exe.tigrulya.relohome.notifier.telegram.serde.SearchOptionsDeserializer

class ReloHomeContext(
    val userHandlerGateway: UserHandlerGateway,
    val userStatesManager: UserStatesManager,
    val keyboardProvider: MainKeyboardProvider,
    val searchOptionsDeserializer: SearchOptionsDeserializer,
    delegate: BehaviourContext
) : BehaviourContext by delegate