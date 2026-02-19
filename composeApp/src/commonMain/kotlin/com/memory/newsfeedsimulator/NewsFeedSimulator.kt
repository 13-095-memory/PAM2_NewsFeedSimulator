package com.memory.newsfeedsimulator

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class News( // Blueprint atau cetakan objek berita
    val id: Int, // ID unik dari tiap berita
    val title: String, // Judul berita
    val category: String, // Kategori berita (Olahraga, Teknologi, Musik)
    val content: String // Isi berita
)

class NewsFeedSimulator {

    //Ruang kerja coroutine di kelas NewsFeedSimulator
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        const val MAX_NEWS = 20 // Batas maksimal berita yang dibuat
    }

    // 1️. State daftar berita saat ini
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    // 2️. State kategori terpilih
    private val _selectedCategory = MutableStateFlow("Semua") //Untuk menyimpan kategori berita yang dipilih user
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    // 3️. Filter reaktif berdasarkan kategori
    val filteredNews: StateFlow<List<News>> = // Untuk menampilkan berita berdasarkan kategori yang dipilih
        combine(_newsList, _selectedCategory) { list, category ->
            if (category == "Semua") list
            else list.filter { it.category == category }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    // 4️. StateFlow counter berita dibaca
    private val _readCount = MutableStateFlow(0) // Untuk menghitung berita yang dibaca
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    fun markAsRead(newsId: Int) {
        // Cegah hitung dua kali kalau berita sama diklik lagi
        if (_readNews.add(newsId)) {
            _readCount.update { it + 1 }
        }
    }

    private val _readNews = mutableSetOf<Int>() // Untuk menyimpan ID berita yang dibaca

    // 5. Simulasi berita tiap 2 detik (dengan batas MAX_NEWS)
    private val categories = listOf("Teknologi", "Olahraga", "Musik")

    // Kumpulan berita per kategori (judul + isi berita)
    private val newsTemplates = mapOf(
        "Teknologi" to listOf(
            Pair("Apple Rilis iPhone Terbaru dengan Chip AI", "Apple baru saja meluncurkan iPhone generasi terbaru yang dilengkapi chip AI khusus. Perangkat ini diklaim mampu menjalankan model bahasa secara lokal tanpa koneksi internet."),
            Pair("Google Perbarui Algoritma Pencarian", "Google mengumumkan pembaruan besar pada algoritma pencariannya yang berfokus pada konten berkualitas tinggi. Perubahan ini mulai berlaku di seluruh dunia minggu ini."),
            Pair("Microsoft Integrasikan Copilot di Windows 12", "Microsoft resmi mengintegrasikan asisten AI Copilot secara mendalam di Windows 12. Pengguna kini bisa mengontrol seluruh sistem operasi menggunakan perintah suara."),
            Pair("Startup Lokal Kembangkan Baterai EV 1000 km", "Sebuah startup asal Bandung berhasil mengembangkan prototipe baterai kendaraan listrik dengan jangkauan hingga 1.000 km dalam sekali pengisian daya penuh."),
            Pair("Robot Humanoid Pertama Mulai Bekerja di Pabrik", "Perusahaan otomotif terkemuka mulai mengoperasikan robot humanoid di lini produksinya. Robot ini mampu melakukan tugas kompleks yang sebelumnya hanya bisa dikerjakan manusia."),
            Pair("Meta Luncurkan Kacamata AR Generasi Kedua", "Meta memperkenalkan kacamata augmented reality generasi kedua dengan tampilan yang lebih tipis dan ringan. Perangkat ini mampu menampilkan overlay digital yang sangat realistis."),
        ),
        "Olahraga" to listOf(
            Pair("Timnas Indonesia Lolos ke Semifinal Piala Asia", "Timnas Indonesia berhasil mengalahkan Korea Selatan 2-1 dan melaju ke semifinal Piala Asia. Gol kemenangan dicetak di menit akhir babak perpanjangan waktu."),
            Pair("Atlet Lari Indonesia Pecahkan Rekor Asia", "Sprinter Indonesia berhasil memecahkan rekor Asia untuk nomor 100 meter dengan catatan waktu 9,87 detik di kejuaraan atletik internasional yang digelar di Tokyo."),
            Pair("Barcelona Rekrut Pemain Muda Berbakat dari Brasil", "Barcelona FC resmi mendatangkan pemain muda berusia 18 tahun asal Brasil dengan nilai transfer mencapai 80 juta euro. Pemain ini disebut sebagai calon penerus Neymar."),
            Pair("Indonesia Raih 3 Emas di Kejuaraan Bulu Tangkis Dunia", "Kontingen Indonesia tampil gemilang di Kejuaraan Bulu Tangkis Dunia dengan meraih 3 medali emas, 2 perak, dan 1 perunggu. Ini menjadi prestasi terbaik dalam 10 tahun terakhir."),
            Pair("Formula 1: Pembalap Rookie Menang di GP Monako", "Kejutan besar terjadi di GP Monako ketika pembalap rookie asal Jepang berhasil memenangkan balapan perdananya. Ini menjadi kemenangan rookie pertama di Monako dalam 25 tahun."),
            Pair("Klub Basket NBA Kontrak Pemain Indonesia", "Sejarah baru tercipta ketika pemain basket Indonesia resmi dikontrak oleh salah satu klub NBA. Ia menjadi orang Indonesia pertama yang bermain di liga basket paling bergengsi dunia."),
        ),
        "Musik" to listOf(
            Pair("Album Baru Coldplay Pecahkan Rekor Streaming", "Album terbaru Coldplay berhasil memecahkan rekor streaming global dengan 200 juta pemutaran hanya dalam 24 jam pertama perilisannya di semua platform digital."),
            Pair("Raisa Umumkan Konser Tur 10 Kota di Indonesia", "Raisa mengumumkan tur konser bertajuk 'Ruang' yang akan menyambangi 10 kota besar di Indonesia mulai bulan depan. Tiket sudah bisa dibeli secara online mulai hari ini."),
            Pair("Musisi Indie Lokal Masuk Chart Billboard", "Musisi indie asal Yogyakarta berhasil masuk ke dalam Billboard Hot 100 untuk pertama kalinya. Lagu berjudul 'Senja di Kota Tua' viral setelah digunakan dalam ribuan video di TikTok."),
            Pair("BTS Umumkan Comeback Setelah Wajib Militer", "BTS mengumumkan akan kembali beraktivitas sebagai grup penuh setelah seluruh anggota menyelesaikan wajib militer. Album baru dijadwalkan rilis pada kuartal pertama tahun depan."),
            Pair("Festival Musik Terbesar Asia Tenggara Hadir di Jakarta", "Jakarta akan menjadi tuan rumah festival musik terbesar di Asia Tenggara tahun ini. Lebih dari 50 artis internasional dan lokal akan tampil selama tiga hari berturut-turut."),
            Pair("Dewa 19 Reuni dan Rilis Single Baru", "Dewa 19 mengejutkan publik dengan mengumumkan reuni dan merilis single baru berjudul 'Luka Lama'. Lagu ini langsung menduduki puncak tangga lagu di berbagai platform streaming."),
        )
    )

    private var simulatorJob: Job? = null // Untuk mengontrol coroutine simulator

    // State apakah simulator sedang berjalan
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun start() {
        if (simulatorJob?.isActive == true) return // untuk mencegah mencegah start double

        _isRunning.value = true

        simulatorJob = scope.launch {
            var id = (_newsList.value.lastOrNull()?.id ?: 0) + 1

            while (isActive && id <= MAX_NEWS) {
                delay(2000) // Simulasi delay tiap berita 2 detik


                val randomCategory = categories.random() // Pilih kategori secara acak
                val template = newsTemplates[randomCategory]!!.random()
                val newNews = News(
                    id = id,
                    title = template.first,
                    category = randomCategory,
                    content = template.second
                )

                _newsList.update { current ->
                    (current + newNews).takeLast(MAX_NEWS)
                }

                println("NEWS [$id/$MAX_NEWS] - ${newNews.category}: ${newNews.title}")
                id++
            }

            // Auto-stop setelah mencapai MAX_NEWS
            _isRunning.value = false
            println("Simulator selesai. Total berita: ${_newsList.value.size}")
        }
    }

    fun stop() { // Untuk menghentikan simulator
        simulatorJob?.cancel()
        simulatorJob = null
        _isRunning.value = false
    }

    fun reset() { // Untuk mereset data simulator
        stop()
        _newsList.value = emptyList()
        _readCount.value = 0
        _readNews.clear()
    }

    // 6️. Transform untuk tampilan (Flow)
    val formattedNews: Flow<List<String>> =
        filteredNews.map { list ->
            list.map { news -> "[${news.category}] ${news.title}" }
        }

    // 7. Async ambil detail berita menggunakan Coroutines
    suspend fun fetchNewsDetail(newsId: Int): String {
        val news = _newsList.value.find { it.id == newsId }
        return news?.content
            ?: "Detail berita #$newsId tidak ditemukan."
    }
}