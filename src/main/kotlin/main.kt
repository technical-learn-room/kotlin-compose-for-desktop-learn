import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

typealias OthelloGameBoard = Array<CharArray>

fun main() = Window(
    title = "Othello Game",
    size = IntSize(640, 640),
) {
    var othelloGameBoard by remember {
        mutableStateOf(createDefaultGameBoard())
    }
    var blackTurn by remember { mutableStateOf(true) }
    val othelloGameBoardColors = createDefaultColors()

    MaterialTheme {
        othelloGameBoard.mapIndexed { rowIndex, rowValue ->
            Row(
                modifier = Modifier.absoluteOffset(y = (rowIndex * 75).dp)
            ) {
                rowValue.mapIndexed { columnIndex, columnValue ->
                    Button(
                        onClick = {
                            othelloGameBoard = insertPointInOthelloGameBoard(
                                gameBoard = othelloGameBoard,
                                rowIndex = rowIndex,
                                columnIndex = columnIndex,
                                team = if (blackTurn) OthelloTeam.BLACK else OthelloTeam.WHITE
                            )
                            blackTurn = blackTurn.turnEnd()

                            applyChainAction(
                                gameBoard = othelloGameBoard,
                                rowIndex = rowIndex,
                                columnIndex = columnIndex,
                            )

                            othelloGameBoard.checkEndGame()
                        },
                        modifier = Modifier.weight(4F)
                            .fillMaxWidth(0.125F)
                            .fillMaxHeight(0.125F),
                        colors = ButtonDefaults.textButtonColors(backgroundColor = othelloGameBoardColors[rowIndex][columnIndex])
                    ) {
                        Text(
                            text = columnValue.toString(),
                            fontSize = 50.sp,
                            color = Color.Black,
                        )
                    }
                }
            }
        }
    }
}

fun Boolean.turnEnd() = !this

fun OthelloGameBoard.checkEndGame() =
    this.count(OthelloTeam.ANONYMOUS)

fun OthelloGameBoard.count(matchCharacter: OthelloTeam) =
    this.sumOf { row -> row.filter { it == matchCharacter.shape }.count() }

val Int.isOnBoard: Boolean
    get() = this >= OthelloIndex.MIN.index && this <= OthelloIndex.MAX.index