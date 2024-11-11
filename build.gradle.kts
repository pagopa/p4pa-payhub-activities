plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	`java-library`
	`maven-publish`
}

group = "it.gov.pagopa.payhub"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


repositories {
	mavenCentral()
}


tasks.withType<Test> {
	useJUnitPlatform()
}

apply(plugin = "maven-publish")

val janinoVersion = "3.1.12"
val wiremockVersion = "3.5.4"
val snakeYamlVersion = "2.0"
val hibernateValidatorVersion = "8.0.1.Final"


dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

	// Security fixes
	implementation("org.yaml:snakeyaml:$snakeYamlVersion")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.projectlombok:lombok")
	testImplementation ("org.wiremock:wiremock-standalone:$wiremockVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

}


val projectInfo = mapOf(
		"artifactId" to project.name,
		"version" to project.version
)

tasks {
	val processResources by getting(ProcessResources::class) {
		filesMatching("**/application.yml") {
			expand(projectInfo)
		}
	}
}

configurations {
	compileClasspath {
		resolutionStrategy.activateDependencyLocking()
	}
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

publishing {
	publications {
		create<MavenPublication>("github") {
			from(components["java"])

			groupId = project.group.toString()
			artifactId = project.name
			version = project.version.toString()
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://pkgs.dev.azure.com/pagopaspa/_packaging/pagopaspa/maven/v1")
			credentials {
				username = System.getenv("USERNAME")
				password = System.getenv("TOKEN")
			}
		}
	}
}
