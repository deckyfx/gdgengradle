package com.github.deckyfx.gdgengradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class GDGenGradle implements Plugin<Project> {
    final static String TASK_NAME           = "generateModels";
    final static String TASK_GROUP          = "gdgengradle";

    @Override
    void apply(Project project) {
        project.task(TASK_NAME,        type:GeneratorTask)

        Set<Task> generateModelTask   = project.getTasksByName(TASK_NAME, false)
        generateModelTask.each {
            task->
                GeneratorTask t        = (GeneratorTask) task;
                t.execute()
        }
    }
}
