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
        mutableStateOf(
            arrayOf(
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', '○', '●', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', '●', '○', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
                charArrayOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
            )
        )
    }
    var blackTurn by remember { mutableStateOf(true) }

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
                        colors = ButtonDefaults.textButtonColors(backgroundColor = Color.White)
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

private fun insertPointInOthelloGameBoard(
    gameBoard: OthelloGameBoard,
    rowIndex: Int,
    columnIndex: Int,
    team: OthelloTeam,
) = arrayOf(
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.ZERO),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.ONE),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.TWO),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.THREE),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.FOUR),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.FIVE),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.SIX),
    createBoardRow(gameBoard, rowIndex, columnIndex, team, OthelloIndex.SEVEN),
)

private fun createBoardRow(
    gameBoard: OthelloGameBoard,
    rowIndex: Int,
    columnIndex: Int,
    team: OthelloTeam,
    targetRowIndex: OthelloIndex,
) = if (rowIndex == targetRowIndex.index)
    charArrayOf(
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.ZERO),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.ONE),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.TWO),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.THREE),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.FOUR),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.FIVE),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.SIX),
        createBoardElement(gameBoard, columnIndex, team, targetRowIndex, OthelloIndex.SEVEN),
    )
else gameBoard[targetRowIndex.index]

private fun createBoardElement(
    gameBoard: OthelloGameBoard,
    columnIndex: Int,
    team: OthelloTeam,
    targetRowIndex: OthelloIndex,
    targetColumnIndex: OthelloIndex,
) = if (columnIndex == targetColumnIndex.index) team.shape else gameBoard[targetRowIndex.index][targetColumnIndex.index]

enum class OthelloTeam(val shape: Char) {
    BLACK('●'),
    WHITE('○'),
    ANONYMOUS(' '),
}

enum class OthelloIndex(val index: Int) {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),

    MAX(7),
    MIN(0),
}

fun Boolean.turnEnd() = !this

fun OthelloGameBoard.checkEndGame() =
    this.count(OthelloTeam.ANONYMOUS)

fun OthelloGameBoard.count(matchCharacter: OthelloTeam) =
    this.sumOf { row -> row.filter { it == matchCharacter.shape }.count() }

fun applyChainAction(
    gameBoard: OthelloGameBoard,
    rowIndex: Int,
    columnIndex: Int,
) {
    val currentInsertedTargetShape = gameBoard[rowIndex][columnIndex]
    val allSearchRules = listOf(1 to 1, 1 to 0, 1 to -1, 0 to 1, 0 to 0, 0 to -1, -1 to 1, -1 to 0, -1 to -1)

    allSearchRules.forEach { (rowOffset, columnOffset) ->
        modifyPoints(
            gameBoard = gameBoard,
            basedRowIndex = rowIndex,
            basedColumnIndex = columnIndex,
            rowOffset = rowOffset,
            columnOffset = columnOffset,
            targetShape = currentInsertedTargetShape
        )
    }
}

private fun modifyPoints(
    gameBoard: OthelloGameBoard,
    basedRowIndex: Int,
    basedColumnIndex: Int,
    rowOffset: Int,
    columnOffset: Int,
    targetShape: Char,
): OthelloGameBoard {
    var rowIndex = basedRowIndex
    var columnIndex = basedColumnIndex

    val modificationReservationIndices = mutableListOf<Pair<Int, Int>>()

    while (true) {
        rowIndex += rowOffset
        columnIndex += columnOffset

        if (
            !(rowIndex.isOnBoard && columnIndex.isOnBoard) ||
            gameBoard[rowIndex][columnIndex] == OthelloTeam.ANONYMOUS.shape
        ) {
            modificationReservationIndices.clear()
            break
        }
        if (gameBoard[rowIndex][columnIndex] == targetShape) break

        modificationReservationIndices.add(rowIndex to columnIndex)
    }

    modificationReservationIndices.forEach { (row, column) ->
        gameBoard[row][column] = targetShape
    }

    return gameBoard
}

val Int.isOnBoard: Boolean
    get() = this >= OthelloIndex.MIN.index && this <= OthelloIndex.MAX.index