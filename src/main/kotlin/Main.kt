import LetterState.*

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}

enum class LetterState {
    CORRECT,
    INCORRECT,
    WRONG_POSITION
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
