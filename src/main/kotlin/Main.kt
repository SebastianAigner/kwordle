import LetterState.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val Shapes = Shapes(
    small = RoundedCornerShape(0f),
    medium = RoundedCornerShape(0f),
    large = RoundedCornerShape(0f),
)


fun main() = application {
    val wordleGame = Game("CRANE")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kwordle for Desktop",
        state = rememberWindowState(width = 5 * 128.dp, height = 8 * 128.dp),
    ) {
        MaterialTheme(shapes = Shapes) {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                WordleGame(wordleGame)
            }
        }
    }
}

@Composable
fun WordleGame(game: Game) {
    val gameState by game.guesses.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var textFieldState by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        WordInput(textFieldState, onChange = { textFieldState = it }) {
            textFieldState = ""
            game.guess(it)
            scope.launch {
                delay(100)
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
        Keyboard(game) {
            textFieldState += it
        }
        ColoredWords(gameState, scrollState)
    }
}

@Composable
fun WordInput(textFieldState: String, onChange: (String) -> Unit, onSubmit: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.Center) {
        OutlinedTextField(textFieldState, modifier = Modifier.fillMaxWidth().weight(3f), onValueChange = {
            onChange(it)
        })
        Button(modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f), onClick = {
            onSubmit(textFieldState)
        }) {
            Text("Submit")
        }
    }
}

@Composable
fun ColoredWords(gameState: List<WordleWord>, scrollState: ScrollState) {
    Column(modifier = Modifier.fillMaxHeight().verticalScroll(scrollState)) {
        for (g in gameState) {
            ColoredWord(g)
        }
    }
}


@Composable
fun ColoredWord(w: WordleWord) {
    Row(Modifier.fillMaxWidth()) {
        for ((idx, wordleLetter) in w.letters.withIndex()) {
            val (char, state) = wordleLetter
            Box(Modifier.weight(1f)) {
                WordleLetter(state, char, idx)
            }
        }
    }
}

@Composable
fun Keyboard(g: Game, onKeyClicked: (Char) -> Unit) {
    val keyboard = listOf(
        "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (row in keyboard) {
                Row(horizontalArrangement = Arrangement.Center) {
                    for (letter in row) {
                        val state = g.bestGuessForLetter(letter)
                        Crossfade(targetState = state) { state ->
                            Box(
                                Modifier.clickable { onKeyClicked(letter) }.padding(1.dp).border(1.dp, White)
                                    .width(24.dp).height(32.dp)
                                    .background(state.color), contentAlignment = Alignment.Center
                            ) {
                                Text("$letter", color = state.textColor, fontSize = 1.em)
                            }
                        }
                    }
                }
            }
        }
    }
}

val LetterState.color: Color
    get() {
        return when (this) {
            CORRECT -> Color(0xFF67A760) // Green
            INCORRECT -> Black
            WRONG_POSITION -> Color(0xFFC8B359) // Yellow
            UNGUESSED -> White
        }
    }

val LetterState.textColor: Color
    get() {
        return when (this) {
            UNGUESSED -> Black
            else -> White
        }
    }

@Preview
@Composable
fun WordleLetterA() {
    WordleLetter(INCORRECT, 'A', 0)
}

@Preview
@Composable
fun WordleLetter(state: LetterState, letter: Char, index: Int) {
    var shouldBeVisible by remember { mutableStateOf(false) }
    LaunchedEffect(letter) {
        delay(200L * index)
        shouldBeVisible = true
    }
    AnimatedVisibility(visible = shouldBeVisible, enter = slideInHorizontally() + fadeIn()) {
        Box(
            Modifier.aspectRatio(1.0f).border(1.dp, White).fillMaxWidth().background(state.color),
            contentAlignment = Alignment.Center
        ) {
            Text("$letter", color = state.textColor, fontSize = 3.em)
        }
    }
}