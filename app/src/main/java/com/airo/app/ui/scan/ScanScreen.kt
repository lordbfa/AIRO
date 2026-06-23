package com.airo.app.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airo.app.domain.model.DetectedObject
import com.airo.app.util.toResizedJpegBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    spaceName: String,
    viewModel: ScanViewModel,
    onDone: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Done) onDone()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Scan: $spaceName") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ScanUiState.Capturing -> CameraPermissionGate(
                    onImageCaptured = { bytes ->
                        viewModel.onPhotoCaptured(bytes.toResizedJpegBase64())
                    },
                )

                is ScanUiState.Analyzing -> LoadingMessage("Scanning the room...")
                is ScanUiState.Saving -> LoadingMessage("Saving...")
                is ScanUiState.Done -> Unit

                is ScanUiState.Reviewing -> ReviewList(
                    detectedItems = state.items,
                    onAnswer = viewModel::answerClarification,
                    onSave = viewModel::saveAndFinish,
                )

                is ScanUiState.Error -> ErrorMessage(state.message, onRetry = viewModel::retry)
            }
        }
    }
}

@Composable
private fun CameraPermissionGate(onImageCaptured: (ByteArray) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasPermission = it
    }

    if (hasPermission) {
        CameraPreview(onImageCaptured = onImageCaptured, onError = {})
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("AIRO needs camera access to scan the room.")
            Button(
                onClick = { launcher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.padding(top = 12.dp),
            ) { Text("Grant camera permission") }
        }
    }
}

@Composable
private fun LoadingMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(text = message, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) { Text("Try again") }
    }
}

@Composable
private fun ReviewList(
    detectedItems: List<DetectedObject>,
    onAnswer: (String, String) -> Unit,
    onSave: () -> Unit,
) {
    val allResolved = detectedItems.isNotEmpty() && detectedItems.all { it.suggestedLocation != null }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(detectedItems, key = { it.id }) { item ->
                DetectedItemCard(item = item, onAnswerClarification = { answer -> onAnswer(item.id, answer) })
            }
        }
        Button(
            onClick = onSave,
            enabled = allResolved,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) { Text("Save all (${detectedItems.size})") }
    }
}
