package com.example.books.data

data class SearchedBook(

    val id: Int,

    val imgUrl: String,

    val title: String,

    val subTitle: String,

    val authors: String,

    val pageCount: Int?,

    val topic: String
)

fun SearchedBook.getFormattedTopic() = topic.trim('[', ']')

fun SearchedBook.getFormattedAuthors() : String {

    val maxChar = 33
    val len = authors.length

    return if (len > maxChar) {
        authors.trim('[', ']').dropLast(len - maxChar).plus("...")
    } else {
        authors.trim('[', ']')
    }
}

fun SearchedBook.getFormattedTitle() : String {

    val maxChar = 30
    val len = title.length

    return if (len > maxChar) {
        title.dropLast(len - maxChar).plus("...")
    } else {
        title
    }
}

fun SearchedBook.getFormattedSubtitle() : String {

    val maxChar = 84
    val minChar = 44
    val len = subTitle.length

    return if (len > maxChar) {
        subTitle.dropLast(len - maxChar).plus("...")
    } else if (len < minChar) {
        subTitle.apply {
            plus(" ").repeat(minChar - len + 30)
        }
    } else {
        subTitle
    }
}