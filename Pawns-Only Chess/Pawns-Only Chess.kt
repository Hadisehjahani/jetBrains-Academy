package chess

fun chessboard(board: MutableList<MutableList<Char>>) {
    for (i in 8 downTo 1) {
        println("  +---+---+---+---+---+---+---+---+")
        println("$i | " + board[i].joinToString(" | ") + " |")
    }
    println("  +---+---+---+---+---+---+---+---+")
    println("    a   b   c   d   e   f   g   h")
}

fun isStalemate(board: MutableList<MutableList<Char>>, pawnMoves: MutableList<String> , playerState : Char , otherPlayer:Char) {
    var left: Int
    var right: Int
    var nextRow = 0

    for (x in board) {
        for (i in x.indices) {
            if (x[i] == playerState) {
                pawnMoves.add("${board[0][i]}${board.indexOf(x)}")
                val row = board.indexOf(x)
                left = i - 1
                right = i + 1
                when (playerState) {
                    'B' -> nextRow = row - 1
                    'W' -> nextRow = row + 1
                }
                if (left <= 0) {
                    left = 0
                }
                if (right >= 7) {
                    right = 7
                }
                if (board[nextRow][i] == otherPlayer && ((board[nextRow][left] == ' ') && ((board[nextRow][right] == ' ') || (board[nextRow][right] == otherPlayer))) && !((right == 7 || left == 0) && (board[nextRow][i] != otherPlayer))) {
                    stalemateMessage()
                    break
                }
            }
        }
    }
}

fun stalemateMessage() {
    println("Stalemate!")
    println("Bye!")
}

fun main() {

    val board = mutableListOf(
        mutableListOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
    )
    val whiteMoves = mutableListOf<String>()

    val previousMoves = mutableListOf("0000")
    var previousIndex: Int
    var previousMove: String

    // get players name
    println(" Pawns-Only Chess")
    println("First Player's name:")
    val firstName = readLine()!!
    println("Second Player's name:")
    val secondName = readLine()!!

    chessboard(board)

    val whiteRegex = "[a-h][2-7][a-h][3-8]".toRegex()
    val blackRegex = "[a-h][2-7][a-h][1-6]".toRegex()
    var player = firstName
    var move: String
    while (true) {
        val pawnMoves = mutableListOf<String>()
        isStalemate(board , pawnMoves , 'W' , 'B')

        println("$player's turn:")
        move = readLine()!!

        if (move == "exit") {
            println("Bye!")
            break
        }

        previousMoves.add(move)
        previousIndex = previousMoves.lastIndex - 1
        // convert move to parts
        // e4d5
        val colStartName = move.subSequence(0, 1) //e
        val start = move.subSequence(1, 2) //4
        val colEndName = move.subSequence(2, 3) //d
        val end = move.subSequence(3, 4) //5

        val startCol = board[0].indexOf(colStartName.first()) //4
        val startPoint = start.toString().toInt() //4
        val endCol = board[0].indexOf(colEndName.first()) //3
        val endPoint = end.toString().toInt() //5


        var leftSide = startCol - 1
        var rightSide = startCol + 1
        if (colStartName.first() == 'a') {
            leftSide = 0
        }
        if (colStartName.first() == 'h') {
            rightSide = 7
        }
        var pawnRegex: Regex
        pawnRegex =
            "${colStartName}${startPoint}[${board[0][leftSide]}-${board[0][rightSide]}][${startPoint + 1}-${startPoint + 2}]".toRegex()

        previousMove = previousMoves[previousIndex] //d7d5
        // convert previous move to parts
        val pEndPoint = previousMove.subSequence(3, 4).toString().toInt() //7
        val pStartPoint = previousMove.subSequence(1, 2).toString().toInt() //7
        val pColEndName = previousMove.subSequence(2, 3) //d

        var reg: Regex
        // player one
        when (player) {
            firstName -> {
                whiteMoves.add(move)
                reg = "[a-h][2-7][a-h]${startPoint + 1}".toRegex()
                if (board[2][startCol] == 'W') {
                    reg = "[a-h][2-7][a-h][${startPoint + 1}-${startPoint + 2}]".toRegex()
                }
                if (board[startPoint][startCol] == ' ' || board[startPoint][startCol] == 'B') {
                    println("No white pawn at $colStartName$start")
                } else if (!whiteRegex.matches(move) || !(reg.matches(move)) || board[endPoint][startCol] == 'B' || !pawnRegex.matches(
                        move
                    ) || (colStartName != colEndName && endPoint != 6 && board[endPoint][endCol] != 'B')
                ) {
                    println("Invalid Input")
                } else {
                    if (board[endPoint][startCol] == ' ' || board[endPoint][endCol] == 'B') {
                        if (startPoint == 5 && board[endPoint][endCol] == ' ') {
                            if (pStartPoint == 7 && pEndPoint == 6 && colEndName == pColEndName) {
                                // Active en passant
                                board[endPoint][endCol] = board[startPoint][startCol]
                                board[startPoint][startCol] = ' '
                                board[5][endCol] = ' '
                            } else {
                                board[endPoint][endCol] = board[startPoint][startCol]
                                board[startPoint][startCol] = ' '
                            }
                        } else {
                            board[endPoint][endCol] = board[startPoint][startCol]
                            board[startPoint][startCol] = ' '
                        }

                        chessboard(board)
                        if (board[8][endCol] == 'W' || !(board.any { it.contains('B') })) {
                            println("White Wins!")
                            println("Bye!")
                            break
                        }
                    }
                }
                // player two
            }
            secondName -> {
                pawnRegex =
                    "${colStartName}${startPoint}[${board[0][leftSide]}-${board[0][rightSide]}][${startPoint - 2}-${startPoint - 1}]".toRegex()
                reg = "[a-h][2-7][a-h]${startPoint - 1}".toRegex()
                if (board[7][startCol] == 'B') {
                    reg = "[a-h][2-7][a-h][${startPoint - 2}-${startPoint - 1}]".toRegex()
                }
                if (board[startPoint][startCol] == ' ' || board[startPoint][startCol] == 'W') {
                    println("No black pawn at $colStartName$start")
                } else if (!blackRegex.matches(move) || !(reg.matches(move)) || board[endPoint][startCol] == 'W' || !pawnRegex.matches(
                        move
                    ) || (colStartName != colEndName && endPoint != 3 && board[endPoint][endCol] != 'W')
                ) {
                    println("Invalid Input")
                } else {
                    if (board[endPoint][startCol] == ' ' || board[endPoint][endCol] == 'B') {
                        if (startPoint == 4 && board[endPoint][endCol] == ' ') {
                            if (pStartPoint == 2 && colEndName == pColEndName) {
                                // Active en passant
                                board[endPoint][endCol] = board[startPoint][startCol]
                                board[startPoint][startCol] = ' '
                                board[4][endCol] = ' '
                            } else {
                                board[endPoint][endCol] = board[startPoint][startCol]
                                board[startPoint][startCol] = ' '
                            }
                        } else {
                            board[endPoint][endCol] = board[startPoint][startCol]
                            board[startPoint][startCol] = ' '
                        }
                        chessboard(board)
                        if (board[1][endCol] == 'B' || !(board.any { it.contains('W') })) {
                            println("Black Wins!")
                            println("Bye!")
                            break
                        }
                    }
                }
            }
        }
        player = if (player == firstName) secondName else firstName
    }
}
