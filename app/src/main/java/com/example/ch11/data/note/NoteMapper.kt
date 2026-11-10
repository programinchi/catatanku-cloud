package com.example.ch11.data.note

import com.example.ch11.data.model.Note

const val FIELD_TITLE = "title"
const val FIELD_CONTENT = "content"
const val FIELD_CREATED_AT = "createdAt"
const val FIELD_UPDATED_AT = "updatedAt"
const val FIELD_IS_PINNED = "isPinned"
const val FIELD_TAGS = "tags"

fun Note.toFirestoreMap(): Map<String, Any> = mapOf(
    FIELD_TITLE to title,
    FIELD_CONTENT to content,
    FIELD_CREATED_AT to createdAt,
    FIELD_UPDATED_AT to updatedAt,
    FIELD_IS_PINNED to isPinned,
    FIELD_TAGS to tags
)

fun noteFromMap(
    id: String,
    data: Map<String, Any?>,
    hasPendingWrites: Boolean = false
): Note {
    val title = data[FIELD_TITLE] as? String ?: ""

    val content = data[FIELD_CONTENT] as? String ?: ""

    val createdAt =
        (data[FIELD_CREATED_AT] as? Number)?.toLong() ?: 0L

    val updatedAt =
        (data[FIELD_UPDATED_AT] as? Number)?.toLong()
            ?: createdAt

    val isPinned =
        data[FIELD_IS_PINNED] as? Boolean ?: false

    val tags =
        (data[FIELD_TAGS] as? List<*>)
            ?.filterIsInstance<String>()
            .orEmpty()

    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isPinned = isPinned,
        tags = tags,
        hasPendingWrites = hasPendingWrites
    )
}

fun sortNotes(notes: List<Note>): List<Note> =
    notes.sortedWith(
        compareByDescending<Note> { it.isPinned }
            .thenByDescending { it.createdAt }
    )
