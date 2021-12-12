package com.example.books.network

import com.google.gson.annotations.SerializedName

data class OnSearch(

    @SerializedName("items")
    val items: List<ItemsItem?>? = null
)

data class ItemsItem(

    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo? = null
)

data class VolumeInfo(

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("authors")
    val authors: List<String?>? = null,

    @SerializedName("pageCount")
    val pageCount: Int? = null,

    @SerializedName("categories")
    val categories: List<String?>? = null,

    @SerializedName("imageLinks")
    val imageLinks: ImageLinks? = null
)

data class ImageLinks(

    @SerializedName("thumbnail")
    val thumbnail: String? = null

)