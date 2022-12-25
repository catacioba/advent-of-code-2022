package aoc

import aoc.ch01.Ch01
import aoc.ch02.Ch02
import aoc.ch03.Ch03
import aoc.ch04.Ch04
import aoc.ch05.Ch05
import aoc.ch06.Ch06
import aoc.ch07.Ch07
import aoc.ch08.Ch08
import aoc.ch09.Ch09
import aoc.ch10.Ch10
import aoc.ch11.Ch11
import aoc.ch12.Ch12
import aoc.ch13.Ch13
import aoc.ch14.Ch14
import aoc.ch15.Ch15
import aoc.ch16.Ch16
import aoc.ch17.Ch17
import aoc.ch18.Ch18
import aoc.ch19.Ch19
import aoc.ch20.Ch20
import aoc.ch21.Ch21
import aoc.ch22.Ch22
import aoc.ch23.Ch23
import aoc.ch24.Ch24
import aoc.ch25.Ch25
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File

fun challengeToString(challenge: Int): String {
    if (challenge > 25 || challenge < 1) {
        throw IllegalArgumentException("Invalid challenge $challenge")
    }
    return if (challenge < 10) "0$challenge" else "$challenge"
}

fun readTestFiles(challenge: Int): Array<out File> {
    val dir = File("data/ch${challengeToString(challenge)}/")
    return (dir.listFiles { _, name -> name.startsWith("test") })?.apply { sortBy { it.absolutePath } }!!
}

fun readInputFile(challenge: Int): File {
    return File("data/ch${challengeToString(challenge)}/input.txt")
}

fun getChallenge(challenge: Int): Challenge {
    return when (challenge) {
        1 -> Ch01()
        2 -> Ch02()
        3 -> Ch03()
        4 -> Ch04()
        5 -> Ch05()
        6 -> Ch06()
        7 -> Ch07()
        8 -> Ch08()
        9 -> Ch09()
        10 -> Ch10()
        11 -> Ch11()
        12 -> Ch12()
        13 -> Ch13()
        14 -> Ch14()
        15 -> Ch15()
        16 -> Ch16()
        17 -> Ch17()
        18 -> Ch18()
        19 -> Ch19()
        20 -> Ch20()
        21 -> Ch21()
        22 -> Ch22()
        23 -> Ch23()
        24 -> Ch24()
        25 -> Ch25()
        else -> {
            throw java.lang.IllegalArgumentException("Missing challenge $challenge")
        }
    }
}

fun main(args: Array<String>) {
    val parser = ArgParser("aoc")

    val challenge by parser.option(
        ArgType.Int, shortName = "c", fullName = "challenge"
    ).required()

    val part by parser.option(
        ArgType.Choice(listOf(1, 2), { it.toInt() }),
        shortName = "p",
        fullName = "part"
    ).required()

    val isTestRun by parser.option(
        ArgType.Boolean, shortName = "t", fullName = "test"
    ).default(false)

    val debug by parser.option(
        ArgType.Boolean,
        shortName = "d",
        fullName = "debug"
    ).default(false)

    parser.parse(args)

    val files = if (isTestRun) {
        readTestFiles(challenge)
    } else arrayOf(
        readInputFile(challenge)
    )

    val ch = getChallenge(challenge)

    for (file in files) {
        println("Running ${file.path}")
        println()

        val text = file.readText()

        when (part) {
            1 -> ch.partOne(text, debug, isTestRun)
            2 -> ch.partTwo(text, debug, isTestRun)
        }

        println()
        println("Done ${file.path}")
    }
}
