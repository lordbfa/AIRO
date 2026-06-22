package com.airo.app.ui.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airo.app.data.local.InventoryItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceDetailScreen(
    spaceName: String,
    viewModel: SpaceDetailViewModel,
    onScanClick: () -> Unit,
) {
    val savedItems by viewModel.items.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(spaceName) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onScanClick) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                Text(" Scan room")
            }
        },
    ) { padding ->
        if (savedItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "Nothing saved yet. Scan this room to find things and get storage suggestions.",
                    modifier = Modifier.padding(24.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(savedItems, key = { it.id }) { item -> InventoryItemCard(item) }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(item: InventoryItemEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (item.isWaste) "WASTE • ${item.category}" else item.category,
                style = MaterialTheme.typography.labelMedium,
                color = if (item.isWaste) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
            )
            item.suggestedLocation?.let {
                Text(text = "Put it: $it", style = MaterialTheme.typography.bodyMedium)
            }
            item.reason?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
