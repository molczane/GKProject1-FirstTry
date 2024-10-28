package org.example.project

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import org.example.project.utils.documentationText

fun main() = application {
    var action by remember { mutableStateOf("Last action: None") }
    var clearCanvas by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Polygon Editor - Ernest Mołczan",
        state = rememberWindowState(width = 1000.dp, height = 700.dp), // Set fixed size here
        resizable = true // ~Prevent resizing
    ) {

        var showAboutDialog by remember { mutableStateOf(false) }

        MenuBar {
            Menu("O implementacji") {
                Item("Otwórz okno z tekstem", onClick = { showAboutDialog = true }, shortcut = KeyShortcut(Key.C, ctrl = true))
            }
        }

        App()

        if(showAboutDialog) {
            AlertDialog(
                modifier = Modifier
                    .background(Color.White)
                    .width(600.dp)
                    .heightIn(min = 100.dp, max = 600.dp), // Ustawienie minimalnej i maksymalnej wysokości
                onDismissRequest = { showAboutDialog = false }, // Zamknięcie dialogu
                title = { Text("O implementacji") },
                text = {
                    Box(
                        modifier = Modifier
                            .heightIn(max = 500.dp) // Ograniczenie wysokości skrolowalnej zawartości
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = documentationText,
                                modifier = Modifier
                                    .fillMaxSize()
//                                    .verticalScroll(rememberScrollState())
//                                    .horizontalScroll(rememberScrollState())
                            )
                            // Możesz dodać więcej tekstu, elementów lub dodatkowych widoków w razie potrzeby
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showAboutDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )

        }
    }
}

