package exe.tigrulya.relohome.handler.controller

import exe.tigrulya.relohome.handler.service.FlatAdService
import exe.tigrulya.relohome.handler.service.MustacheHtmlRenderService
import exe.tigrulya.relohome.handler.service.UserService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService = UserService(),
    flatAdService: FlatAdService,
    renderService: MustacheHtmlRenderService = MustacheHtmlRenderService()
) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/forms/{form_id}/{user_id}") {
            val formId = call.parameters["form_id"]
                ?: throw IllegalArgumentException("Form id not provided")
            val userId = call.parameters["user_id"]
                ?: throw IllegalArgumentException("User id not provided")
            val locale = call.request.queryParameters["locale"] ?: "en"

            val searchOptions = userService.getSearchOptions(userId)
            val allDistricts = flatAdService.getDistricts(searchOptions.cityName)

            call.respond(renderService.renderForm("$locale/$formId", searchOptions, allDistricts))
        }
    }
}
