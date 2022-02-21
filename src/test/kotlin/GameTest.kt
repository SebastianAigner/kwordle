import app.cash.turbine.test
import io.sebi.kwordle.game.Game
import io.sebi.kwordle.game.LetterState
import io.sebi.kwordle.game.LetterState.*
import io.sebi.kwordle.game.WordleLetter
import io.sebi.kwordle.game.WordleWord
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class GameTest {

    fun wordleWordfromLettersAndStates(letters: String, state: List<LetterState>): WordleWord {
        return WordleWord(letters.asIterable().zip(state) { letter, state ->
            WordleLetter(letter, state)
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
    fun `correctly returns best state for letter`() = runTest {
        val g = Game("ABCDE")
        g.guess("ACBXX")
        assertEquals("A correctly positioned letter should be identified as such", CORRECT, g.bestGuessForLetter('A'))
        assertEquals(
            "An incorrectly positioned letter should be identified as such",
            WRONG_POSITION,
            g.bestGuessForLetter('B')
        )
        assertEquals("A non-existent letter should be identified as such", INCORRECT, g.bestGuessForLetter('X'))
        assertEquals("An unguessed letter should be identified as such", UNGUESSED, g.bestGuessForLetter('Y'))
    }
}