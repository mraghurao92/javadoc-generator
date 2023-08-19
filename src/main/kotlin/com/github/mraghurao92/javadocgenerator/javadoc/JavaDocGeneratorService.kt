package com.github.mraghurao92.javadocgenerator.javadoc

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class JavaDocGeneratorService : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val methodDescription =
            Messages.showInputDialog("Enter method description:", "Generate JavaDoc", Messages.getQuestionIcon())
        if (methodDescription != null && !methodDescription.isEmpty()) {
            val javaDocTemplate = generateJavaDocTemplate(methodDescription)
            Messages.showInfoMessage("Generated JavaDoc:\n$javaDocTemplate", "JavaDoc Generated")
        }
    }

    companion object {
        private fun generateJavaDocTemplate(methodDescription: String): String? {
//        return String.format(
//                "/**\n" +
//                        " * %s\n" +
//                        " *\n" +
//                        " * @param ...\n" +
//                        " * @return ...\n" +
//                        " * @throws ...\n" +
//                        " */",
//                methodDescription
//        );
            return try {
                val aiJavaDocGenerator = AIJavaDocGenerator()
                aiJavaDocGenerator.generateJavaDoc(methodDescription)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}