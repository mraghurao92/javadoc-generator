package com.github.mraghurao92.javadocgenerator.javadoc

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.ui.Messages

class TextCommentGeneratorAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent) {
        val currentProject = e.getData(PlatformDataKeys.PROJECT)
        val inputMethod =
            Messages.showInputDialog("Enter method:", "Generate Java Doc", Messages.getQuestionIcon())
        if (inputMethod != null) {
            CommandProcessor.getInstance().executeCommand(
                currentProject,
                { ApplicationManager.getApplication().runWriteAction { write(inputMethod, e) } },
                "Plugin",
                UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
            )
        }
    }

    /**
     * @param event ActionSystem event
     */
    private fun write(codeSnippet: String?, event: AnActionEvent) {
        val editor = event.getData(PlatformDataKeys.EDITOR)!!
        val primaryCaret = editor.caretModel.primaryCaret
        val start = primaryCaret.selectionStart
        val document = editor.document
        document.insertString(start, generateJavaDocTemplate(codeSnippet)!!)
    }

    companion object {
        private fun generateJavaDocTemplate(codeSnippet: String?): String? {
            return try {
                val aiJavaDocGenerator = AIJavaDocGenerator()
                aiJavaDocGenerator.generateJavaDoc(codeSnippet!!)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}