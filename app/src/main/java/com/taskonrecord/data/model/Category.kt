package com.taskonrecord.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the type/theme of a house category.
 * Each type has a unique set of resident characters.
 */
enum class HouseType {
    COLLEGE,    // Residents: Pig, Dog, Cat, Mouse
    HOME,       // Residents: Boy, Girl, Baby, Granny
    GARDEN      // Residents: Flowers, Crops (plants)
}

/**
 * A task category — represented visually as a house or garden.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: HouseType,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
