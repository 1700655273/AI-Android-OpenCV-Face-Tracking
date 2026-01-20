package com.android.example.myapplication.detector

import org.opencv.core.Rect
import java.util.*

data class TrackedFace(
    val id: Int,
    var rect: Rect,
    var lastSeen: Long = System.currentTimeMillis(),
    var confidence: Int = 1,
    var velocityX: Double = 0.0,
    var velocityY: Double = 0.0,
    var smoothedRect: Rect? = null
)

class FaceTrackingManager {
    private val trackedFaces = mutableMapOf<Int, TrackedFace>()
    private var nextId = 0

    private val iouThreshold = 0.3
    private val smoothingFactor = 0.3
    private val minConfidenceForDisplay = 1
    private var frameCount = 0

    fun update(detections: List<Rect>): List<TrackedFace> {
        frameCount++
        val currentTime = System.currentTimeMillis()
        val usedIds = mutableSetOf<Int>()

        val filteredDetections = filterCloseDetections(detections)

        for (detection in filteredDetections) {
            var matched = false
            var bestMatchId = -1
            var bestIoU = iouThreshold

            for ((id, trackedFace) in trackedFaces) {
                if (usedIds.contains(id)) continue

                val predictedRect = predictNextPosition(trackedFace)
                val iou = calculateIoU(detection, predictedRect)

                if (iou > bestIoU) {
                    bestIoU = iou
                    bestMatchId = id
                }
            }

            if (bestMatchId != -1) {
                val trackedFace = trackedFaces[bestMatchId]!!
                trackedFace.velocityX = (detection.x - trackedFace.rect.x).toDouble()
                trackedFace.velocityY = (detection.y - trackedFace.rect.y).toDouble()

                val prevRect = trackedFace.smoothedRect ?: trackedFace.rect
                val smoothedX = (prevRect.x * smoothingFactor + detection.x * (1 - smoothingFactor)).toInt()
                val smoothedY = (prevRect.y * smoothingFactor + detection.y * (1 - smoothingFactor)).toInt()
                val smoothedW = (prevRect.width * smoothingFactor + detection.width * (1 - smoothingFactor)).toInt()
                val smoothedH = (prevRect.height * smoothingFactor + detection.height * (1 - smoothingFactor)).toInt()

                trackedFace.smoothedRect = Rect(smoothedX, smoothedY, smoothedW, smoothedH)
                trackedFace.rect = detection
                trackedFace.lastSeen = currentTime
                trackedFace.confidence = minOf(trackedFace.confidence + 1, 20)
                usedIds.add(bestMatchId)
                matched = true
            }

            if (!matched) {
                val newId = nextId++
                trackedFaces[newId] = TrackedFace(
                    id = newId,
                    rect = detection,
                    smoothedRect = detection,
                    lastSeen = currentTime,
                    confidence = 1,
                    velocityX = 0.0,
                    velocityY = 0.0
                )
                usedIds.add(newId)
            }
        }

        trackedFaces.entries.removeIf { (id, face) ->
            if (usedIds.contains(id)) {
                false
            } else {
                if (face.confidence > 0) {
                    val predictedRect = predictNextPosition(face)
                    face.rect = predictedRect
                    face.smoothedRect = predictedRect
                }
                face.confidence = maxOf(face.confidence - 2, 0)
                face.confidence <= 0
            }
        }

        return trackedFaces.values
            .filter { it.confidence >= minConfidenceForDisplay }
            .sortedBy { it.id }
    }

    private fun filterCloseDetections(detections: List<Rect>): List<Rect> {
        val filtered = mutableListOf<Rect>()
        val used = mutableSetOf<Int>()

        for (i in detections.indices) {
            if (used.contains(i)) continue

            var merged = detections[i]
            for (j in i + 1 until detections.size) {
                if (used.contains(j)) continue
                val iou = calculateIoU(detections[i], detections[j])
                if (iou > 0.5) {
                    used.add(j)
                }
            }
            filtered.add(merged)
            used.add(i)
        }

        return filtered
    }

    private fun predictNextPosition(face: TrackedFace): Rect {
        val newX = (face.rect.x + face.velocityX).toInt()
        val newY = (face.rect.y + face.velocityY).toInt()
        return Rect(newX, newY, face.rect.width, face.rect.height)
    }

    private fun calculateIoU(rect1: Rect, rect2: Rect): Double {
        val x1 = maxOf(rect1.x, rect2.x)
        val y1 = maxOf(rect1.y, rect2.y)
        val x2 = minOf(rect1.x + rect1.width, rect2.x + rect2.width)
        val y2 = minOf(rect1.y + rect1.height, rect2.y + rect2.height)

        val intersection = maxOf(0, x2 - x1) * maxOf(0, y2 - y1)

        val area1 = rect1.width * rect1.height.toDouble()
        val area2 = rect2.width * rect2.height.toDouble()
        val union = area1 + area2 - intersection

        return if (union > 0) intersection / union else 0.0
    }

    fun clear() {
        trackedFaces.clear()
        nextId = 0
    }

    fun getTrackedFaceCount(): Int = trackedFaces.size
}
