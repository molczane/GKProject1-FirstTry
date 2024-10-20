package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    var action by remember { mutableStateOf("Last action: None") }
    var clearCanvas by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Polygon Editor - Ernest Mołczan",
        state = rememberWindowState(width = 1000.dp, height = 700.dp), // Set fixed size here
        resizable = true // ~Prevent resizing
    ) {

        MenuBar {
            Menu("Canvas", mnemonic = 'C') {
                Item("Clear canvas", onClick = { action = "Last action: Copy" }, shortcut = KeyShortcut(Key.C, ctrl = true))
            }
        }

        App()
    }
}

