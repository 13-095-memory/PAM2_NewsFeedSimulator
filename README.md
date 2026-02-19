# News Feed Simulator

## Fitur

| Fitur | Implementasi |
|---|---|
| Simulasi berita baru setiap 2 detik | `Flow` + `delay(2000)` di coroutine |
| Filter berita berdasarkan kategori | `combine()` antara `_newsList` dan `_selectedCategory` |
| Transform data ke format tampilan | `formattedNews: Flow<List<String>>` dengan `.map` |
| Menyimpan jumlah berita yang dibaca | `readCount: StateFlow<Int>` |
| Ambil detail berita secara async | `suspend fun fetchNewsDetail()` dipanggil di `LaunchedEffect` |

---

## Struktur Proyek

```
NewsFeedSimulator/
├── composeApp/
│   ├── build/
│   ├── src/
│   │   └── commonMain
│   │       ├── composeResources
│   │       ├── kotlin
│   │       │   ├── com.memory.newsfeedsimulator
│   │       │    │  ├── App.kt
│   │       │    │  ├── Greeting
│   │       │    │  ├── NewsFeedSimulator.kt   # Logic: Flow, StateFlow, Coroutines
│   │       │    │  ├── Platform.kt
│   │    └── commonTest
│   │    └── iosMain
│   │    └── jvmMain
│   └── build.gradle.kts
├── gradle
├── iosApp
└── README.md
```
## Cara Menjalankan

### 1. Buka di Android Studio

1. Buka **Android Studio**
2. Klik **File → Open**
3. Pilih folder `NewsFeedSimulator`
4. Tunggu proses **Gradle Sync** selesai
6. Pilih Running Device
Di sebelah kanan layar terdapat panel Running Devices — di sinilah nanti tampilan aplikasi akan muncul setelah di-run. Pastikan sudah ada emulator atau device fisik yang terhubung.
7. Pilih Konfigurasi & Run
Di pojok kanan atas Android Studio:

Pastikan konfigurasi yang dipilih adalah composeApp — ini yang menjalankan tampilan Compose kita
Klik tombol ▶ Run (atau tekan Shift+F10)
Tunggu beberapa detik, aplikasi akan muncul di panel Running Devices

atau lewat terminal:

```bash
./gradlew installDebug
```

---

## Cara Menggunakan Aplikasi

1. **Tap ▶ Start** — simulator mulai generate berita baru setiap 2 detik
2. **Pilih Kategori** — gunakan dropdown untuk filter berita (Teknologi / Olahraga / Musik / Semua)
3. **Tap kartu berita** — detail berita diambil secara async (ada loading spinner sebentar)
4. **Lihat counter** — "📖 Berita dibaca: X" di atas update otomatis setiap berita dibuka
5. **Tap ⏹ Stop** — hentikan simulator kapan saja
6. **Tap ↺ Reset** — hapus semua berita dan mulai ulang dari awal
7. Simulator **berhenti otomatis** setelah menghasilkan 20 berita
---

## Lisensi

Proyek ini dibuat untuk keperluan tugas pembelajaran Kotlin & Coroutines.
