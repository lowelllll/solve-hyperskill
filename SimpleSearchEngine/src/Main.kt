import java.io.File
import java.util.Scanner

class SearchEngine(private val people: List<String>) {
    private val invertedIndex = mutableMapOf<String, MutableList<Int>>()
    private val scanner = Scanner(System.`in`)

    init {
        indexing(people)
    }

    fun run() {
        while(true) {
            when (inputMenu()) {
                0 -> {
                    terminate()
                    return
                }
                1 -> findPeople()
                2 -> printAllPeople()
                else -> incorrectOption()
            }
        }
    }

    private fun findPeople() {
        println()
        println("Select a matching strategy: ALL, ANY, NONE")
        val strategy = Strategy.valueOf(scanner.nextLine())

        println()
        println("Enter a name or email to search all suitable people.")

        val targetPeople = scanner.nextLine().split(" ")

        val result: Set<Int> = when(strategy) {
            Strategy.ALL -> findAllPeople(targetPeople)
            Strategy.ANY -> findAnyPeople(targetPeople)
            Strategy.NONE -> findExceptPeople(targetPeople)
        }

        if (result.isNullOrEmpty()) println("No matching people found.")
        else {
            println("${result.size} persons found:")
            result.forEach { println(people[it]) }
        }
        println()
    }

    private fun findAllPeople(targetPeople: List<String>): Set<Int> {
        var result: MutableSet<Int> = mutableSetOf()

        targetPeople.forEach { person ->
            person.toLowerCase().run {
                invertedIndex[this]?.let { r: MutableList<Int> ->
                    if (result.isEmpty()) result.addAll(r)
                    else result = result.intersect(r) as MutableSet<Int>
                }
            }
        }

        return result
    }

    private fun findAnyPeople(targetPeople: List<String>): MutableSet<Int> {
        val result: MutableSet<Int> = mutableSetOf()

        targetPeople.forEach { person ->
            person.toLowerCase().run {
                invertedIndex[this]?.let { result.addAll(it) }
            }
        }

        return result
    }

    private fun findExceptPeople(targetPeople: List<String>): Set<Int> {
        return people.mapIndexed { index, _ -> index }.subtract(findAnyPeople(targetPeople))
    }

    private fun printAllPeople() {
        println()
        println("=== List of people ===")
        people.forEach { println(it) }
        println()
    }

    private fun inputMenu(): Int {
        println("""=== Menu ===
1. Find a person
2. Print all people
0. Exit""".trimIndent())

        return scanner.nextLine().toInt()
    }

    private fun indexing(people: List<String>) {
        people.forEachIndexed { index, line ->
            line.split(" ").forEach { word ->
                word.toLowerCase()
                    .run {
                        invertedIndex[this]?.add(index)
                            ?: run { invertedIndex[this] = mutableListOf(index) }
                    }
            }
        }
    }

    private fun terminate() {
        println()
        println("Bye!")
    }

    private fun incorrectOption() {
        println()
        println("Incorrect option! Try again.")
        println()
    }

    private enum class Strategy {
        ALL, ANY, NONE
    }
}

fun main(args: Array<String>) {
    val people = File(args[0]).readLines()
    val simpleSearchEngine = SearchEngine(people)

    simpleSearchEngine.run()
}