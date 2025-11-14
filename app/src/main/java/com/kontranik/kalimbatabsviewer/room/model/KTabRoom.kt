package com.kontranik.kalimbatabsviewer.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable
import java.util.*

@Entity(tableName = "ktabs_table")
data class KTabRoom(
    @PrimaryKey @ColumnInfo(name = "kTabId") val kTabId: String,
    @ColumnInfo(name = "difficulty") var difficulty: Int = 0,
    @ColumnInfo(name = "interpreter") var interpreter: String = "",
    @ColumnInfo(name = "source") var source: String = "",
    @ColumnInfo(name = "youtube") var youtube: String = "",
    @ColumnInfo(name = "text") var text: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "updated") var updated: Date = Date(),
    @ColumnInfo(name = "bookmarked") var bookmarked: Boolean = false,
    ) : Serializable {


    override fun toString(): String {
        return "title: $title, interpreter: $interpreter, updated: $updated"
    }



    override fun hashCode(): Int {
        var result = kTabId.hashCode()
        result = 31 * result + difficulty
        result = 31 * result + interpreter.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + youtube.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + updated.hashCode()
        result = 31 * result + bookmarked.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KTabRoom

        if (kTabId != other.kTabId) return false
        if (difficulty != other.difficulty) return false
        if (interpreter != other.interpreter) return false
        if (source != other.source) return false
        if (youtube != other.youtube) return false
        if (text != other.text) return false
        if (title != other.title) return false
        if (updated != other.updated) return false
        if (bookmarked != other.bookmarked) return false

        return true
    }

    fun hasText(): Boolean {
        val scanner = Scanner(text)
        while (scanner.hasNextLine()) {
            val line: String = scanner.nextLine()
            if (line.startsWith(">")) {
                return true
            }
        }
        scanner.close()
        return false
    }

    fun hasUrl(): Boolean {
        return source.isNotEmpty() || youtube.isNotEmpty()
    }

    fun containtTextLines(): Boolean {
        text.lines().forEach { line ->
            if (line.startsWith(">")) {
                return true
            }
        }
        return false
    }

}

