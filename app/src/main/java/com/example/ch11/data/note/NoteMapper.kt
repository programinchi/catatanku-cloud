package com.example.ch11.data.note

import com.example.ch11.data.model.Note

// Nama field dokumen Firestore. Ditulis eksplisit (bukan toObject()) agar
// sama persis dengan yang diperiksa di firestore.rules.
const val FIELD_TITLE = "title"
const val FIELD_CONTENT = "content"
const val FIELD_CREATED_AT = "createdAt"
const val FIELD_UPDATED_AT = "updatedAt"
const val FIELD_IS_PINNED = "isPinned"
const val FIELD_TAGS = "tags"

// id tidak disimpan di dalam dokumen: id adalah nama dokumen itu sendiri.
// hasPendingWrites juga tidak: itu status cache di perangkat.
// TODO [T3.1] Kembalikan mapOf(FIELD_TITLE to title, ...) untuk keenam field di atas.
fun Note.toFirestoreMap(): Map<String, Any> = TODO("T3.1 toFirestoreMap")

// Skema Firestore fleksibel: dokumen lama bisa saja tidak punya field baru.
// Karena tidak ada migration seperti Room, pembaca harus memberi nilai default.
fun noteFromMap(
    id: String,
    data: Map<String, Any?>,
    hasPendingWrites: Boolean = false
): Note {
    // TODO [T3.1] Baca setiap field dengan aman, lalu kembalikan Note(id = id, ...):
    //   title/content : data[FIELD_TITLE] as? String ?: ""
    //   createdAt     : (data[FIELD_CREATED_AT] as? Number)?.toLong() ?: 0L
    //   updatedAt     : sama, tetapi default-nya createdAt
    //   isPinned      : data[FIELD_IS_PINNED] as? Boolean ?: false
    //   tags          : (data[FIELD_TAGS] as? List<*>)?.filterIsInstance<String>().orEmpty()
    //   hasPendingWrites dari parameter.
    // Gunakan "as?" (bukan "as"): dokumen yang rusak tidak boleh membuat aplikasi crash.
    TODO("T3.1 noteFromMap")
}

// Sudah disediakan. Firestore mengurutkan berdasarkan createdAt. Catatan yang disematkan
// dipindah ke atas di perangkat: orderBy dua field butuh composite index.
fun sortNotes(notes: List<Note>) =
    notes.sortedWith(
        compareByDescending<Note> { it.isPinned }
            .thenByDescending { it.createdAt }
    )
