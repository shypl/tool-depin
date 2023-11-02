plugins {
	kotlin("jvm") version "1.9.20"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "17.2.2"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(17)
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation(kotlin("reflect"))
	implementation("org.shypl.tool:tool-logging:1.0.0-SNAPSHOT")
	
	testImplementation(kotlin("test"))
	testImplementation("ch.qos.logback:logback-classic:1.4.11")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
}