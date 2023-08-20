package com.github.mraghurao92.javadocgenerator.action

import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.API_KEY_NULL_EMPTY_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.INPUT_DIALOG_MESSAGE
import com.github.mraghurao92.javadocgenerator.constants.AppConstants.Companion.PLUGIN_NAME
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
     * This method is called when an action event is triggered. It overrides the actionPerformed method of AnAction class.
     * It retrieves the editor and project from the AnActionEvent object. If both editor and project are not null, it retrieves the selected text in the editor.
     * If the selected text is not null, it executes a command using the CommandProcessor instance and the project. The command is executed within a write action using ApplicationManager.
     * The selected text and the event are passed to the write method. The command is executed with the name "Plugin" and with UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION.
     *
     * @param e the AnActionEvent object representing the action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        // Get the editor and project
        val secretManager = SecretManager()
        if (secretManager.getSecret(API_KEY).isNullOrEmpty()) {
            val openAiApiKey =
                Messages.showInputDialog(
                    INPUT_DIALOG_MESSAGE, PLUGIN_NAME, Messages
                        .getQuestionIcon()
                )
            if (!openAiApiKey.isNullOrEmpty()) {
                secretManager.storeSecret(API_KEY, openAiApiKey)
            } else {
                return Messages.showErrorDialog(API_KEY_NULL_EMPTY_MESSAGE, PLUGIN_NAME)
            }

        }
        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.project
        if (editor != null && project != null) {
            // Get the selected text in the editor
            val selectedText = editor.selectionModel.selectedText
            if (selectedText != null) {
                CommandProcessor.getInstance().executeCommand(
                    project,
                    { ApplicationManager.getApplication().runWriteAction { write(selectedText, e) } },
                    PLUGIN_NAME,
                    UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
                )
            }
        }
    }


    /**
     * Writes a JavaDoc string for the provided code snippet.
     *
     * @param codeSnippet the code snippet to generate the JavaDoc string for
     * @param event the AnActionEvent object that triggered the method
     */
    private fun write(codeSnippet: String, event: AnActionEvent) {
        val editor = event.getData(PlatformDataKeys.EDITOR)!!
        val primaryCaret = editor.caretModel.primaryCaret
        val document = editor.document
        val startOffset = primaryCaret.selectionStart

        // Calculate the line number for the start offset
        val startLineNumber = document.getLineNumber(startOffset)

        // Calculate the offset for the beginning of the line above the selected text
        val offsetAboveSelectedText = document.getLineStartOffset(startLineNumber - 1)

        val docString = generateJavaDocString(codeSnippet)
        if (docString != null) {
            document.insertString(offsetAboveSelectedText, docString)
        }

    }

    companion object {
        /**
         * Generates a JavaDoc String for the given code snippet.
         *
         * @param codeSnippet the code snippet for which the JavaDoc String is to be generated
         * @return the generated JavaDoc String
         * @throws RuntimeException if an exception occurs while generating the JavaDoc String
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