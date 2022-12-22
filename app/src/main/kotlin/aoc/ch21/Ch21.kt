package aoc.ch21

import aoc.Challenge

class Ch21 : Challenge {

    sealed interface Term

    data class Value(val value: Long) : Term
    data class Expression(
        val left: String,
        val right: String,
        val operation: Char
    ) : Term


    private fun parseLine(l: String): Pair<String, Term> {
        val parts = l.split(": ")
        val identifier = parts[0]
        return identifier to if (parts[1].any { it == '+' || it == '-' || it == '*' || it == '/' }) {
            val expressionParts = parts[1].split(' ')
            Expression(
                expressionParts[0],
                expressionParts[2],
                expressionParts[1].first()
            )
        } else {
            Value(parts[1].toLong())
        }
    }

    private val cachedValues = mutableMapOf<String, Long>()

    private fun solve(current: String, expressions: Map<String, Term>): Long {
        return when (val term = expressions[current]!!) {
            is Expression -> {
                when (term.operation) {
                    '+' -> solve(term.left, expressions) + solve(
                        term.right,
                        expressions
                    )

                    '-' -> solve(term.left, expressions) - solve(
                        term.right,
                        expressions
                    )

                    '*' -> solve(term.left, expressions) * solve(
                        term.right,
                        expressions
                    )

                    '/' -> solve(term.left, expressions) / solve(
                        term.right,
                        expressions
                    )

                    else -> {
                        throw IllegalArgumentException("Invalid operation ${term.operation}")
                    }
                }
            }

            is Value -> term.value
        }
    }

    override fun partOne(input: String, debug: Boolean, isTestRun: Boolean) {
        val expressions = input.lineSequence().map(this::parseLine).toMap()

        println(solve("root", expressions))
    }

    private fun findValue(expressions: Map<String, Term>): Long {
        val prev = mutableMapOf<String, String>()

        fun dfs(ss: String) {
            val t = expressions[ss]!!
            if (t is Expression) {
                prev[t.left] = ss
                dfs(t.left)
                prev[t.right] = ss
                dfs(t.right)
            }
        }

        dfs("root")

        val unknownStates = mutableSetOf<String>()
        var s = "humn"
        while (s != "root") {
            unknownStates.add(s)
            s = prev[s]!!
        }

        val newExpressions = buildMap {
            putAll(expressions)
            val root = expressions["root"]!! as Expression
            put("root", Expression(root.left, root.right, '='))
        }

        fun findValueAux(s: String, value: Long? = null): Long {
            if (s == "humn") {
                return value!!
            }
            val expr = newExpressions[s]!! as Expression
            val knownPath =
                if (unknownStates.contains(expr.left)) expr.right else expr.left
            val unknownPath =
                if (unknownStates.contains(expr.left)) expr.left else expr.right

            val knownValue = solve(knownPath, newExpressions)
            return when (expr.operation) {
                '+' -> findValueAux(unknownPath, (value ?: 0) - knownValue)
                '-' -> {
                    if (expr.left == unknownPath) {
                        findValueAux(unknownPath, (value ?: 0) + knownValue)
                    } else {
                        findValueAux(unknownPath, knownValue - (value ?: 0))
                    }
                }

                '*' -> findValueAux(unknownPath, (value ?: 0) / knownValue)
                '/' -> {
                    if (expr.right == unknownPath) {
                        throw IllegalArgumentException("Operand on the right")
                    }
                    findValueAux(unknownPath, (value ?: 0) * knownValue)
                }

                '=' -> findValueAux(unknownPath, knownValue)
                else -> throw IllegalArgumentException("Invalid operation ${expr.operation}")
            }
        }

        return findValueAux("root")
    }

    override fun partTwo(input: String, debug: Boolean, isTestRun: Boolean) {
        val expressions = input.lineSequence().map(this::parseLine).toMap()

        println(findValue(expressions))
    }
}