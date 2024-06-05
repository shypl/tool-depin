plugins {
	kotlin("jvm") version "2.0.0"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.8"
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
	testImplementation("ch.qos.logback:logback-classic:1.5.6")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
}