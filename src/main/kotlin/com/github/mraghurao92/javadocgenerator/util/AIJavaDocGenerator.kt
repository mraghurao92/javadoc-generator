package com.github.mraghurao92.javadocgenerator.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_HEADER_AUTHORIZATION
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_HEADER_CONTENT_TYPE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_USER_ROLE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.APPLICATION_JSON_VALUE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.BEARER_TOKEN_TYPE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.GPT_MODEL
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.OPEN_AI_API_URL
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.OPEN_AI_PROMPT
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
        val secretManager: SecretManager = SecretManager()
        val apiKey = secretManager.getSecret(API_KEY)!!
        val request: Request = Request.Builder()
            .url(OPEN_AI_API_URL)
            .post(body)
            .addHeader(API_HEADER_CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .addHeader(API_HEADER_AUTHORIZATION, BEARER_TOKEN_TYPE + apiKey)
            .build()
        val response: Response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            assert(response.body != null)
            val typeRef: TypeReference<PromptResponse> = object : TypeReference<PromptResponse>() {}
            val aiResponse: PromptResponse = ObjectMapper()
                .readValue(response.body?.string(), typeRef)
            aiResponse.choices?.get(0)?.message?.content
        } else {
            return null
        }

    }


    companion object {

        /**
         * Generates a PromptRequest object for the given code snippet prompt.
         *
         * @param codeSnippetPrompt The code snippet prompt used to generate the prompt request.
         * @return The generated PromptRequest object.
         */
        private fun generatePromptRequest(codeSnippetPrompt: String): PromptRequest {
            val promptRequest = PromptRequest()
            val javaDocPrompt = OPEN_AI_PROMPT + codeSnippetPrompt
            val message = getMessage(javaDocPrompt)
            promptRequest.model = GPT_MODEL
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
            message.role = API_USER_ROLE
            message.content = codeSnippetPrompt
            return message
        }
    }
}