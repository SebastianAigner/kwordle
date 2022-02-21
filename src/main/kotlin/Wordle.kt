import LetterState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

enum class LetterState {
    CORRECT,
    INCORRECT,
    WRONG_POSITION,
    UNGUESSED
}

data class WordleWord(val letters: List<WordleLetter>)
data class WordleLetter(val letter: Char, val letterState: LetterState)

class Game(val realWord: String) {
    fun bestGuessForLetter(c: Char): LetterState {
        val letterStates = guesses.value
            .flatMap { it.letters }
            .filter { it.letter == c }
            .map { it.letterState }
        if (letterStates.any { it == CORRECT }) return CORRECT
        if (letterStates.any { it == WRONG_POSITION }) return WRONG_POSITION
        if (letterStates.any { it == INCORRECT }) return INCORRECT
        return UNGUESSED
    }

    var guesses = MutableStateFlow(listOf<WordleWord>())
    fun guess(guess: String) {
        val wordleWord =
            guess.asIterable().zip(wordle(realWord, guess)) { letter, state -> WordleLetter(letter, state) }
        guesses.update {
            it + WordleWord(wordleWord)
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
