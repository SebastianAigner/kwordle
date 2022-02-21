import LetterState.*
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class WordleTest {

    @Test
    fun `correct guess`() {
        val state = wordle("XXXXX", "XXXXX")
        assertEquals("", listOf(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT), state)
    }

    @Test
    fun `incorrect guess`() {
        val state = wordle("XXXXX", "OOOOO")
        assertEquals("", listOf(INCORRECT, INCORRECT, INCORRECT, INCORRECT, INCORRECT), state)
    }

    @Test
    fun `one letter correct`() {
        val state = wordle("XXXXX", "XOOOO")
        assertEquals("", listOf(CORRECT, INCORRECT, INCORRECT, INCORRECT, INCORRECT), state)
    }

    @Test
    fun `one letter correct, but in the wrong position`() {
        val state = wordle("XXXXZ", "OOOOX")
        assertEquals("", listOf(INCORRECT, INCORRECT, INCORRECT, INCORRECT, WRONG_POSITION), state)
    }

    @Test
    fun `one letter correct, in the wrong position, appears twice in the guess`() {
        val state = wordle("AAAAO", "OOBBB")
        assertEquals("", listOf(WRONG_POSITION, INCORRECT, INCORRECT, INCORRECT, INCORRECT), state)
    }

    @Test
    fun `one letter correct, but appears twice in guess, once before actual position`() {
        val state = wordle(
            "AOAAA",
            "OOBBB"
        )
        assertEquals("", listOf(INCORRECT, CORRECT, INCORRECT, INCORRECT, INCORRECT), state)
    }

    @Test
    fun `one letter correct, but appears twice in guess, at the actual position and after`() {
        val state = wordle(
            "AOAAA",
            "BOOBB"
        )
        assertEquals("", listOf(INCORRECT, CORRECT, INCORRECT, INCORRECT, INCORRECT), state)
    }

    @Test
    fun `ignores capitalization`() {
        val state = wordle(
            "AaaaA",
            "aaAaA"
        )
        assertEquals("", List(5) { CORRECT }, state)
    }
}