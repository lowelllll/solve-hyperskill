package search

import java.util.*

enum class Strategy {
    ALL, ANY, NONE
}

fun selectStrategy(scanner: Scanner): Strategy {
    val strategy = scanner.nextLine()
    return Strategy.valueOf(strategy)
}

fun findPeople(scanner: Scanner, people: List<String>, invertIndex: MutableMap<String, MutableList<Int>> ) {
    scanner.nextLine()

    println()
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = Strategy.valueOf(scanner.nextLine())

    println()
    println("Enter a name or email to search all suitable people.")

    val targetPeople = scanner.nextLine().split(" ")
    // Katie Eric QQQ
    // all 모두 있어야함
    // any 하나만 있어도 됨
    // none 아닌걸 구함.

    val result: Collection<Int> = when(strategy) {
        Strategy.ALL -> findAllPeople(targetPeople, invertIndex)
        Strategy.ANY -> findAnyPeople(targetPeople, invertIndex)
        Strategy.NONE -> findExceptPeople(people, targetPeople, invertIndex)
    }

    if (result.isNullOrEmpty()) println("No matching people found.")
    else {
        println("${result.size} persons found:")
        result.forEach { println(people[it]) }
    }
}

fun findAllPeople(targetPeople: List<String>, invertIndex: MutableMap<String, MutableList<Int>>): MutableSet<Int> {
    var result: MutableSet<Int> = mutableSetOf()
    targetPeople.forEach { person ->
        person.toLowerCase().run {
            invertIndex[this]?.let { r ->
                if (result.isEmpty()) result.addAll(r)
                else {
                    result = result.intersect(r).toMutableSet() // FIXME 맘에 안든다
                }
            }
        }
    }

    return result
}

fun findAnyPeople(targetPeople: List<String>,  invertIndex: MutableMap<String, MutableList<Int>>): MutableSet<Int> {
    val result: MutableSet<Int> = mutableSetOf()
    targetPeople.forEach { person ->
        person.toLowerCase().run {
            invertIndex[this]?.let { result.addAll(it) }
        }
    }
    return result
}

fun findExceptPeople(people: List<String>, targetPeople: List<String>, invertIndex: MutableMap<String, MutableList<Int>>): List<Int> {
    val exceptPeople = findAnyPeople(targetPeople, invertIndex)

    return people.asSequence().mapIndexed { index, _ -> if (!exceptPeople.contains(index)) index else -1 }.filter { it != -1 }.toList()
}

fun printAllPeople(people: List<String>) {
    println()
    println("=== List of people ===")
    people.forEach { println(it) }
}


fun inputMenu(scanner: Scanner): Int {
    println()
    println("""
        === Menu ===
        1. Find a person
        2. Print all people
        0. Exit
    """.trimIndent())

    return scanner.nextInt()
}

fun registerInvertIndex(people: List<String>): MutableMap<String, MutableList<Int>> {
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()

    people.forEachIndexed { index, line ->
        line.split(" ").forEach { word ->
            word.toLowerCase().run {
                invertedIndex[this]?.add(index) ?: run { invertedIndex[this] = mutableListOf(index) } // 대소문자 구분을 위해 tolowercase
            }
        }
    }

    return invertedIndex
}

fun terminate() {
    println()
    println("Bye!")
}

fun getPeople(): List<String> {
    return listOf("Kristofer Galley", "Fernando Marbury fernando_marbury@gmail.com",
            "Kristyn Nix nix-kris@gmail.com",
            "Regenia Enderle",
            "Malena Gray",
            "Colette Mattei",
            "Wendolyn Mcphillips",
            "Carlene Bob"
    )
}


fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
//    val people = File(args[1]).readLines()
    val people = getPeople()
    val invertIndex = registerInvertIndex(people)

    while(true) {
        when (inputMenu(scanner)) {
            0 -> {
                terminate()
                return
            }
            1 -> findPeople(scanner, people, invertIndex)
            2 -> printAllPeople(people)
            else -> {
                println()
                println("Incorrect option! Try again.")
            }
        }
    }
}