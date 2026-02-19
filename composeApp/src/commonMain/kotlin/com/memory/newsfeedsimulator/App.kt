package com.memory.newsfeedsimulator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {

    val simulator = remember { NewsFeedSimulator() }

    val newsList       by simulator.filteredNews.collectAsState()
    val selectedCategory by simulator.selectedCategory.collectAsState()
    val readCount      by simulator.readCount.collectAsState()      // ✅ 4️⃣ StateFlow readCount
    val isRunning      by simulator.isRunning.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    // State untuk dialog detail berita
    var selectedNews    by remember { mutableStateOf<News?>(null) }
    var detailText      by remember { mutableStateOf("") }
    var loadingDetail   by remember { mutableStateOf(false) }

    val categories = listOf("Semua", "Teknologi", "Olahraga", "Musik")

    // Ambil detail berita secara async setiap kali selectedNews berubah ✅ 5️⃣
    LaunchedEffect(selectedNews) {
        val news = selectedNews ?: return@LaunchedEffect
        loadingDetail = true
        simulator.markAsRead(news.id)                    // tambah readCount
        detailText = simulator.fetchNewsDetail(news.id)  // suspend fun — async coroutine
        loadingDetail = false
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("News Feed Simulator", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Tampilkan readCount (Penghitung jumlah berita yang sudah dibaca oleh user) dari StateFlow
            Text(
                text = "📖 Berita dibaca: $readCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Start / Stop / Reset
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { simulator.start() },
                    enabled = !isRunning
                ) { Text("▶ Start") }

                Button(
                    onClick = { simulator.stop() },
                    enabled = isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("⏹ Stop") }

                OutlinedButton(onClick = { simulator.reset() }) {
                    Text("↺ Reset")
                }
            }

            // Tampilkan status simulator jika sedang berjalan dan jika sudah selesai
            if (isRunning) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "🔴 Simulator berjalan... (maks ${NewsFeedSimulator.MAX_NEWS} berita)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (newsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "✅ Simulator selesai",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown kategori berita
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Kategori") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                simulator.setCategory(category)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Menampilkan ${newsList.size} berita", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Tampilkan daftar berita dengan lazy column
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(newsList, key = { it.id }) { news ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedNews = news }  // Klik → fetch detail async
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Gunakan formattedNews transform: "[Kategori] Judul"
                            Text(
                                text = "[${news.category}] ${news.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text( // Klik untuk baca detail berita
                                text = "Tap untuk baca detail →",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Dialog detail berita (muncul setelah fetchNewsDetail async selesai)
        if (selectedNews != null) {
            AlertDialog(
                onDismissRequest = { selectedNews = null },
                title = { Text(selectedNews!!.title) },
                text = {
                    if (loadingDetail) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text("Mengambil detail berita...")
                        }
                    } else {
                        Text(detailText)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedNews = null }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}