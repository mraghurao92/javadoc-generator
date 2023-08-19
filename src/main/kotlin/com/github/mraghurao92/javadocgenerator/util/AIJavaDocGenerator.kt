package com.github.mraghurao92.javadocgenerator.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mraghurao92.javadocgenerator.model.Message
import com.github.mraghurao92.javadocgenerator.model.PromptRequest
import com.github.mraghurao92.javadocgenerator.model.PromptResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.nio.charset.StandardCharsets

class AIJavaDocGenerator {


    /**
     * Generates JavaDoc for the generateJavaDoc method.
     *
     * @param codeSnippet The code snippet to be processed.
     * @return The content of the message from the first choice of the AI response, if the response is successful; otherwise, null.
     * @throws Exception if an error occurs while executing the HTTP request.
     */
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
            .addHeader("Authorization", "<YOUR-OPEN-API-KEY>")
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

        /**
         * Generates a PromptRequest object for the given code snippet prompt.
         *
         * @param codeSnippetPrompt The code snippet prompt used to generate the prompt request.
         * @return The generated PromptRequest object.
         */
        private fun generatePromptRequest(codeSnippetPrompt: String): PromptRequest {
            val promptRequest = PromptRequest()
            val promptForDocStr =
                "Provide java doc string for this method, only return javadoc not the method: $codeSnippetPrompt"
            val message = getMessage(promptForDocStr)
            promptRequest.model = "gpt-3.5-turbo";
            promptRequest.messages = listOf(message)
            return promptRequest
        }

        /**
         * Constructs a new Message object with a specified codeSnippetPrompt.
         *
         * @param codeSnippetPrompt the code snippet prompt for the message
         * @return the constructed Message object
         */
        private fun getMessage(codeSnippetPrompt: String): Message {
            val message = Message()
            message.role = "user"
            message.content = codeSnippetPrompt
            return message
        }
    }
}