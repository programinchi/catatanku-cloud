# Ch11 Starter — CatatanKu Cloud: Firebase Auth & Firestore

Titik awal Praktikum Pertemuan 11. Isinya aplikasi **CatatanKu (Ch07)** yang
sudah dipindah ke arsitektur Firebase + Hilt. Semua **layar sudah jadi**: Login,
Registrasi, Daftar catatan (pin, cari, tag, undo hapus), dan Form edit. Yang
**belum** ada adalah bagian yang menghubungkan aplikasi ke Firebase.

Tugasmu: **membuat proyek Firebase, menulis autentikasi, menyimpan catatan ke
Firestore secara real-time, dan mengamankannya dengan Security Rules.** Semua
bagian yang harus dikerjakan ditandai di kode dengan komentar `// TODO [Tx.y]`.

> Jawaban lengkap ada di `praktikum/ch11`. Kerjakan dulu sendiri, baru
> bandingkan. Jangan menyalin sebelum semua TODO selesai.

---

## Yang Sudah Disiapkan

- **Gradle sudah lengkap.** Plugin `com.google.gms.google-services` 4.4.2,
  Firebase BoM 33.1.0 (`firebase-auth`, `firebase-firestore`),
  `kotlinx-coroutines-play-services` (untuk `Task.await()`), Credential Manager,
  dan Hilt dari Ch10. Kamu tidak perlu mengubah Gradle, tetapi **bacalah**
  `app/build.gradle.kts`.
- **`di/FirebaseModule.kt`** menyediakan `FirebaseAuth` dan `FirebaseFirestore`
  lewat `@Provides`. **`di/RepositoryModule.kt`** mengikat interface ke implementasi
  Firebase lewat `@Binds`.
- **Interface** `AuthRepository` dan `NoteRepository`. ViewModel hanya mengenal
  keduanya, bukan SDK Firebase.
- **Semua layar Compose**, `NoteListViewModel`, dan `NoteEditViewModel`. Logikanya
  sama dengan Ch07, hanya ID catatan kini berupa `String` dari Firestore.
- **`data/auth/AuthErrors.kt`** menerjemahkan kode error Firebase menjadi pesan
  bahasa Indonesia. **`util/Validation.kt`** memvalidasi email dan password.
- **Semua unit test** dan fake-nya (`FakeAuthRepository`, `FakeNoteRepository`).
- **`app/google-services.json` placeholder**, supaya proyek bisa di-build dan diuji
  sebelum kamu punya proyek Firebase.

---

## TODO — Wajib Diselesaikan Mahasiswa

**Menemukan semua TODO:** Android Studio → **View → Tool Windows → TODO**
(`⌘6` di macOS, `Alt+6` di Windows/Linux). Hapus komentar `TODO` setelah selesai,
sehingga daftar itu **kosong** saat kamu selesai. Setiap TODO berisi petunjuk kode.

### Tugas 1 — Proyek Firebase (tanpa kode)

| ID | Tempat | Yang harus dikerjakan |
|---|---|---|
| [ ] **T1.1** | [Firebase Console](https://console.firebase.google.com) | Buat proyek → **Add app → Android**, package name **`com.example.ch11`** |
| [ ] **T1.2** | `app/google-services.json` | Unduh dari console → **timpa** file placeholder |
| [ ] **T1.3** | Console → Authentication | **Sign-in method → Email/Password → Enable** |
| [ ] **T1.4** | Console → Firestore Database | **Create database** (lokasi mis. `asia-southeast2`) dalam *production mode* |

Selama masih memakai placeholder, login akan menampilkan *"Konfigurasi Firebase
tidak valid. Ganti app/google-services.json…"*.

> `google-services.json` **bukan rahasia** dan boleh di-commit. Keamanan data
> dijaga oleh Security Rules (Tugas 4).

### Tugas 2 — Login, Registrasi, dan Navigasi berdasarkan Status Login

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T2.1** | `data/auth/FirebaseAuthRepository.kt` | `authState` dengan `callbackFlow` + `AuthStateListener` + `awaitClose`; `login` dan `register` dengan `.await()` |
| [ ] **T2.2** | `ui/auth/LoginViewModel.kt` | `login()`: cegah klik ganda → `validateLogin` → `authRepository.login(...)` |
| [ ] **T2.3** | `ui/auth/RegisterViewModel.kt` | `register()`: `validateRegistration` → `authRepository.register(...)` |
| [ ] **T2.4** | `ui/session/SessionViewModel.kt` | `currentUser` = `authState.stateIn(..., initialValue = currentUser)` |
| [ ] **T2.5** | `CatatanKuApp.kt` | `LaunchedEffect(isSignedIn)`: sudah login di layar auth → daftar catatan; logout → layar login, back stack dikosongkan |

**Navigasi = fungsi dari state.** `LoginViewModel` sengaja **tidak** punya
`isSuccess`. Login berhasil → `authState` berubah → `CatatanKuApp` memindahkan
layar. Jalur yang sama dipakai untuk registrasi, Google Sign-In, dan logout.

### Tugas 3 — Catatan di Firestore secara Real-Time

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T3.1** | `data/note/NoteMapper.kt` | `toFirestoreMap()` (6 field, **tanpa** `id` dan `hasPendingWrites`) dan `noteFromMap()` (aman terhadap field hilang dan tipe salah) |
| [ ] **T3.2** | `data/note/FirestoreNoteRepository.kt` | `notesSnapshots(uid)`: `callbackFlow` + `addSnapshotListener(MetadataChanges.INCLUDE)` + `awaitClose` |
| [ ] **T3.3** | file yang sama | `getNote` dengan `.get().await()`; `addNote`/`updateNote`/`deleteNote` **tanpa** `await()` |

Data disimpan di `users/{uid}/notes/{noteId}`. Fungsi `observeNotes()` sudah
disediakan: listener otomatis berganti saat akun berganti (`flatMapLatest` atas
`authState`).

> **Mengapa penulisan tidak di-`await()`?** `Task` Firestore baru selesai setelah
> **server** mengonfirmasi. Saat offline, tombol Simpan akan menggantung sampai
> koneksi kembali. Tanpa `await`, perubahan langsung masuk cache lokal dan
> listener langsung memancarkan daftar baru (*latency compensation*).

### Tugas 4 — Security Rules

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T4.1** | `firestore.rules` | Ganti `request.auth != null` dengan `isOwner(userId)`: hanya **pemilik** yang boleh membaca/menulis |
| [ ] **T4.2** | file yang sama *(tantangan)* | `isValidNote()` untuk `create`/`update`: 6 field wajib, tipe benar, judul 1–200 karakter |

Publish: salin isi file ke **Firestore → Rules → Publish**, atau
`firebase deploy --only firestore:rules`.

**Uji dengan dua akun, lalu laporkan:**
1. Daftar sebagai akun A, buat 2 catatan; salin UID A dari Firestore → Data.
2. Logout → daftar sebagai akun B. Daftar catatan B harus kosong.
3. **Rules Playground**: `get` ke `/users/<UID_A>/notes/<ID_CATATAN>` dengan UID B.
   - Rules starter → **Allowed** (bocor!)
   - Setelah T4.1 → **Denied**
4. Tangkapan layar kedua hasil itu adalah bukti Tugas 4.

### Tugas 5 (Tantangan) — Masuk dengan Google

| ID | File | Yang harus dikerjakan |
|---|---|---|
| [ ] **T5.1** | `ui/auth/GoogleSignIn.kt` | `requestGoogleIdToken()` dengan Credential Manager + `GetGoogleIdOption` |
| [ ] **T5.2** | `data/auth/FirebaseAuthRepository.kt` | `signInWithGoogle()`: `GoogleAuthProvider.getCredential(idToken, null)` → `signInWithCredential` |
| [ ] **T5.3** | `ui/auth/LoginViewModel.kt` | `onGoogleIdToken()` → `authRepository.signInWithGoogle(...)` |

Setup console: aktifkan provider **Google**, tambahkan **SHA-1** debug
(`./gradlew signingReport`), lalu **unduh ulang** `google-services.json`. Uji di
emulator dengan image **Google Play** yang sudah login akun Google.

### Aturan yang harus dipatuhi

- **ViewModel tidak boleh meng-import `com.google.firebase`.** Firebase hanya ada di
  `data/` dan `di/`.
- **Password tidak pernah disimpan** di aplikasi (SharedPreferences, Room, Firestore).
- **Jangan memanggil `navigate()` dari LoginScreen/RegisterScreen.** Navigasi hanya
  di `CatatanKuApp` berdasarkan status login (T2.5).
- **Jangan `await()` penulisan Firestore** (T3.3).
- Rules akhir **tidak boleh** mengandung `if true` atau hanya `request.auth != null`.

### Membaca hasil tes

```bash
./gradlew testDebugUnitTest      # tanpa emulator, internet, atau Firebase
```

| Berkas tes | Kondisi awal | Hijau setelah |
|---|---|---|
| `NoteListViewModelTest` (7), `NoteEditViewModelTest` (5), `AuthErrorsTest` (6), `ValidationTest` (3), `TagsTest` (3), `RelativeTimeTest` (7) | **hijau** (31) | harus **tetap hijau** |
| `LoginViewModelTest` (6) | 4 merah | **T2.2** (3 tes) dan **T5.3** (tes *token Google*) |
| `RegisterViewModelTest` (3) | 3 merah | **T2.3** |
| `SessionViewModelTest` (2) | 1 merah | **T2.4** |
| `NoteMapperTest` (6) | 5 merah | **T3.1** |

Total **48** tes: 35 hijau dan 13 merah di awal. Tugas T2.1, T2.5, T3.2, T3.3,
dan T4 menyentuh Firebase sungguhan, jadi **tidak** bisa diuji di JVM. Buktikan
lewat checklist di bawah.

### Error yang sering muncul

| Pesan | Penyebab |
|---|---|
| *Konfigurasi Firebase tidak valid…* | `google-services.json` masih placeholder (T1.2) |
| *Metode login ini belum diaktifkan di Firebase Console* | Email/Password belum di-enable (T1.3) |
| *Belum diimplementasikan: TODO [T…]* | Stub yang disebut belum dikerjakan |
| Login berhasil (akun muncul di console) tetapi layar tidak pindah | T2.1 masih `flowOf(...)`, T2.4 belum, atau T2.5 belum |
| Daftar selalu kosong walau data ada di console | T3.2 masih `flowOf(emptyList())` |
| *Akses ditolak server. Periksa Security Rules* | Rules salah path, atau masih *production mode* tanpa rules (T4.1) |
| Tombol Simpan menggantung saat offline | Penulisan di-`await()` (lihat T3.3) |
| `File google-services.json is missing` (build) | File terhapus atau salah folder; harus di `app/` |
| `No matching client found for package name 'com.example.ch11'` (build) | Package name di console berbeda dengan `applicationId` |

### Dianggap selesai jika semua ini benar

- [ ] Registrasi → langsung masuk ke daftar catatan; logout → layar login, dan
      **Back** tidak kembali ke daftar.
- [ ] Tutup aplikasi lalu buka lagi → masih login.
- [ ] Catatan tampil di **Firestore → Data** pada `users/{uid}/notes`.
- [ ] Ubah data langsung di console → aplikasi ikut berubah dalam hitungan detik.
- [ ] Mode pesawat → tambah catatan → muncul seketika dengan ikon *Menunggu sinkron*
      → online lagi → ikon hilang.
- [ ] Rules Playground: akun lain **Denied**.
- [ ] Jendela TODO kosong; `./gradlew testDebugUnitTest` hijau semua (**48** tes).

**Pengumpulan:** push ke branch `pertemuan-11` → kumpulkan link di LMS sebelum
pertemuan berikutnya. Sertakan tangkapan layar Rules Playground (Tugas 4).

---

## Cara Membuka di Android Studio

1. **File → Open** → pilih folder `praktikum/ch11_starter/`
2. Tunggu Gradle sync → pilih device/emulator → klik **Run ▶**.
   Aplikasi langsung berjalan dan menampilkan layar login. Tombol **Masuk**
   belum melakukan apa pun sampai T2.2.
3. Kerjakan Tugas 1 sebelum mencoba login sungguhan.

> **Versi compiler sama dengan Ch03–Ch10**: Gradle 8.6, AGP 8.3.2, Kotlin 1.9.23,
> KSP `1.9.23-1.0.20`, Hilt 2.51.1. Ditambah google-services 4.4.2, Firebase BoM
> 33.1.0, dan Credential Manager 1.3.0.

Materi konsep (BaaS, autentikasi vs otorisasi, token, model dokumen, real-time,
offline, Security Rules) ada di buku Bab 11 dan slide `ch11`. Penjelasan tiap
langkah dan perbedaan dengan listing buku ada di `praktikum/ch11/README.md`.

---

## Run the code

```bash
cd /Users/dhareva/Books/2026_MP/codes/praktikum/ch11_starter
./gradlew installDebug
adb shell am start -n com.example.ch11/.MainActivity
```
