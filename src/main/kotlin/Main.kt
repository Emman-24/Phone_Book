package org.example

import java.io.File

const val directoryFilePath = "src/main/kotlin/resources/small_directory.txt"
const val phonebookFilePath = "src/main/kotlin/resources/small_find.txt"


fun formatTime(timeInMillis: Long): String {
    val minutes = timeInMillis / 60000
    val seconds = (timeInMillis % 60000) / 1000
    val milliseconds = timeInMillis % 1000
    return "$minutes min. $seconds sec. $milliseconds ms."
}

data class Contact(val phone: String, val name: String) {
    constructor(str: String) : this(str.substringBefore(' '), str.substringAfter(' '))
}

interface Search {
    val name: String
    fun search(array: List<Contact>, name: String): Contact?
}

interface Soart {
    val name: String
    fun sort(array: MutableList<Contact>)
}

object BubbleSort : Soart {
    override val name: String
        get() = "bubble sort"

    override fun sort(array: MutableList<Contact>) {
        for (i in array.size - 1 downTo 1) {
            for (j in 0 until i) {
                if (array[j].name > array[j + 1].name) {
                    val temp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = temp
                }
            }
        }
    }

}

object JumpSearch : Search {
    override val name: String
        get() = "jump search"

    override fun search(array: List<Contact>, name: String): Contact? {
        return null
    }

}


class LinearSearch : Search {
    override val name: String
        get() = "linear search"

    override fun search(array: List<Contact>, name: String): Contact? =
        array.find { it.name == name }
}


fun main() {
    searchQueries(LinearSearch())
    println()
    searchQueries(JumpSearch, BubbleSort)
}

fun searchQueries(searchQuery: Search, soart: Soart? = null) {
    val directoryEntries = File(directoryFilePath).readLines().map { Contact(it) }
    val phonebookEntries = File(phonebookFilePath).readLines()

    if (soart != null) println("Start searching (${soart.name} + ${searchQuery.name})...") else println("Start searching (${searchQuery.name})...")

    val start = System.currentTimeMillis()
    soart?.sort(directoryEntries as MutableList<Contact>)
    val sortTime = System.currentTimeMillis() - start

    val dataFind = phonebookEntries.count {
        searchQuery.search(directoryEntries, it) != null
    }

    val totalTime = System.currentTimeMillis() - start

    println("Found $dataFind / ${phonebookEntries.size} entries. Time taken: ${formatTime(totalTime)}")
    if (soart != null) {
        println("Sorting time: ${formatTime(sortTime)}")
        println("Searching time: ${formatTime(totalTime - sortTime)}")
    }

}