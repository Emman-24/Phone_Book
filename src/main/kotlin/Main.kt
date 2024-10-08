package org.example

import java.io.File
import kotlin.math.min
import kotlin.math.sqrt

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

class QuickSort : Soart {
    override val name: String
        get() = "quick sort"

    override fun sort(array: MutableList<Contact>) {
        if (array.isNotEmpty()){
            quickSort(array,0,array.size-1)
        }
    }

    private fun quickSort(array: MutableList<Contact>, l: Int, r: Int) {
        if (l < r) {
            val p = partition(array, l, r)
            quickSort(array, l, p - 1)
            quickSort(array, p + 1, r)
        }
    }

    private fun partition(array: MutableList<Contact>, l: Int, r: Int): Int {
        val x = array[r]
        var i = l - 1
        for (j in l until r) {
            if (array[j].name <= x.name) {
                i++
                val temp = array[i]
                array[i] = array[j]
                array[j] = temp
            }
        }
        val temp = array[i + 1]
        array[i + 1] = array[r]
        array[r] = temp
        return i + 1
    }

}

object JumpSearch : Search {
    override val name: String
        get() = "jump search"

    override fun search(array: List<Contact>, name: String): Contact? {
        if (array.isEmpty()) return null
        var curr = 1
        var prev = 1
        val last = array.size
        val step = sqrt(last.toDouble()).toInt()

        while (array[curr].name < name) {
            if (curr == last) return null
            prev = curr
            curr = min(curr + step, last)
        }

        while (array[curr].name > name) {
            if (--curr <= prev) return null
        }

        return if (array[curr].name == name) array[curr] else null
    }

}


class LinearSearch : Search {
    override val name: String
        get() = "linear search"

    override fun search(array: List<Contact>, name: String): Contact? =
        array.find { it.name == name }
}

class BinarySearch : Search {
    override val name: String
        get() = "binary search"

    override fun search(array: List<Contact>, name: String): Contact? {
        return array.binarySearchBy(name)
    }

    private fun List<Contact>.binarySearchBy(name: String): Contact? {
        return this.binarySearch { it.name.compareTo(name) }.takeIf { it >= 0 }?.let { this[it] }
    }

}

fun main() {
    searchQueries(LinearSearch())
    println()
    searchQueries(JumpSearch, BubbleSort)
    println()
    searchQueries(BinarySearch(), QuickSort())
}

fun searchQueries(searchAlgorithm: Search, sortAlgorithm: Soart? = null) {
    val directoryEntries = File(directoryFilePath).readLines().map { Contact(it) }
    val phonebookEntries = File(phonebookFilePath).readLines()

    printSearchStartMessage(searchAlgorithm, sortAlgorithm)
    val startTime = System.currentTimeMillis()

    val sortedEntries = sortContacts(directoryEntries, sortAlgorithm)
    val sortTime = elapsedTime(startTime)

    val foundEntriesCount = phonebookEntries.count { name ->
        searchAlgorithm.search(sortedEntries, name) != null
    }

    val totalTime = elapsedTime(startTime)

    printSearchResults(phonebookEntries.size, foundEntriesCount, totalTime, sortTime)
}

fun elapsedTime(startTime: Long): Long =
    System.currentTimeMillis() - startTime


private fun sortContacts(contacts: List<Contact>, sortAlgorithm: Soart?): List<Contact> {
    return sortAlgorithm?.let {
        val mutableContacts = contacts.toMutableList()
        it.sort(mutableContacts)
        mutableContacts
    } ?: contacts
}

private fun printSearchStartMessage(searchAlgorithm: Search, sortAlgorithm: Soart?) {
    val message = if (sortAlgorithm != null) {
        "Start searching (${sortAlgorithm.name} + ${searchAlgorithm.name})..."
    } else {
        "Start searching (${searchAlgorithm.name})..."
    }
    println(message)
}

private fun printSearchResults(totalEntries: Int, foundEntries: Int, totalTime: Long, sortTime: Long) {
    println("Found $foundEntries / $totalEntries entries. Time taken: ${formatTime(totalTime)}")
    if (sortTime != 0L) {
        println("Sorting time: ${formatTime(sortTime)}")
        println("Searching time: ${formatTime(totalTime - sortTime)}")
    }
}