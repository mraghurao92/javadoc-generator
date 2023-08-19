package com.github.mraghurao92.javadocgenerator.javadoc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.nio.charset.StandardCharsets

class AIJavaDocGenerator {

    @Throws(Exception::class)
    fun generateJavaDoc(codeSnippet: String): String? {
        val client = OkHttpClient()
        val body: RequestBody = ObjectMapper().writeValueAsString(
            generatePromptRequest(codeSnippet)
        )
            .toByteArray(charset(StandardCharsets.UTF_8.name()))
            .toRequestBody(
                "".toMediaTypeOrNull(),
                0
            )
        val request: Request = Request.Builder()
            .url(AI_API_URL)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "<ADD-YOUR-GPT-KEY>")
            .build()
        val response: Response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            assert(response.body != null)
            val typeRef: TypeReference<PromptResponse> = object : TypeReference<PromptResponse>() {}
            val aiResponse: PromptResponse = ObjectMapper()
                .readValue(response.body?.string(), typeRef)
            aiResponse.choices?.get(0)?.message?.content
        } else {
            null
        }
    }


    companion object {
        private const val AI_API_URL = "https://api.openai.com/v1/chat/completions"
        private fun generatePromptRequest(codeSnippetPrompt: String): PromptRequest {
            val promptRequest = PromptRequest()
            val promptForDocStr =
                "Provide java doc string for this method, only return javadoc not the method: $codeSnippetPrompt"
            val message = getMessage(promptForDocStr)
            promptRequest.model = "gpt-3.5-turbo";
            promptRequest.messages = listOf(message)
            return promptRequest
        }

        private fun getMessage(codeSnippetPrompt: String): Message {
            val message = Message()
            message.role = "user"
            message.content = codeSnippetPrompt
            return message
        }
    }
}