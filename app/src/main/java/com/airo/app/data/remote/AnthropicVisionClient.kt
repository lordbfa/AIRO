package com.airo.app.data.remote

import com.airo.app.data.remote.dto.AnthropicResponse
import com.airo.app.data.remote.dto.PlacementSuggestionDto
import com.airo.app.data.remote.dto.SceneAnalysisDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Thin client around the Anthropic Messages API for scene understanding and placement reasoning. */
class AnthropicVisionClient(private val apiKey: String, private val model: String = DEFAULT_MODEL) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun analyzeScene(imageBase64: String, spaceName: String): SceneAnalysisDto =
        withContext(Dispatchers.IO) {
            val systemPrompt = """
                You are AIRO, a home-organization assistant. You are shown a photo of a space
                called "$spaceName". Identify every distinct physical object visible, including
                trash and waste. For each object decide if you can confidently suggest where it
                belongs in a typical home without more information. If you can, fill
                suggestedLocation and reason directly and set needsClarification to false. If the
                object's type is ambiguous and the right storage spot depends on a sub-type the
                photo doesn't make clear (for example a "bag" could be a laptop bag, gym bag,
                handbag, or grocery bag, each stored differently), set needsClarification to true,
                write a short clarificationQuestion, give 3-5 concise clarificationOptions, and
                leave suggestedLocation/reason null.

                Respond with ONLY valid JSON matching exactly this shape, no prose, no markdown
                fences:
                {"items":[{"name":string,"category":one of
                ["BAG","CLOTHING","FOOTWEAR","ELECTRONICS","DOCUMENT","TOILETRY","KITCHENWARE","WASTE","OTHER"],
                "isWaste":boolean,"needsClarification":boolean,
                "clarificationQuestion":string|null,"clarificationOptions":string[],
                "suggestedLocation":string|null,"reason":string|null}]}
            """.trimIndent()

            val requestBody = buildJsonObject {
                put("model", model)
                put("max_tokens", 2048)
                put("system", systemPrompt)
                put("messages", buildJsonArray {
                    add(buildJsonObject {
                        put("role", "user")
                        put("content", buildJsonArray {
                            add(buildJsonObject {
                                put("type", "image")
                                put("source", buildJsonObject {
                                    put("type", "base64")
                                    put("media_type", "image/jpeg")
                                    put("data", imageBase64)
                                })
                            })
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", "Scan this room and list everything you see.")
                            })
                        })
                    })
                })
            }

            val responseText = execute(requestBody.toString())
            json.decodeFromString<SceneAnalysisDto>(stripCodeFences(responseText))
        }

    suspend fun suggestPlacement(
        itemName: String,
        category: String,
        spaceName: String,
        clarificationAnswer: String,
    ): PlacementSuggestionDto = withContext(Dispatchers.IO) {
        val systemPrompt = """
            You are AIRO, a home-organization assistant. A user is putting away an item found in
            "$spaceName". The item is "$itemName" (category: $category) and the user clarified:
            "$clarificationAnswer". Suggest exactly one concrete, practical storage location for
            it in a typical home, and a one-sentence reason.

            Respond with ONLY valid JSON matching exactly this shape, no prose, no markdown
            fences: {"suggestedLocation":string,"reason":string}
        """.trimIndent()

        val requestBody = buildJsonObject {
            put("model", model)
            put("max_tokens", 512)
            put("system", systemPrompt)
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "user")
                    put("content", "Where should this go?")
                })
            })
        }

        val responseText = execute(requestBody.toString())
        json.decodeFromString<PlacementSuggestionDto>(stripCodeFences(responseText))
    }

    private fun execute(jsonBody: String): String {
        val request = Request.Builder()
            .url(API_URL)
            .header("x-api-key", apiKey)
            .header("anthropic-version", ANTHROPIC_VERSION)
            .header("content-type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyString = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("Anthropic API error ${response.code}: $bodyString")
            }
            val parsed = json.decodeFromString<AnthropicResponse>(bodyString)
            if (parsed.error != null) {
                throw IOException("Anthropic API error: ${parsed.error.message}")
            }
            return parsed.content.firstOrNull { it.type == "text" }?.text
                ?: throw IOException("Anthropic API returned no text content")
        }
    }

    private fun stripCodeFences(text: String): String =
        text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

    companion object {
        private const val API_URL = "https://api.anthropic.com/v1/messages"
        private const val ANTHROPIC_VERSION = "2023-06-01"
        const val DEFAULT_MODEL = "claude-sonnet-4-6"
    }
}
