
package com.reelsclipper

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class HighlightSuggestion(
    val startSeconds: Int,
    val endSeconds: Int,
    val score: Int
)

data class VideoAnalysisResult(
    val durationSeconds: Int,
    val width: Int,
    val height: Int,
    val sampledFrames: Int,
    val suggestions: List<HighlightSuggestion>
)

object VideoAnalyzer {

    fun analyze(
        context: Context,
        uri: Uri
    ): VideoAnalysisResult {

        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, uri)

            val durationMs =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLongOrNull() ?: 0L

            val width =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
                )?.toIntOrNull() ?: 0

            val height =
                retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
                )?.toIntOrNull() ?: 0

            val duration = (durationMs / 1000L).toInt()

            if (duration <= 0) {
                error("Could not read video duration.")
            }

            val changes = mutableListOf<Pair<Int, Double>>()
            var previousPixels: IntArray? = null
            var sampledFrames = 0

            val sampleStep = 2

            for (second in 0 until duration step sampleStep) {

                val bitmap = retriever.getFrameAtTime(
                    second * 1_000_000L,
                    MediaMetadataRetriever.OPTION_CLOSEST
                ) ?: continue

                val small = Bitmap.createScaledBitmap(
                    bitmap, 32, 18, true
                )

                if (small !== bitmap) {
                    bitmap.recycle()
                }

                val pixels = IntArray(32 * 18)
                small.getPixels(
                    pixels, 0, 32, 0, 0, 32, 18
                )
                small.recycle()

                sampledFrames++

                val previous = previousPixels

                if (previous != null) {
                    var difference = 0L

                    for (i in pixels.indices) {
                        val oldPixel = previous[i]
                        val newPixel = pixels[i]

                        val oldGray = (
                            ((oldPixel shr 16) and 255) * 30 +
                            ((oldPixel shr 8) and 255) * 59 +
                            (oldPixel and 255) * 11
                        ) / 100

                        val newGray = (
                            ((newPixel shr 16) and 255) * 30 +
                            ((newPixel shr 8) and 255) * 59 +
                            (newPixel and 255) * 11
                        ) / 100

                        difference += abs(oldGray - newGray)
                    }

                    val score =
                        difference.toDouble() /
                        (pixels.size * 255.0)

                    changes.add(second to score)
                }

                previousPixels = pixels
            }

            val suggestions = changes
                .filter { it.second > 0.08 }
                .sortedByDescending { it.second }
                .fold(mutableListOf<HighlightSuggestion>()) { result, change ->

                    val center = change.first
                    val start = max(0, center - 8)
                    val end = min(duration, center + 12)

                    val overlaps = result.any {
                        start < it.endSeconds &&
                        end > it.startSeconds
                    }

                    if (!overlaps && end > start) {
                        result.add(
                            HighlightSuggestion(
                                startSeconds = start,
                                endSeconds = end,
                                score = (change.second * 100)
                                    .toInt()
                                    .coerceIn(0, 100)
                            )
                        )
                    }

                    result
                }
                .take(3)
                .sortedBy { it.startSeconds }

            return VideoAnalysisResult(
                durationSeconds = duration,
                width = width,
                height = height,
                sampledFrames = sampledFrames,
                suggestions = suggestions
            )

        } finally {
            retriever.release()
        }
    }
}
