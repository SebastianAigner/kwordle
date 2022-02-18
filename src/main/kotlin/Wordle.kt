import LetterState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

enum class LetterState {
    CORRECT,
    INCORRECT,
    WRONG_POSITION
}

data class WordleWord(val word: String, val letterStates: List<LetterState>)

class Game(val realWord: String) {
    var guesses = MutableStateFlow(listOf<WordleWord>())
    fun guess(guess: String) {
        guesses.update {
            it + WordleWord(guess, wordle(realWord, guess))
        }
    }
}

suspend fun main() {
    val g = Game("foo")
    g.guesses.take(1)
}

fun wordle(realWord: String, guess: String): List<LetterState> {
    val output = MutableList(realWord.length) { INCORRECT }

    val lettersInRealWord = realWord.toMutableList()

    for (index in output.indices) {
        val guessLetter = guess[index]
        val realLetter = realWord[index]
        if (guessLetter == realLetter) {
            output[index] = CORRECT
            lettersInRealWord -= realLetter
        }
    }

    for (index in output.indices) {
        if (output[index] != INCORRECT) continue
        val guessLetter = guess[index]
        if (guessLetter in lettersInRealWord) {
            output[index] = WRONG_POSITION
            lettersInRealWord -= guessLetter
        }
    }
    return output
}
