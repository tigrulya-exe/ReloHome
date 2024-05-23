package exe.tigrulya.relohome.error

open class ReloHomeException(message: String): RuntimeException(message)
class ReloHomeUserException(message: String): ReloHomeException(message)
class ReloHomeServerException(message: String): ReloHomeException(message)
