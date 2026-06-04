package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.*

@Composable
fun WelcomeIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2

        // Base soft decorative circles
        drawCircle(
            color = PrimaryLight.copy(alpha = 0.08f),
            radius = w * 0.3f,
            center = Offset(centerX - w * 0.1f, centerY)
        )
        drawCircle(
            color = SecondaryLight.copy(alpha = 0.08f),
            radius = w * 0.25f,
            center = Offset(centerX + w * 0.15f, centerY - h * 0.1f)
        )

        // Draw a premium floating notebook device layout
        val cardW = w * 0.45f
        val cardH = h * 0.6f
        val cardX = centerX - cardW / 2
        val cardY = centerY - cardH / 2

        // Main card outline
        drawRoundRect(
            color = PrimaryLight,
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(24f, 24f),
            style = Stroke(width = 6f)
        )

        // Decorative grid line details
        for (i in 1..4) {
            val lineY = cardY + cardH * (0.2f + i * 0.15f)
            drawLine(
                color = GrayTextLight.copy(alpha = 0.4f),
                start = Offset(cardX + cardW * 0.2f, lineY),
                end = Offset(cardX + cardW * 0.8f, lineY),
                strokeWidth = 4f
            )
        }

        // Draw checklist checkboxes
        for (i in 1..3) {
            val boxY = cardY + cardH * (0.2f + i * 0.15f) - 10f
            drawRoundRect(
                color = SuccessGreen,
                topLeft = Offset(cardX + cardW * 0.08f, boxY),
                size = Size(20f, 20f),
                cornerRadius = CornerRadius(4f, 4f),
                style = Stroke(width = 3f)
            )
        }

        // Animated checkmark highlight floating
        val checkPath = Path().apply {
            moveTo(cardX + cardW * 0.65f, centerY + h * 0.1f)
            lineTo(cardX + cardW * 0.75f, centerY + h * 0.18f)
            lineTo(cardX + cardW * 0.95f, centerY - h * 0.05f)
        }
        drawPath(
            path = checkPath,
            color = SuccessGreen,
            style = Stroke(width = 10f)
        )
    }
}

@Composable
fun ProductivityIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2

        // Soft visual orbit lines
        drawCircle(
            color = SecondaryLight.copy(alpha = 0.08f),
            radius = w * 0.35f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 4f)
        )

        // Draw a beautiful productivity geometric clock structure
        drawCircle(
            color = PrimaryLight,
            radius = w * 0.18f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 6f)
        )

        // Hour and minute indicators
        drawLine(
            color = PrimaryLight,
            start = Offset(centerX, centerY),
            end = Offset(centerX + w * 0.08f, centerY - h * 0.08f),
            strokeWidth = 8f
        )
        drawLine(
            color = SecondaryLight,
            start = Offset(centerX, centerY),
            end = Offset(centerX - w * 0.12f, centerY),
            strokeWidth = 6f
        )

        // Small nodes/stars rotating
        drawCircle(
            color = SuccessGreen,
            radius = 16f,
            center = Offset(centerX + w * 0.25f, centerY - h * 0.1f)
        )
        drawCircle(
            color = WarningOrange,
            radius = 12f,
            center = Offset(centerX - w * 0.22f, centerY + h * 0.15f)
        )
    }
}

@Composable
fun EmptyStateIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.8f)
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2

        // Soft diffuse clouds
        drawCircle(
            color = GrayTextLight.copy(alpha = 0.08f),
            radius = w * 0.2f,
            center = Offset(centerX, centerY)
        )

        val shieldPath = Path().apply {
            moveTo(centerX, centerY - h * 0.25f)
            quadraticTo(centerX + w * 0.15f, centerY - h * 0.25f, centerX + w * 0.15f, centerY)
            quadraticTo(centerX + w * 0.15f, centerY + h * 0.2f, centerX, centerY + h * 0.3f)
            quadraticTo(centerX - w * 0.15f, centerY + h * 0.2f, centerX - w * 0.15f, centerY)
            quadraticTo(centerX - w * 0.15f, centerY - h * 0.25f, centerX, centerY - h * 0.25f)
        }

        drawPath(
            path = shieldPath,
            color = PrimaryLight.copy(alpha = 0.15f)
        )

        drawPath(
            path = shieldPath,
            color = PrimaryLight,
            style = Stroke(width = 4f)
        )

        // Inner checklist outline
        drawLine(
            color = SuccessGreen,
            start = Offset(centerX - w * 0.06f, centerY + h * 0.02f),
            end = Offset(centerX - w * 0.01f, centerY + h * 0.08f),
            strokeWidth = 6f
        )
        drawLine(
            color = SuccessGreen,
            start = Offset(centerX - w * 0.01f, centerY + h * 0.08f),
            end = Offset(centerX + w * 0.07f, centerY - h * 0.06f),
            strokeWidth = 6f
        )
    }
}
