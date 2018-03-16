package com.github.deckyfx.gdgengradle

import com.github.deckyfx.gdgengradle.generator.DaoGenerator
import com.github.deckyfx.gdgengradle.generator.Schema
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

class GenerateModel extends AbstractGenerator {
    GenerateModel() {
        super()
    }
}

