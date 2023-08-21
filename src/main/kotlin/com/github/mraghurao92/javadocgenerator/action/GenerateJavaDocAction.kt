package com.github.mraghurao92.javadocgenerator.action

import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY_NULL_EMPTY_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.ENTER_FEEDBACK
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.FAILED_TO_GENERATE_JAVA_DOC
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.INPUT_DIALOG_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.PLUGIN_NAME
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.REPORT_TO_AUTHOR_ERROR_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.REPORT_TO_AUTHOR_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.THANK_YOU_FEEDBACK_MESSAGE
import com.github.mraghurao92.javadocgenerator.util.AIJavaDocGenerator
import com.github.mraghurao92.javadocgenerator.util.SecretManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.ui.Messages

class GenerateJavaDocAction : AnAction() {
    /**
     * Overrides the actionPerformed method from the AnAction interface.
     * This method is invoked when the action is performed.
     *
     * @param e the AnActionEvent object representing the action event.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val secretManager = SecretManager()
        val apiKey = secretManager.getSecret()

        if (apiKey.isNullOrEmpty()) {
            val apiKeyInput =
                Messages.showInputDialog(
                    INPUT_DIALOG_MESSAGE, PLUGIN_NAME, Messages.getQuestionIcon()
                )
            if (!apiKeyInput.isNullOrEmpty()) {
                secretManager.storeSecret(API_KEY, apiKeyInput)
            } else {
                return Messages.showErrorDialog(API_KEY_NULL_EMPTY_MESSAGE, PLUGIN_NAME)
            }
        }

        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.project

        if (editor != null && project != null) {
            val selectedText = editor.selectionModel.selectedText
            if (selectedText != null) {
                CommandProcessor.getInstance().executeCommand(
                    project,
                    {
                        ApplicationManager.getApplication().runWriteAction { write(selectedText, e) }
                    },
                    PLUGIN_NAME,
                    UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
                )
            }
        }
    }

    /**
     * Writes a Java doc string above the selected code snippet in the editor.
     *
     * @param codeSnippet the code snippet selected in the editor
     * @param event the action event that triggered the method
     */
    private fun write(codeSnippet: String, event: AnActionEvent) {
        val editor = event.getData(PlatformDataKeys.EDITOR)!!
        val primaryCaret = editor.caretModel.primaryCaret
        val document = editor.document
        val startOffset = primaryCaret.selectionStart

        val startLineNumber = document.getLineNumber(startOffset)
        val offsetAboveSelectedText = document.getLineStartOffset(startLineNumber - 1)

        val docString = generateJavaDocString(codeSnippet)

        if (docString.isNullOrEmpty()) {
            return handleErrorForNullOrEmptyDocString()
        }

        document.insertString(offsetAboveSelectedText, docString)
    }

    /**
     * Handles error for null or empty doc string.
     * Prompts user to report to author if error occurs.
     * If user chooses to report, prompts for author message.
     * If author message is not null or empty, shows thank you message.
     * If author message is null or empty, shows error dialog to enter feedback.
     * Finally, shows error dialog for failed to generate Java doc.
     */
    private fun handleErrorForNullOrEmptyDocString() {
        val reportToAuthor =
            Messages.showYesNoDialog(
                troubleShootingGuide() + "\n" + REPORT_TO_AUTHOR_ERROR_MESSAGE,
                PLUGIN_NAME,
                Messages.getQuestionIcon()
            )
        if (reportToAuthor == Messages.YES) {
            val reportToAuthorMessage =
                Messages.showInputDialog(REPORT_TO_AUTHOR_MESSAGE, PLUGIN_NAME, Messages.getQuestionIcon())
            if (!reportToAuthorMessage.isNullOrEmpty()) {
                Messages.showInfoMessage(THANK_YOU_FEEDBACK_MESSAGE, PLUGIN_NAME)
            } else {
                Messages.showErrorDialog(ENTER_FEEDBACK, PLUGIN_NAME)
            }
        }
        Messages.showErrorDialog(FAILED_TO_GENERATE_JAVA_DOC, PLUGIN_NAME)
    }

    /**
     * Returns a string containing a troubleshooting guide for resolving issues with OpenAI API.
     *
     * The troubleshooting steps include:
     * 1. Checking if the OpenAI API key is valid.
     * 2. Checking if the OpenAI API key has sufficient credits.
     * 3. Clearing cache and restarting the IDE.
     *
     * @return the troubleshooting guide as an HTML string
     */
    private fun troubleShootingGuide(): String {
        val troubleShootingSteps = """
            Troubleshooting steps:
            1. Check if the OpenAI API key is valid.
            2. Check if the OpenAI API key has sufficient credits.
            3. Clear cache and restart IDE.
        """.trimIndent()
        return "<html><body><p style='width: 300px;'>$troubleShootingSteps</p></body></html>"
    }

    companion object {
        /**
         * Generates a JavaDoc string using a given code snippet.
         *
         * @param codeSnippet The code snippet used to generate the JavaDoc string.
         * @return The generated JavaDoc string.
         * @throws RuntimeException If an exception occurs during the generation process.
         */
        private fun generateJavaDocString(codeSnippet: String?): String? {
            return try {
                val aiJavaDocGenerator = AIJavaDocGenerator()
                aiJavaDocGenerator.generateJavaDoc(codeSnippet!!)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}
