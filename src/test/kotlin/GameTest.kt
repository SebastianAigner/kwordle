import LetterState.CORRECT
import LetterState.INCORRECT
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class GameTest {
    @Test
    fun `adds an all-wrong guess`() = runTest {
        val g = Game("AAAAA")
        g.guess("BBBBB")
        g.guesses.test {
            assertEquals(
                "Most recent state should contain guess",
                listOf(WordleWord("BBBBB", List(5) { INCORRECT })),
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
                    WordleWord("BBBBB", List(5) { INCORRECT }),
                    WordleWord("BBBBB", List(5) { INCORRECT }),
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
                    WordleWord("AAAAA", List(5) { CORRECT }),
                ),
                expectMostRecentItem(),
            )
        }
    }
}