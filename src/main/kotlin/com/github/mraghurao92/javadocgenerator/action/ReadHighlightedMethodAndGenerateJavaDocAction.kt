package com.github.mraghurao92.javadocgenerator.action

import com.github.mraghurao92.javadocgenerator.util.AIJavaDocGenerator
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy

class ReadHighlightedMethodAndGenerateJavaDocAction : AnAction() {
    /**
     * Perform an action in response to an event.
     *
     * @param e the event that triggered the action
     */
    override fun actionPerformed(e: AnActionEvent) {
        // Get the editor and project
        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.project
        if (editor != null && project != null) {
            // Get the selected text in the editor
            val selectedText = editor.selectionModel.selectedText
            if (selectedText != null) {
                CommandProcessor.getInstance().executeCommand(
                    project,
                    { ApplicationManager.getApplication().runWriteAction { write(selectedText, e) } },
                    "Plugin",
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
        val start = primaryCaret.selectionStart
        val document = editor.document
        val docString = generateJavaDocString(codeSnippet)
        if (docString != null) {
            document.insertString(start, docString)
        }

    }

    companion object {
        /**
         * Generates a JavaDoc template for the given code snippet.
         *
         * @param codeSnippet the code snippet for which the JavaDoc template needs to be generated
         * @return the generated JavaDoc template string, or null if the code snippet is null
         * @throws RuntimeException if an exception occurs while generating the JavaDoc template
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