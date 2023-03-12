import app.cash.turbine.test
import io.sebi.kwordle.game.Game
import io.sebi.kwordle.game.LetterState
import io.sebi.kwordle.game.LetterState.*
import io.sebi.kwordle.game.WordleLetter
import io.sebi.kwordle.game.WordleWord
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GameTest {

    fun wordleWordfromLettersAndStates(letters: String, state: List<LetterState>): WordleWord {
        return WordleWord(letters.asIterable().zip(state) { letter, letterState ->
            WordleLetter(letter, letterState)
        })
    }

    @Test
    fun `adds an all-wrong guess`() = runTest {
        val g = Game("AAAAA")
        g.guess("BBBBB")
        g.guesses.test {
            assertEquals(
                "Most recent state should contain guess",
                listOf(wordleWordfromLettersAndStates("BBBBB", List(5) { INCORRECT })),
                expectMostRecentItem(),
            )
        }
    }

    @Test
    fun `adds two all-wrong guesses`() = runTest {
        val g = Game("AAAAA")
        g.guess("BBBBB")
        g.guess("BBBBB")
        g.guesses.test {
            assertEquals(
                "Most recent state should contain all guesses",
                listOf(
                    wordleWordfromLettersAndStates("BBBBB", List(5) { INCORRECT }),
                    wordleWordfromLettersAndStates("BBBBB", List(5) { INCORRECT }),
                ),
                expectMostRecentItem(),
            )
        }
    }

    @Test
    fun `adds a correct guess`() = runTest {
        val g = Game("AAAAA")
        g.guess("AAAAA")
        g.guesses.test {
            assertEquals(
                "Most recent state should contain all guesses",
                listOf(
                    wordleWordfromLettersAndStates("AAAAA", List(5) { CORRECT }),
                ),
                expectMostRecentItem(),
            )
        }
    }

    @Test
    fun `correctly returns best state for correctly positioned letter`() = runTest {
        val g = Game("ABCDE")
        g.guess("ACBXX")
        assertEquals("A correctly positioned letter should be identified as such", CORRECT, g.bestGuessForLetter('A'))
    }

    @Test
    fun `correctly returns best state for wrongly positioned letter`() = runTest {
        val g = Game("ABCDE")
        g.guess("ACBXX")
        assertEquals(
            "An incorrectly positioned letter should be identified as such",
            WRONG_POSITION,
            g.bestGuessForLetter('B')
        )
    }


    @Test
    fun `correctly returns best state for unguessed letter`() = runTest {
        val g = Game("ABCDE")
        g.guess("ACBXX")
        assertEquals("An unguessed letter should be identified as such", UNGUESSED, g.bestGuessForLetter('Y'))
    }

    @Test
    fun `correctly returns best state for incorrect letter`() {
        val g = Game("ABCDE")
        g.guess("ACBXX")
        assertEquals("A non-existent letter should be identified as such", INCORRECT, g.bestGuessForLetter('X'))
    }

    @Test
    fun `no guessed letters means all letters should be unguessed`() {
        val g = Game("ABCDE")
        assertEquals("A non-existent letter should be identified as such", UNGUESSED, g.bestGuessForLetter('X'))
    }

    @Test
    fun `correctly returns best state for incorrect and unguessed letters`() {
        val g = Game("ABCDE")
        g.guess("XXXXX")
        assertEquals("A non-existent letter should be identified as such", INCORRECT, g.bestGuessForLetter('X'))
        assertEquals("A non-existent letter should be identified as such", UNGUESSED, g.bestGuessForLetter('Y'))
    }

    @Test
    fun `accepts a guess that is too short`() {
        val g = Game("ABCDE")
        g.guess("ABC")
        assertEquals("A guess that is too short should be accepted", 1, g.guesses.value.size)
    }

    @Test
    fun `accepts a guess that is too long`() {
        val g = Game("ABCDE")
        g.guess("ABCDEFGH")
        assertEquals("A guess that is too long should be accepted", 1, g.guesses.value.size)
    }

}