import androidx.compose.ui.graphics.Color

val allSearchRules = listOf(1 to 1, 1 to 0, 1 to -1, 0 to 1, 0 to -1, -1 to 1, -1 to 0, -1 to -1)

fun insertPointInOthelloGameBoard(
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

fun applyChainAction(
    gameBoard: OthelloGameBoard,
    rowIndex: Int,
    columnIndex: Int,
) {
    val currentInsertedTargetShape = gameBoard[rowIndex][columnIndex]

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
    val modificationReservationIndices =
        searchChangeablePoint(gameBoard, basedRowIndex, basedColumnIndex, rowOffset, columnOffset, targetShape)

    modificationReservationIndices.forEach { (row, column) ->
        gameBoard[row][column] = targetShape
    }

    return gameBoard
}

private fun searchChangeablePoint(
    gameBoard: OthelloGameBoard,
    basedRowIndex: Int,
    basedColumnIndex: Int,
    rowOffset: Int,
    columnOffset: Int,
    targetShape: Char,
): List<Pair<Int, Int>> {
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

    return modificationReservationIndices
}

fun highlightAvailablePoint(gameBoard: OthelloGameBoard, gameBoardColors: GameBoardColors, team: OthelloTeam) {
    for (row in OthelloIndex.MIN.index..OthelloIndex.MAX.index) {
        for (column in OthelloIndex.MIN.index..OthelloIndex.MAX.index) {
            var color = Color.White
            allSearchRules.forEach { (rowOffset, columnOffset) ->
                val isAvailablePoint = isAvailablePoint(
                    gameBoard = gameBoard,
                    basedRowIndex = row,
                    basedColumnIndex = column,
                    rowOffset = rowOffset,
                    columnOffset = columnOffset,
                    team = team,
                )
                color = if (isAvailablePoint || color == Color.Yellow) Color.Yellow else Color.White
            }
            gameBoardColors[row][column] = color
        }
    }
}

private fun isAvailablePoint(
    gameBoard: OthelloGameBoard,
    basedRowIndex: Int,
    basedColumnIndex: Int,
    rowOffset: Int,
    columnOffset: Int,
    team: OthelloTeam,
) = searchChangeablePoint(gameBoard, basedRowIndex, basedColumnIndex, rowOffset, columnOffset, team.shape).isNotEmpty()
        && gameBoard[basedRowIndex][basedColumnIndex] == OthelloTeam.ANONYMOUS.shape

private val Int.isOnBoard: Boolean
    get() = this >= OthelloIndex.MIN.index && this <= OthelloIndex.MAX.index