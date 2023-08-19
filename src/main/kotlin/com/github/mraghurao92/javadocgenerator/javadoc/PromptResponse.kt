package com.github.mraghurao92.javadocgenerator.javadoc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class PromptResponse {
    var id: String? = null
    var created = 0
    var model: String? = null
    var choices: List<Choice>? = null
    var usage: Usage? = null
}