import java.io.File

fun partOne(input: String) {
    val parts = input.split("\n\n")
    println(parts.maxOfOrNull { x -> x.split("\n").sumOf { it.toInt() } })
}

fun partTwo(input: String) {
    println(input.split("\n\n")
        .map { x -> x.split("\n").sumOf { it.toInt() } }
        .sortedDescending()
        .take(3)
        .sum())
}

fun main() {
    val file = File("data/input.txt")
    val text = file.readText()

    partTwo(text)
}