package exe.tigrulya.relohome.notifier.telegram.serde

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import exe.tigrulya.relohome.model.UserSearchOptionsDto

interface SearchOptionsDeserializer {
    fun deserialize(value: String): UserSearchOptionsDto
}

class JsonSearchOptionsDeserializer: SearchOptionsDeserializer {

    private val mapper = jacksonObjectMapper()

    override fun deserialize(value: String): UserSearchOptionsDto = mapper.readValue(value)

}