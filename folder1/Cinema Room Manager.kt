@Suppress("MagicNumber")
const val FRONT_HALF = 10
const val BACK_HALF = 8
fun main() {
    var ticketprice = 10
    var total = 0
    var numberOfTickets = 0
    var currentIncome = 0
    
    println("Enter the number of rows:")
    val rows = readln().toInt()
    println("Enter the number of seats in each row:")
    val seats = readln().toInt()
    println()
    var totalSeats : Int = rows * seats
    var tickets = MutableList(rows) {
        "S".repeat(seats).toMutableList()
    }
    
    totalSeats = rows * seats
    if (totalSeats < 60) {

        total = totalSeats * FRONT_HALF
    } else if (rows % 2 == 0) {

        total = (totalSeats / 2) * FRONT_HALF + (totalSeats / 2) * BACK_HALF 
    } else {

        total = ((rows/2)*seats) * FRONT_HALF + ((rows - (rows/2))*seats) * BACK_HALF
    }

    while (true) {
        println("1. Show the seats")
        println("2. Buy a ticket")
        println("3. Statistics")
        println("0. Exit")
        
        val choose = readln().toInt()
        println()
        when(choose){
            1 -> {
                println()
                println("Cinema:")
                print(" ")
                for(first in 1..seats) {
                    print(" $first")
                }
                println()
                var c = 1
                for (j in 0..rows - 1) {
                    print(c)
                    print(" ${tickets[j].joinToString(" ")}")
                    println()
                    c++
                }
            }
            2 -> {
                while(true) {
                    try {
                        println("Enter a row number:")
                        val row = readln().toInt()
                        println("Enter a seat number in that row:")
                        val seat = readln().toInt()
                        println()
                        if (tickets[row-1][seat-1] == 'B') {
                            println("That ticket has already been purchased!")
                            println()
                            continue
                        } else {
                            tickets[row-1][seat-1] = 'B'
                            if (totalSeats < 60) {
                                ticketprice = FRONT_HALF
                            } else if (row > 4) {
                                ticketprice = BACK_HALF
                            } else {
                                ticketprice = FRONT_HALF
                            }
                            numberOfTickets++
                            println()
                            currentIncome += ticketprice
                            println("Ticket price: $$ticketprice")
                            println()
                            break
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        println("Wrong input!")
                        println()
                    }
                }
            }
            3 -> {
                println("Number of purchased tickets: $numberOfTickets")
                var percentage = 0.0
                try {
                    percentage = numberOfTickets.toDouble() / totalSeats * 100
                } catch (e: ArithmeticException) {
                    percentage = 0.0
                }
                var formatPercentage = "%.2f".format(percentage)
                println("Percentage: $formatPercentage%")
                println("Current income: $$currentIncome")
                println("Total income: $$total")
                println()
            }
            0 -> break
        }
        if (choose == 0) break
    }
}
