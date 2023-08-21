package com.github.mraghurao92.javadocgenerator.constants

class AppConstants private constructor() {

    companion object {
        const val OPEN_AI_API_URL = "https://api.openai.com/v1/chat/completions"
        const val API_USER_ROLE = "user"
        const val API_HEADER_CONTENT_TYPE = "Content-Type"
        const val APPLICATION_JSON_VALUE = "application/json"
        const val API_HEADER_AUTHORIZATION = "Authorization"
        const val OPEN_AI_PROMPT = "Provide java doc string for this method, only return javadoc not the method: "
        const val GPT_MODEL = "gpt-3.5-turbo"
        const val API_KEY = "OPEN_AI_API_KEY"
        const val INPUT_DIALOG_MESSAGE = "ENTER OPEN AI API_KEY"
        const val API_KEY_NULL_EMPTY_MESSAGE = "API KEY CANNOT BE NULL/EMPTY"
        const val PLUGIN_NAME = "JavaDoc Generator"
        const val BEARER_TOKEN_TYPE = "Bearer "
        const val REPORT_TO_AUTHOR_ERROR_MESSAGE =
            "Unable to generate JavaDoc for the selected method. Would you like to report this to the author?"
        const val REPORT_TO_AUTHOR_MESSAGE = "Please enter a message to the author."
        const val REPORT_TO_AUTHOR_TITLE = "Report to Author"
        const val THANK_YOU_FEEDBACK_MESSAGE = "Thank you for your feedback!"
        const val ENTER_FEEDBACK = "Please enter a message to the author."
        const val FAILED_TO_GENERATE_JAVA_DOC = "Unable to generate JavaDoc for the selected text."
    }

}