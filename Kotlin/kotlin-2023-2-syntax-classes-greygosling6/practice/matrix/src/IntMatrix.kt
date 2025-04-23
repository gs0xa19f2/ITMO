class IntMatrix {
    val rows: Int
    val columns: Int
    private val matrix: IntArray

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(rows: Int, columns: Int) {
        require(rows > 0 && columns > 0) {
            "Number of rows: $rows and columns: $columns must be greater than zero"
        }
        this.rows = rows
        this.columns = columns
        this.matrix = IntArray(rows * columns)
    }

    private fun computeIndex(row: Int, col: Int): Int {
        require(row in 0..<rows && col in 0..<columns) {
            "Index out of bounds - row: $row, rows: $rows; col: $col, columns: $columns"
        }
        return row * rows + col
    }

    operator fun get(row: Int, col: Int): Int = matrix[computeIndex(row, col)]
    operator fun set(row: Int, col: Int, value: Int) {
        matrix[computeIndex(row, col)] = value
    }
}
