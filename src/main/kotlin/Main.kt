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


class LinearSearch : Search {
    override val name: String
        get() = "linear search"

    override fun search(array: List<Contact>, name: String): Contact? =
        array.find { it.name == name }
}


fun main() {
    searchContacts(LinearSearch())
}

fun searchContacts(searchQuery: Search) {
    val directoryEntries = File(directoryFilePath).readLines().map { Contact(it) }
    val phonebookEntries = File(phonebookFilePath).readLines()

    println("Start searching (${searchQuery.name})...")

    val start = System.currentTimeMillis()

    val dataFind = phonebookEntries.onEach { phoneNumber ->
        searchQuery.search(directoryEntries, phoneNumber)
    }.size

    val totalTime = System.currentTimeMillis() - start

    println("Found $dataFind / ${phonebookEntries.size} entries. Time taken: ${formatTime(totalTime)}")


}