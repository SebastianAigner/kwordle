import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val wordleGame = Game("CRANE")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                WordleGame(wordleGame)
            }
        }
    }
}

@Composable
fun WordleGame(game: Game) {
    val gameState by game.guesses.collectAsState()
    var textFieldState by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        for (g in gameState) {
            ColoredWord(g)
        }
        TextField(textFieldState, onValueChange = {
            textFieldState = it
        })
        Button(onClick = {
            game.guess(textFieldState)
            textFieldState = ""
        }) {
            Text("Submit")
        }
    }
}


@Composable
fun ColoredWord(w: WordleWord) {
    Row(Modifier.fillMaxWidth()) {
        w.letterStates.zip(w.word.asIterable()) { letterState, letter ->
            WordleLetter(letterState, letter)
        }

    }
}

val LetterState.color: Color
    get() {
        return when (this) {
            LetterState.CORRECT -> Green
            LetterState.INCORRECT -> Gray
            LetterState.WRONG_POSITION -> Yellow
        }
    }

@Preview
@Composable
fun WordleLetterA() {
    WordleLetter(LetterState.INCORRECT, 'A')
}

@Preview
@Composable
fun WordleLetter(state: LetterState, letter: Char) {
    Box(Modifier.width(128.dp).height(128.dp).background(state.color), contentAlignment = Alignment.Center) {
        Text("$letter", color = White, fontSize = 3.em)
    }
}