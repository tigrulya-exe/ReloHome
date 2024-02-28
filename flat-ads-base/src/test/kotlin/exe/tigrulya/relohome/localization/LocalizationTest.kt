package exe.tigrulya.relohome.localization

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

data class TestCtx(val name: String)

const val EN = "en"
const val RU = "ru"

const val RAW_CONSTANT = "test.constant.raw"
const val EVAL_CONSTANT = "test.constant.with-ctx"

class LocalizationTest : FunSpec({
    val localization = Localization("locales")

    test("en - check raw value") {
        localization[EN, RAW_CONSTANT]?.rawValue shouldBe "Hello, dear friends!"
    }

    test("en - check eval value") {
        val ctx = TestCtx("Jigo")
        localization[EN, EVAL_CONSTANT]?.eval(ctx) shouldBe "Hello, dear Jigo!"
    }

    test("ru - check raw value") {
        localization[RU, RAW_CONSTANT]?.rawValue shouldBe "Привет, дорогие друзья!"
    }

    test("ru - check eval value") {
        val ctx = TestCtx("Джиго")
        localization[RU, EVAL_CONSTANT]?.eval(ctx) shouldBe "Привет, дорогой Джиго!"
    }
})
