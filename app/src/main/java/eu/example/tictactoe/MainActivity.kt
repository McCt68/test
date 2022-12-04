package eu.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Check if someone has won
enum class Win {
	PLAYER,
	AI,
	DRAW
}

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			TicTacToeTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colors.background
				) {
					TTTScreen()
				}
			}
		}
	}
}

@Composable
fun TTTScreen() {

	// true = Player's turn - false AI turn
	val playerTurnState = remember { mutableStateOf(true) }

	// true = Player's move - false AI move - null = no move
	// list of nullable Booleans
	val movesState = remember {
		mutableStateListOf<Boolean?>(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
//			true,
	//		null,
//			false,
//			null,
//			true,
//			null,
//			false,
//			null,
//			null
		)
	}

	// set the state of the game if anyone has won or if it a draw
	val winState = remember {
		mutableStateOf<Win?>(null)
	}

	// return the position in a box x and y cordinates
	val onTap: (Offset) -> Unit = {
		if (playerTurnState.value && winState.value == null) {
			val x = (it.x / 333).toInt()
			val y = (it.y / 333).toInt()
			val positionInMoves = y * 3 + x
			if (movesState[positionInMoves] == null) {
				movesState[positionInMoves] = true
				playerTurnState.value = false
				winState.value = checkEndGame(movesState)
			}
		}
	}

	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))

		Header(playerTurn = playerTurnState.value)

		Board(moves = movesState, onTap)

		// AI moves
		if (!playerTurnState.value && winState.value == null) {
			CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

			val coroutineScope = rememberCoroutineScope()
			LaunchedEffect(key1 = Unit) {
				coroutineScope.launch {
					delay(1500L)
					while (true) {
						val i = Random.nextInt(9)
						if (movesState[i] == null) {
							movesState[i] = false
							playerTurnState.value = true
							winState.value = checkEndGame(movesState)
							break
						}
					}
				}
			}
		}

		// do when someone has won or draw
		// I proberly have to make functionality of restart game here ??
		if (winState.value != null) {
			when (winState.value) {
				Win.PLAYER -> {
					Text(
						text = "Player has won \uD83C\uDF89",
						fontSize = 25.sp
					) // concatenate emoji
				}
				Win.AI -> {
					Text(text = "AI has won", fontSize = 25.sp)
				}
				Win.DRAW -> {
					Text(text = "The game was a draw", fontSize = 25.sp)
				}
			}
			// Restart game
			Button(onClick = {
				playerTurnState.value = true
				winState.value = null
				for (i in 0..8) {
					movesState[i] = null
				}
			}) {
				Text(text = "Start new game")
			}
		}
	}
}

// Check if game is over
// m is a list of positions I think
fun checkEndGame(m: List<Boolean?>): Win? {
	// Instantiate a Win object from the ENum class, with initial value of null
	var win: Win? = null

	// Check fo player win
	if (
	// horizontal
		(m[0] == true && m[1] == true && m[2] == true) ||
		(m[3] == true && m[4] == true && m[5] == true) ||
		(m[6] == true && m[7] == true && m[8] == true) ||
		// vertical
		(m[0] == true && m[3] == true && m[6] == true) ||
		(m[1] == true && m[4] == true && m[7] == true) ||
		(m[2] == true && m[5] == true && m[8] == true) ||
		// Diagonal
		(m[0] == true && m[4] == true && m[8] == true) ||
		(m[2] == true && m[4] == true && m[6] == true)
	)
	// Set the win object to player win
		win = Win.PLAYER

	// Check for AI win
	if (
	// horizontal
		(m[0] == false && m[1] == false && m[2] == false) ||
		(m[3] == false && m[4] == false && m[5] == false) ||
		(m[6] == false && m[7] == false && m[8] == false) ||
		// vertical
		(m[0] == false && m[3] == false && m[6] == false) ||
		(m[1] == false && m[4] == false && m[7] == false) ||
		(m[2] == false && m[5] == false && m[8] == false) ||
		// Diagonal
		(m[0] == false && m[4] == false && m[8] == false) ||
		(m[2] == false && m[4] == false && m[6] == false)
	)
	// Set the win object to AI win
		win = Win.AI

	// Draw
	if (win == null) {
		// check if all moves in the grid is taken
		var aviableMove = false
		for (i in 0..8) {
			if (m[i] == null)
				aviableMove = true
		}
		if (!aviableMove)
			win = Win.DRAW
	}
	return win
}

@Composable
fun Header(playerTurn: Boolean) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceAround
	) {
		val playerBoxColor = if (playerTurn) Color.Blue else Color.LightGray
		val aiBoxColor = if (playerTurn == false) Color.LightGray else Color.Red// Right ?

		Box(
			modifier = Modifier
				.width(100.dp)
				.background(playerBoxColor)
		) {
			Text(
				text = "Player", modifier = Modifier
					.padding(8.dp)
					.align(Alignment.Center)
			)
		}
		Spacer(modifier = Modifier.width(50.dp))

		Box(
			modifier = Modifier
				.width(100.dp)
				.background(aiBoxColor)
		) {
			Text(
				text = "AI", modifier = Modifier
					.padding(8.dp)
					.align(Alignment.Center)
			)
		}
	}
}

@Composable
fun Board(
	moves: List<Boolean?>,
	onTap: (Offset) -> Unit
) {
	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.padding(32.dp)
			.background(Color.LightGray)
			.pointerInput(Unit) {
				detectTapGestures(onTap = onTap)
			}
	) {
		Column(
			verticalArrangement = Arrangement
				.SpaceEvenly,
			modifier = Modifier
				.fillMaxSize(1f)
		) {
			Row(
				modifier = Modifier
					.height(2.dp)
					.fillMaxWidth(1f)
					.background(Color.Black)
			) {

			}
			Row(
				modifier = Modifier
					.height(2.dp)
					.fillMaxWidth(1f)
					.background(Color.Black)
			) {

			}

		}
		Row(
			horizontalArrangement = Arrangement.SpaceEvenly,
			modifier = Modifier.fillMaxSize(1f)
		) {
			Column(
				modifier = Modifier
					.width(2.dp)
					.fillMaxHeight(1f)
					.background(Color.Black)
			) {

			}
			Column(
				modifier = Modifier
					.width(2.dp)
					.fillMaxHeight(1f)
					.background(Color.Black)
			) {

			}
		}

		// Grid of 3 * 3 rows -- 0, 1, 2 rows
		Column(modifier = Modifier.fillMaxSize(1f)) {
			for (i in 0..2) {
				Row(modifier = Modifier.weight(1f)) {
					for (j in 0..2) {
						Column(modifier = Modifier.weight(1f)) {
							GetComposabelFromMoves(move = moves[i * 3 + j])
						}
					}
				}
			}
		}
	}
}


// tranforms a move into an image we can display
@Composable
fun GetComposabelFromMoves(move: Boolean?) {
	when (move) {
		true -> Image(
			painter = painterResource(id = R.drawable.ic_x),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(1f),
			colorFilter = ColorFilter.tint(Color.Blue)
		)
		false -> Image(
			painter = painterResource(id = R.drawable.ic_o),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(1f),
			colorFilter = ColorFilter.tint(Color.Red)
		)
		null -> Image(
			painter = painterResource(id = R.drawable.ic_null),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(1f)
		)
	}

}

