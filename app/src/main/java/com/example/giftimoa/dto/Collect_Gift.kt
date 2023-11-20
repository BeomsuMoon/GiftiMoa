package com.example.giftimoa.dto
import java.io.Serializable

data class Collect_Gift(
    val ID: Int,
    var giftName: String,
    var effectiveDate: String,
    var barcode: String,
    var usage: String,
    var imageUrl: String,
    var state: Int,
    var deletedGiftState: Int = 0
) : Serializable
{
    override fun toString(): String {
        return "ID: $ID , Gift Name: $giftName, Effective Date: $effectiveDate, Barcode: $barcode, Usage: $usage, Image URL: $imageUrl"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Collect_Gift) return false
        return ID == other.ID
    }

}
data class Badge(
    val content: String,
    val color: String
)