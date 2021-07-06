import androidx.compose.ui.graphics.Color

fun createDefaultGameBoard() =
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

fun createDefaultColors() =
    arrayOf(
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.Yellow, Color.White, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.Yellow, Color.White, Color.White, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.White, Color.Yellow, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.Yellow, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White),
        arrayOf(Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White, Color.White),
    )