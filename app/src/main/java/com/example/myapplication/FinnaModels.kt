package com.example.myapplication
import com.google.gson.annotations.SerializedName

data class FinnaResponse(
    @SerializedName("records")
    val records: List<FinnaRecord>? = null
)

data class FinnaRecord(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("year")
    val year: String?,

    @SerializedName("images")
    val images: List<String>?
) {
    fun getFullImageUrl(): String? {
        val imagePath = images?.firstOrNull() ?: return null
        return "https://api.finna.fi$imagePath"
    }
}
