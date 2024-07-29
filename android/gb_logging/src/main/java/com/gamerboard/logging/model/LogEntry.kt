package com.gamerboard.logging.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gamerboard.logging.utils.now
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Entity(tableName = "logs")
data class LogEntry(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name ="identifier")
    val identifier: String?,
    @ColumnInfo(name = "message")
    val message: String,
    @ColumnInfo(name = "read")
    val read: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogEntry

        if (id != other.id) return false
        if (identifier != other.identifier) return false
        if (read != other.read) return false
        if (!message.contentEquals(other.message)) return false
        return createdAt == other.createdAt
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (identifier?.hashCode() ?: 0)
        result = 31 * result + message.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + read.hashCode()
        return result
    }
}