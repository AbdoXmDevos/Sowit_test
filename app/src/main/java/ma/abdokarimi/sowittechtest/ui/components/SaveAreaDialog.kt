package ma.abdokarimi.sowittechtest.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Dialog component for saving a new area with a name
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAreaDialog(
    isVisible: Boolean,
    areaName: String,
    onAreaNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Nommez votre zone") },
            text = {
                OutlinedTextField(
                    value = areaName,
                    onValueChange = onAreaNameChange,
                    label = { Text("Nom de la zone") },
                    singleLine = true,
                    modifier = modifier
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onSave,
                    enabled = areaName.isNotBlank()
                ) {
                    Text("Sauvegarder")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}
