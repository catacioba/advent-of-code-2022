package aoc.ch07

import aoc.Challenge

class Ch07 : Challenge {

    /* File Tree entries */
    sealed class FileTreeEntry(val name: String) {
        abstract val size: Long

        fun directoriesSizeSequence(): Sequence<Long> = sequence {
            when (this@FileTreeEntry) {
                is TreeDirectory -> {
                    yield(this@FileTreeEntry.size)
                    this@FileTreeEntry.children.values.forEach {
                        yieldAll(it.directoriesSizeSequence())
                    }
                }

                is TreeFile -> {
                }
            }
        }
    }

    data class TreeFile(
        val filename: String, val fileSize: Long, val fileParent: TreeDirectory?
    ) : FileTreeEntry(filename) {
        override val size: Long by lazy { fileSize }
    }

    data class TreeDirectory(
        val directoryName: String,
        val children: MutableMap<String, FileTreeEntry>,
        val dirParent: TreeDirectory?
    ) : FileTreeEntry(directoryName) {
        override val size: Long by lazy { children.values.sumOf { it.size } }
    }

    /* Commands */
    sealed interface Command

    data class CdCommand(val path: String) : Command
    data class LsCommand(val contents: List<LsResultEntry>) : Command

    sealed class LsResultEntry(val name: String)

    data class LsFile(val filename: String, val size: Long) : LsResultEntry(
        filename
    )

    data class LsDirectory(val dirName: String) : LsResultEntry(dirName)

    private fun String.toLsResultEntry(): LsResultEntry {
        return if (this.startsWith("dir ")) {
            val name = this.removePrefix("dir ").trim()
            LsDirectory(name)
        } else {
            val tokens = this.split(" ")
            LsFile(tokens[1], tokens[0].toLong())
        }
    }

    private fun parseInput(input: String): List<Command> {
        return input.split("$ ").filterNot { it.isBlank() }.map { l ->
            if (l.startsWith("cd")) {
                CdCommand(l.removePrefix("cd ").trim())
            } else {
                LsCommand(l.lineSequence()
                    .drop(1)
                    .filterNot { it.isBlank() }
                    .map { it.toLsResultEntry() }
                    .toList())
            }
        }
    }

    private fun reconstructFileTree(commands: List<Command>): TreeDirectory {
        val root = TreeDirectory("root", mutableMapOf(), null)
        var current = root

        for (command in commands.asSequence().drop(1)) {
            when (command) {
                is CdCommand -> {
                    current = when (command.path) {
                        ".." -> {
                            current.dirParent!!
                        }

                        else -> {
                            current.children[command.path] as TreeDirectory
                        }
                    }
                }

                is LsCommand -> {
                    current.children.putAll(
                        command.contents.asSequence().map {
                            it.name to (when (it) {
                                is LsDirectory -> TreeDirectory(
                                    it.dirName, mutableMapOf(), current
                                )

                                is LsFile -> TreeFile(
                                    it.filename, it.size, current
                                )
                            })
                        }.toMap()
                    )
                }
            }
        }

        return root
    }

    override fun partOne(input: String, debug: Boolean) {
        val commands = parseInput(input)
        val root = reconstructFileTree(commands)

        println(root.directoriesSizeSequence().filter { it <= 100000 }.sum())
    }

    override fun partTwo(input: String, debug: Boolean) {
        val commands = parseInput(input)
        val root = reconstructFileTree(commands)

        val diskSize = 70000000
        val requiredSize = 30000000
        val availableSize = diskSize - requiredSize
        val totalSize = root.size
        val sizeToDelete = totalSize - availableSize

        println(root.directoriesSizeSequence()
            .filter { it >= sizeToDelete }
            .min())
    }
}