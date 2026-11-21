package com.reelsclipper

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private val Ink = Color(0xFF0B0D12)
private val Panel = Color(0xFF171B24)
private val Accent = Color(0xFFB7F36B)
private val Muted = Color(0xFF9AA3B2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ReelsClipperApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReelsClipperApp() {
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var youtubeUrl by remember { mutableStateOf("") }
    var showUrlInfo by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Import a video to get started") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedVideo = uri
            status = "Video ready • Local preview"
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Ink,
            background = Ink,
            surface = Panel,
            onSurface = Color.White
        )
    ) {
        Scaffold(containerColor = Ink) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Accent, RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ContentCut, null, tint = Ink)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("REELS CLIPPER", fontWeight = FontWeight.Black, fontSize = 19.sp, letterSpacing = 1.sp)
                        Text("AI SHORT-FORM STUDIO", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(color = Panel, shape = RoundedCornerShape(50)) {
                        Text("BETA", color = Accent, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                    }
                }

                Spacer(Modifier.height(30.dp))
                Text("Turn long videos into", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("scroll-stopping clips.", color = Accent, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Import a video. Let AI find the moments, write captions and suggest emojis.",
                    color = Muted, fontSize = 14.sp, lineHeight = 21.sp
                )

                Spacer(Modifier.height(24.dp))
                Surface(
                    color = Panel,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, tint = Accent, contentDescription = null)
                            Spacer(Modifier.width(9.dp))
                            Text("YOUR SOURCE VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { picker.launch(arrayOf("video/*")) },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (selectedVideo == null) "Choose from device" else "Choose another video")
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(Modifier.weight(1f), color = Color(0xFF343B48))
                            Text("  OR  ", color = Muted, fontSize = 10.sp)
                            HorizontalDivider(Modifier.weight(1f), color = Color(0xFF343B48))
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = youtubeUrl,
                            onValueChange = { youtubeUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Paste a YouTube link", color = Muted) },
                            leadingIcon = { Icon(Icons.Default.Link, null, tint = Muted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Color(0xFF343B48),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showUrlInfo = true }) {
                            Text("How YouTube links work", color = Accent, fontSize = 12.sp)
                        }
                    }
                }

                selectedVideo?.let { uri ->
                    Spacer(Modifier.height(18.dp))
                    Text("VIDEO PREVIEW", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(9.dp))
                    VideoPreview(uri)
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        status = if (selectedVideo != null) {
                            "Imported successfully. AI processing is the next pipeline step."
                        } else if (youtubeUrl.isNotBlank()) {
                            "YouTube link saved for source validation. Import an authorized video file to process it."
                        } else {
                            "Choose a video first."
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text("START AI CLIPPING", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(status, color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(24.dp))

                Text("WHAT AI WILL CREATE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                FeatureRow(Icons.Default.Subtitles, "Synced captions", "English • Hindi • Hinglish • Odia")
                FeatureRow(Icons.Default.ContentCut, "Smart highlight clips", "Find complete, interesting moments")
                FeatureRow(Icons.Default.Movie, "Vertical-ready exports", "9:16 framing • emoji suggestions • 1080p target")
                Spacer(Modifier.height(30.dp))
                Text("Made for creators • Local-first • No ads", color = Muted, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(24.dp))
            }
        }

        if (showUrlInfo) {
            AlertDialog(
                onDismissRequest = { showUrlInfo = false },
                title = { Text("YouTube link support") },
                text = {
                    Text("A YouTube URL can be stored for reference and metadata. This app will not bypass YouTube download restrictions. To edit a video, import a local copy that you own or have permission to use.")
                },
                confirmButton = {
                    TextButton(onClick = { showUrlInfo = false }) { Text("Got it", color = Accent) }
                },
                containerColor = Panel,
                titleContentColor = Color.White,
                textContentColor = Muted
            )
        }
    }
}

@Composable
private fun VideoPreview(uri: Uri) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        },
        update = { it.player = player },
        modifier = Modifier.fillMaxWidth().height(230.dp)
    )
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = Panel, shape = RoundedCornerShape(12.dp)) {
            Icon(icon, null, tint = Accent, modifier = Modifier.padding(12.dp).size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}
