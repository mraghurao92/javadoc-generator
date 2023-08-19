package com.github.mraghurao92.javadocgenerator.javadoc

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.psi.PsiDocumentManager

class ReadHighlightedMethodAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Get the editor and project
//        Editor editor = e.getDataContext().getData(Editor.class);
        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.project
        if (editor != null && project != null) {
            // Get the selected text in the editor
            val selectedText = editor.selectionModel.selectedText
            if (selectedText != null) {
                // Get the PSI file and create a PsiDocumentManager
                val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
                if (psiFile != null) {
                    // Find the element at the selected text offset
                    val selectedElement = psiFile.findElementAt(editor.selectionModel.selectionStart)
                    if (selectedElement != null) {
                        // Find the closest PsiMethod by traversing the PSI tree
                        val containingClassName =
                            selectedElement.text

                        // Print method information
                        println("Selected Method: $selectedText")
                        println("Containing Class: $selectedText")
//                        Messages.showInfoMessage("Generated JavaDoc:\n$selectedText", "JavaDoc Generated")
                        CommandProcessor.getInstance().executeCommand(
                            project,
                            { ApplicationManager.getApplication().runWriteAction { write(selectedText, e) } },
                            "Plugin",
                            UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
                        )
                    }
                }
            }
        }
    }

    /**
     * @param event ActionSystem event
     */
    private fun write(codeSnippet: String, event: AnActionEvent) {
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