package exe.tigrulya.relohome.handler.controller

import exe.tigrulya.relohome.handler.service.UserService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(userService: UserService = UserService()) {
    routing {
        staticResources("/forms", "templates")

        get("/") {
            call.respondText("Hello World!")
        }

        get("/forms/{form_id}/{user_id}") {
            val formId = call.parameters["form_id"]
            val userId = call.parameters["user_id"]
                ?: throw IllegalArgumentException("User id not provided")

            val renderedForm = MustacheContent(
                "${formId}.hbs",
                mapOf("searchData" to userService.getSearchOptions(userId).toDomain()))
            call.respond(renderedForm)
        }
    }
}
