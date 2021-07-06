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

var title = "Othello Game"

fun main() = Window(
    title = title,
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
                            if (othelloGameBoardColors[rowIndex][columnIndex] == Color.Yellow) {
                                othelloGameBoard = insertPointInOthelloGameBoard(
                                    gameBoard = othelloGameBoard,
                                    rowIndex = rowIndex,
                                    columnIndex = columnIndex,
                                    team = blackTurn.team,
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
                                    team = blackTurn.team,
                                )

                                if (othelloGameBoardColors.checkEndGame()) {
                                    val blackCount = othelloGameBoard.count(OthelloTeam.BLACK)
                                    val whiteCount = othelloGameBoard.count(OthelloTeam.WHITE)

                                    when {
                                        blackCount > whiteCount -> {
                                            title = "Winner is Black"
                                            othelloGameBoard.end(OthelloTeam.BLACK)
                                        }
                                        blackCount < whiteCount -> {
                                            title = "Winner is White"
                                            othelloGameBoard.end(OthelloTeam.WHITE)
                                        }
                                        else -> {
                                            othelloGameBoard.end(OthelloTeam.ANONYMOUS)
                                        }
                                    }
                                }
                            }
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

fun GameBoardColors.checkEndGame() =
    this.sumOf { row -> row.filter { it == Color.Yellow }.count() } <= 0

fun OthelloGameBoard.count(matchCharacter: OthelloTeam) =
    this.sumOf { row -> row.filter { it == matchCharacter.shape }.count() }

val Boolean.team: OthelloTeam
    get() = if (this) OthelloTeam.BLACK else OthelloTeam.WHITE

fun OthelloGameBoard.end(winner: OthelloTeam) {
    when (winner) {
        OthelloTeam.BLACK, OthelloTeam.WHITE ->
            this.forEachIndexed { rowIndex, rowValue ->
                rowValue.forEachIndexed { columnIndex, _ ->
                    this[rowIndex][columnIndex] = winner.shape
                }
            }
        OthelloTeam.ANONYMOUS ->
            this.forEachIndexed { rowIndex, rowValue ->
                rowValue.forEachIndexed { columnIndex, _ ->
                    this[rowIndex][columnIndex] = if (rowIndex % 2 == 0) OthelloTeam.BLACK.shape else OthelloTeam.WHITE.shape
                }
            }
    }
}