plugins {
	kotlin("jvm") version "2.1.0"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.10"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation(kotlin("reflect"))
	implementation("org.shypl.tool:tool-logging:1.0.0-SNAPSHOT")
	
	testImplementation(kotlin("test"))
	testImplementation("ch.qos.logback:logback-classic:1.5.16")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
}