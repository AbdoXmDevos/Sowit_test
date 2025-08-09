package ma.abdokarimi.sowittechtest.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ma.abdokarimi.sowittechtest.entity.Area

/**
 * Dialog component for confirming area deletion
 */
@Composable
fun DeleteAreaDialog(
    isVisible: Boolean,
    areaToDelete: Area?,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible && areaToDelete != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Supprimer la zone")
            },
            text = {
                Text("Êtes-vous sûr de vouloir supprimer \"${areaToDelete.name}\" ? Cette action ne peut pas être annulée.")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete
                ) {
                    Text(
                        "Supprimer",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Annuler")
                }
            },
            modifier = modifier
        )
    }
}
