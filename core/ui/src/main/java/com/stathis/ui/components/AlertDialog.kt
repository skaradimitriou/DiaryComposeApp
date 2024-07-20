package com.stathis.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    dialogOpened: Boolean,
    onYesClicked: () -> Unit,
    closeDialog: () -> Unit
) {
    if (dialogOpened) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 24.dp),
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message
                )
            },

            onDismissRequest = closeDialog,
            confirmButton = {
                Button(onClick = {
                    onYesClicked()
                    closeDialog()
                }) {
                    Text(
                        text = "Yes"
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { closeDialog() }
                ) {
                    Text(
                        text = "No"
                    )
                }
            }
        )
    }
}