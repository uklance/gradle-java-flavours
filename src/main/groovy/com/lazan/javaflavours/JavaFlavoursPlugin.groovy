package com.lazan.javaflavours

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.bundling.*

class JavaFlavoursPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.with {
			apply plugin: 'java'
			extensions.add('javaFlavours', new JavaFlavoursExtension(project))
		}
	}
}