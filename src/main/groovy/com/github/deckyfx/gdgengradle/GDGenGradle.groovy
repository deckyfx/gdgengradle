package com.github.deckyfx.gdgengradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.NoConventionMapping

@NoConventionMapping
class GDGenGradle implements Plugin<Project> {
    public final static String TASK_GROUP          = "gdgengradle"

    @Override
    void apply(Project project) {
        def extension = project.extensions.create(TASK_GROUP, GenerateTaskExtension, project)

        project.tasks.create(AbstractGenerator.TASK_NAME,        GenerateModel) {
            force = extension.force
        }
        project.tasks.create(AbstractGenerator.FORCE_TASK_NAME,  GenerateModelForce) {
            force = true
        }

        Set<Task> generateModelTask   = project.getTasksByName(AbstractGenerator.TASK_NAME, false)
        generateModelTask.each {
            task->
                GenerateModel t        = (GenerateModel) task
                t.execute()
        }
    }
}