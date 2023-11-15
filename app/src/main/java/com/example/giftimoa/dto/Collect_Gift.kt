package com.example.giftimoa.dto
import java.io.Serializable

data class Collect_Gift(
    val id: Int,
    var giftName: String,
    var effectiveDate: String,
    var barcode: String,
    var usage: String,
    var imageUrl: String,
    var state: Int
) : Serializable
{
    override fun toString(): String {
        return "ID: $id , Gift Name: $giftName, Effective Date: $effectiveDate, Barcode: $barcode, Usage: $usage, Image URL: $imageUrl"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Collect_Gift) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
data class Badge(
    val content: String,
    val color: String
)