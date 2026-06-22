package com.airo.app.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airo.app.domain.model.DetectedObject

@Composable
fun DetectedItemCard(
    item: DetectedObject,
    onAnswerClarification: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (item.isWaste) "WASTE • ${item.category}" else item.category.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (item.isWaste) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
            )

            when {
                item.isResolving -> ResolvingIndicator()
                item.needsClarification && item.suggestedLocation == null ->
                    ClarificationPrompt(item, onAnswerClarification)
                item.suggestedLocation != null -> SuggestionDisplay(item)
            }
        }
    }
}

@Composable
private fun ResolvingIndicator() {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp).size(16.dp), strokeWidth = 2.dp)
        Text(text = "Finding the best spot...", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ClarificationPrompt(item: DetectedObject, onAnswer: (String) -> Unit) {
    var customAnswer by rememberSaveable(item.id) { mutableStateOf("") }
    var showCustomField by rememberSaveable(item.id) { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        item.clarificationQuestion?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item.clarificationOptions.forEach { option ->
                AssistChip(onClick = { onAnswer(option) }, label = { Text(option) })
            }
            if (!showCustomField) {
                TextButton(onClick = { showCustomField = true }) { Text("Something else...") }
            } else {
                OutlinedTextField(
                    value = customAnswer,
                    onValueChange = { customAnswer = it },
                    label = { Text("Describe it") },
                    singleLine = true,
                )
                TextButton(
                    onClick = { onAnswer(customAnswer) },
                    enabled = customAnswer.isNotBlank(),
                ) { Text("Use this") }
            }
        }
    }
}

@Composable
private fun SuggestionDisplay(item: DetectedObject) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(text = "Put it: ${item.suggestedLocation}", style = MaterialTheme.typography.bodyMedium)
        item.reason?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
    }
}
