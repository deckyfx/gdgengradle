package com.github.deckyfx.gdgengradle

import org.gradle.api.Project
import org.gradle.api.provider.Property;

class GenerateTaskExtension {
    final Property<Boolean> force

    GenerateTaskExtension(Project project) {
        force = project.objects.property(Boolean)
        force.set(false)
    }
}
