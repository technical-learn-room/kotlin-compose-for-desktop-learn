import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

typealias OthelloGameBoard = Array<CharArray>
typealias GameBoardColors = Array<Array<Color>>

fun main() = Window(
    title = "Othello Game",
    size = IntSize(640, 640),
) {
    var othelloGameBoard by remember {
        mutableStateOf(createDefaultGameBoard())
    }
    var blackTurn by remember { mutableStateOf(true) }
    val othelloGameBoardColors = createDefaultColors()

    highlightAvailablePoint(
        gameBoard = othelloGameBoard,
        gameBoardColors = othelloGameBoardColors,
        team = blackTurn.team
    )

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
                                team = blackTurn.team
                            )
                            blackTurn = blackTurn.turnEnd()

                            applyChainAction(
                                gameBoard = othelloGameBoard,
                                rowIndex = rowIndex,
                                columnIndex = columnIndex,
                            )

                            highlightAvailablePoint(
                                gameBoard = othelloGameBoard,
                                gameBoardColors = othelloGameBoardColors,
                                team = blackTurn.team
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

private fun OthelloGameBoard.count(matchCharacter: OthelloTeam) =
    this.sumOf { row -> row.filter { it == matchCharacter.shape }.count() }

val Boolean.team: OthelloTeam
    get() = if (this) OthelloTeam.BLACK else OthelloTeam.WHITE