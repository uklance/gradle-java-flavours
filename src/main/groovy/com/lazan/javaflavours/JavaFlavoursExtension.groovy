package com.lazan.javaflavours

import java.util.Collections
import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.bundling.*

class JavaFlavoursExtension {
	private final List<String> flavours = []
	private final Project project
	FlavourPathResolver javaPathResolver = { String flavour -> "src/$flavour/java" }
	FlavourPathResolver resourcesPathResolver = { String flavour -> "src/$flavour/resources" }
	FlavourPathResolver testJavaPathResolver = { String flavour -> "src/${flavour}Test/java" }
	FlavourPathResolver testResourcesPathResolver = { String flavour -> "src/${flavour}Test/resources" }

	JavaFlavoursExtension(Project project) {
		this.project = project
	}
	
	List<String> getFlavours() {
		return Collections.unmodifiableList(flavours)
	}
	
	void flavour(String flavour) {
		flavours << flavour
		
		project.with {
			SourceSet sourceSet = sourceSets.create(flavour)
			sourceSet.compileClasspath += sourceSets.main.output
			sourceSet.runtimeClasspath += sourceSets.main.output
			sourceSet.java.srcDir { -> javaPathResolver.getPath(flavour) }
			sourceSet.resources.srcDir { -> resourcesPathResolver.getPath(flavour) }
	
			SourceSet testSourceSet = sourceSets.create("${flavour}Test")
			testSourceSet.compileClasspath += (sourceSets.main.output + sourceSets.test.output + sourceSet.output)
			testSourceSet.runtimeClasspath += (sourceSets.main.output + sourceSets.test.output + sourceSet.output)
			testSourceSet.java.srcDir { -> testJavaPathResolver.getPath(flavour) }
			testSourceSet.resources.srcDir { -> testResourcesPathResolver.getPath(flavour) }
	
			['compile', 'compileOnly', 'compileClasspath', 'runtime'].each { String suffix ->
	
				// these configurations were magically created when we added the source sets above
				Configuration config = configurations.getByName("${flavour}${suffix.capitalize()}")
				Configuration testConfig = configurations.getByName("${flavour}Test${suffix.capitalize()}")
	
				config.extendsFrom(configurations.getByName(suffix))
				testConfig.extendsFrom(configurations.getByName("test${suffix.capitalize()}"))
				testConfig.extendsFrom(config)
			}
	
			Task testTask = tasks.create(name: "${flavour}Test", type: Test) {
				group = JavaBasePlugin.VERIFICATION_GROUP
				description = "Runs the tests for ${flavour}."
				testClassesDir = testSourceSet.output.classesDir
				classpath = testSourceSet.runtimeClasspath
			}
			check.dependsOn testTask
	
			Task jarTask = tasks.create(name: "${flavour}Jar", type: Jar) {
				group = BasePlugin.BUILD_GROUP
				description = "Assembles a jar archive containing the $flavour classes combined with the main classes."
				from sourceSet.output
				from sourceSets.main.output
				classifier flavour
			}
	
			artifacts {
				   archives jarTask
			}
			assemble.dependsOn jarTask
		}
	}
}

