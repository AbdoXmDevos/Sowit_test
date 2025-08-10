package ma.abdokarimi.sowittechtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*

import ma.abdokarimi.sowittechtest.ui.viewmodels.MainViewModel
import ma.abdokarimi.sowittechtest.ui.components.MainScreen

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen(viewModel)
            }
        }
    }
}




