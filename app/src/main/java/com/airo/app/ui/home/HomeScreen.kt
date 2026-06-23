package com.airo.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airo.app.data.local.SpaceEntity
import com.airo.app.ui.common.AddSpaceDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSpaceClick: (SpaceEntity) -> Unit,
    onProfileClick: () -> Unit,
) {
    val spaces by viewModel.spaces.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var spaceToRename by remember { mutableStateOf<SpaceEntity?>(null) }
    var spaceToDelete by remember { mutableStateOf<SpaceEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AIRO") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add space")
            }
        },
    ) { padding ->
        if (spaces.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "Add a space (like \"Bedroom\" or \"Hallway Closet\") to start scanning.",
                    modifier = Modifier.padding(24.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(spaces, key = { it.id }) { space ->
                    SpaceCard(
                        space = space,
                        onClick = { onSpaceClick(space) },
                        onRename = { spaceToRename = space },
                        onDelete = { spaceToDelete = space },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSpaceDialog(
            onConfirm = { name ->
                viewModel.addSpace(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }

    spaceToRename?.let { space ->
        AddSpaceDialog(
            title = "Rename space",
            initialName = space.name,
            confirmLabel = "Save",
            onConfirm = { newName ->
                viewModel.renameSpace(space.id, newName)
                spaceToRename = null
            },
            onDismiss = { spaceToRename = null },
        )
    }

    spaceToDelete?.let { space ->
        AlertDialog(
            onDismissRequest = { spaceToDelete = null },
            title = { Text("Delete \"${space.name}\"?") },
            text = { Text("This also deletes everything saved in this space.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSpace(space.id)
                    spaceToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { spaceToDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun SpaceCard(
    space: SpaceEntity,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = space.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(vertical = 16.dp),
            )
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Space options")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("Rename") }, onClick = { showMenu = false; onRename() })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() })
                }
            }
        }
    }
}
