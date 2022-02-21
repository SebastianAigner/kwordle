import LetterState.*
import kotlinx.coroutines.flow.MutableStateFlow
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
        val normalizedGuess = guess.trim().uppercase()
        val wordleWord =
            normalizedGuess.asIterable()
                .zip(wordle(realWord, normalizedGuess)) { letter, state -> WordleLetter(letter, state) }
        guesses.update {
            it + WordleWord(wordleWord)
        }
    }
}

fun wordle(realWord: String, guess: String): List<LetterState> {
    val normalizedRealWord = realWord.uppercase()
    val normalizedGuess = guess.uppercase()
    val output = MutableList(normalizedRealWord.length) { INCORRECT }

    val lettersInRealWord = normalizedRealWord.toMutableList()

    for (index in output.indices) {
        val guessLetter = normalizedGuess[index]
        val realLetter = normalizedRealWord[index]
        if (guessLetter == realLetter) {
            output[index] = CORRECT
            lettersInRealWord -= realLetter
        }
    }

    for (index in output.indices) {
        if (output[index] != INCORRECT) continue
        val guessLetter = normalizedGuess[index]
        if (guessLetter in lettersInRealWord) {
            output[index] = WRONG_POSITION
            lettersInRealWord -= guessLetter
        }
    }
    return output
}
