package exe.tigrulya.relohome.ssge.model

import com.fasterxml.jackson.annotation.JsonValue

enum class TranslateLanguage(@get:JsonValue val text: String) {
    RU("ru"),
    ENG("en"),
    KA("ka");
}

data class TranslateRequest(val text: String, val targetLanguage: TranslateLanguage)
data class TranslateResponse(val translation: String)