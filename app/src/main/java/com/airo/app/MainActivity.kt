package com.airo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.airo.app.ui.nav.AiroNavHost
import com.airo.app.ui.theme.AiroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiroTheme {
                AiroNavHost()
            }
        }
    }
}
