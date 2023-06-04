package exe.tigrulya.relohome.handler.model

enum class RoomCount(private val roomCount: Int) {
    STUDIO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE_PLUS(5);
}

fun Int.toRoomCount(): RoomCount = when (this) {
    0 -> RoomCount.STUDIO
    1 -> RoomCount.ONE
    2 -> RoomCount.TWO
    3 -> RoomCount.THREE
    4 -> RoomCount.FOUR
    else -> RoomCount.FIVE_PLUS
}

