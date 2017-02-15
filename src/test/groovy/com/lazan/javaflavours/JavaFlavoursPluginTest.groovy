package com.lazan.javaflavours

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

import spock.lang.Specification

class JavaFlavoursPluginTest extends Specification {

	@Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
	String classpathString
	
	def setup() {
		URL classpathUrl = getResourceUrl("testkit-classpath.txt")
		List<File> classpathFiles = classpathUrl.readLines().collect { new File(it) }
		
		classpathString = classpathFiles
			.collect { it.absolutePath.replace('\\', '/') } // escape backslashes in Windows paths
			.collect { "'$it'" }
			.join(", ")
			
		writeFile('gradle.properties', getResourceUrl("testkit-gradle.properties").text)
	}

	URL getResourceUrl(String path) {
		URL url = getClass().classLoader.getResource(path)
		if (url == null) throw new RuntimeException("No such resource $path")
		return url
	}
	
	void writeFile(String path, String text) {
		File file = new File(testProjectDir.root, path)
		file.parentFile.mkdirs()
		file.text = text
	}
	
	void assertZipEntries(String zipPath, List<String> expectedEntries) {
		File zipFile = new File(testProjectDir.root, zipPath)
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))
		Set<String> actualEntries = [] as Set
		ZipEntry entry
		while ((entry = zipIn.getNextEntry()) != null) {
			if (!entry.isDirectory()) {
				actualEntries << entry.name
			}
		}
		assertEquals(expectedEntries as Set, actualEntries)
	}
	
	def "Test two flavours compile, test and jar"() {
		given:
		writeFile("settings.gradle", "rootProject.name = 'test-project'")
		writeFile("build.gradle", """
			buildscript {
				dependencies {
					classpath files($classpathString)
				}
			}
			version = '1.0-SNAPSHOT'
			repositories {
				mavenCentral()
			}
			apply plugin: 'com.lazan.javaflavours'
			javaFlavours {
				flavour 'red'
				flavour 'blue'
            }
			dependencies {
				testCompile 'junit:junit:4.12'
			}
			tasks.withType(Test) {
			    testLogging.showStandardStreams = true
			}
		""")
		['main', 'red', 'blue'].each { String flavour ->
			writeFile("src/$flavour/resources/${flavour}.txt", flavour)
			writeFile("src/$flavour/java/foo/${flavour.capitalize()}.java", """
				package foo;
				public interface ${flavour.capitalize()} {}
			""")
			String testDir = 'main' == flavour ? 'test' : "${flavour}Test"
			writeFile("src/$testDir/resources/${flavour}Test.txt", flavour)
			writeFile("src/$testDir/java/foo/${flavour.capitalize()}Test.java", """
				package foo;
				import java.util.*;
				import org.junit.*;
				public class ${flavour.capitalize()}Test {

					@Test
					public void ${flavour}Test() {
						String[] files = { 
							"foo/Main.class", "main.txt", "foo/MainTest.class", "mainTest.txt",
							"foo/Red.class", "red.txt", "foo/RedTest.class", "redTest.txt", 
							"foo/Blue.class", "blue.txt", "foo/BlueTest.class", "blueTest.txt" 
						};
						List<String> found = new ArrayList<>();
						List<String> notFound = new ArrayList<>();
						for (String file : files) {
							if (getClass().getClassLoader().getResource(file) != null) {
								found.add(file);
							} else {
								notFound.add(file);
							}
						}
						System.out.println(String.format("class=%s, found=%s, notFound=%s", getClass().getName(), found, notFound));
					}
				}
			""")
		}
		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('build', '--stacktrace')
			.build()

		then:
		result.task(":build").outcome == TaskOutcome.SUCCESS
		
		result.output.contains('class=foo.MainTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt], notFound=[foo/Red.class, red.txt, foo/RedTest.class, redTest.txt, foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt]')
		result.output.contains('class=foo.RedTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt, foo/Red.class, red.txt, foo/RedTest.class, redTest.txt], notFound=[foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt]')
		result.output.contains('class=foo.BlueTest, found=[foo/Main.class, main.txt, foo/MainTest.class, mainTest.txt, foo/Blue.class, blue.txt, foo/BlueTest.class, blueTest.txt], notFound=[foo/Red.class, red.txt, foo/RedTest.class, redTest.txt]')
		
		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt'])
		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT-red.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt', 'foo/Red.class', 'red.txt'])
		assertZipEntries("build/libs/test-project-1.0-SNAPSHOT-blue.jar", ['META-INF/MANIFEST.MF', 'foo/Main.class', 'main.txt', 'foo/Blue.class', 'blue.txt'])
	}
}