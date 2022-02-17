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
    val output = MutableList(realWord.length) { LetterState.INCORRECT }

    val lettersInRealWord = realWord.toMutableList()
    for(index in 0..realWord.lastIndex) {
        if(realWord[index] == guess[index]) {
            output[index] = LetterState.CORRECT
            lettersInRealWord.remove(realWord[index])
        }
    }
    for(index in 0..realWord.lastIndex) {
        if(output[index] != LetterState.INCORRECT) continue
        if(guess[index] in lettersInRealWord) {
            output[index] = LetterState.WRONG_POSITION
            lettersInRealWord.remove(guess[index])
        }
    }
    return output
}
