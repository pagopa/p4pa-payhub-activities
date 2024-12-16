import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.sonarqube") version "5.0.0.4638"
	`java-library`
	`maven-publish`
	jacoco
	id("com.intershop.gradle.jaxb") version "7.0.0"
	id("org.openapi.generator") version "7.9.0"
	id("org.ajoberstar.grgit") version "5.3.0"
}

group = "it.gov.pagopa.payhub"
version = rootProject.file("version").readText().trim()

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
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
	}
}

apply(plugin = "maven-publish")

val janinoVersion = "3.1.12"
val wiremockVersion = "3.5.4"
val snakeYamlVersion = "2.0"
val hibernateValidatorVersion = "8.0.1.Final"
val commonsCompressVersion = "1.27.1"
val commonsLang3Version = "3.17.0"
val commonsTextVersion = "1.12.0"
val jacksonModuleVersion = "2.18.1"
val activationVersion = "2.1.3"
val jaxbVersion = "4.0.5"
val jaxbApiVersion = "4.0.2"
val openApiToolsVersion = "0.2.6"


dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

	//apache commons
	implementation("org.apache.commons:commons-compress:$commonsCompressVersion")
	implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
	implementation("org.apache.commons:commons-text:$commonsTextVersion")

	// Security fixes
	implementation("org.yaml:snakeyaml:$snakeYamlVersion")

	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jacksonModuleVersion")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.projectlombok:lombok")
	testImplementation ("org.wiremock:wiremock-standalone:$wiremockVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	//jaxb
	runtimeOnly("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion")
	implementation("com.sun.xml.bind:jaxb-xjc:$jaxbVersion")
	implementation("com.sun.xml.bind:jaxb-jxc:$jaxbVersion")
	implementation("com.sun.xml.bind:jaxb-core:$jaxbVersion")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:$jaxbApiVersion")
	implementation("jakarta.activation:jakarta.activation-api:$activationVersion")

	// openApi
	implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")

    jaxbext("com.github.jaxb-xew-plugin:jaxb-xew-plugin:2.1")
    jaxbext("org.jvnet.jaxb:jaxb-plugins:4.0.0")
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

jaxb {
	javaGen {
		register("PaymentsReport") {
			extension = true
			args = listOf("-xmlschema")
			outputDir = file("$projectDir/build/generated/jaxb/java")
			schema = file("src/main/resources/xsd/FlussoRiversamento.xsd")
			bindings = layout.files("src/main/resources/xsd/FlussoRiversamento.xjb")
		}
		register("Opi14TresauryFlow") {
			extension = true
			args = listOf("-xmlschema","-Xsimplify")
			outputDir = file("$projectDir/build/generated/jaxb/java")
			schema = file("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd")
			bindings = layout.files("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xjb")
		}
		register("Opi161TresauryFlow") {
			extension = true
			args = listOf("-xmlschema","-Xsimplify")
			outputDir = file("$projectDir/build/generated/jaxb/java")
			schema = file("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_6_1.xsd")
			bindings = layout.files("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_6_1.xjb")
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
		java.srcDir("$projectDir/build/generated/ionotification/src/main/java")
	}
}

tasks.register<Jar>("sourcesJar") {
	group = "build"
	description = "Assembles a JAR archive containing the main source code."

	from(sourceSets["main"].allSource)
	inputs.dir("$projectDir/build/generated/ionotification/src/main/java")
	dependsOn("openApiGenerateIONOTIFICATION")
	archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
	group = "build"
	description = "Assembles a JAR archive containing the Javadoc."

	dependsOn(tasks.javadoc)
	from(tasks.javadoc.get().destinationDir)
	archiveClassifier.set("javadoc")
}

publishing {
	publications {
		create<MavenPublication>("github") {
			from(components["java"])

			artifact(tasks["sourcesJar"])
			artifact(tasks["javadocJar"])

			groupId = project.group.toString()
			artifactId = project.name
			version = project.version.toString()
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/pagopa/p4pa-payhub-activities")
			credentials {
				username = System.getenv("USERNAME")
				password = System.getenv("TOKEN")
			}
		}
	}
}


tasks.withType<BootJar> {
	enabled = false
}


tasks.compileJava {
	dependsOn("openApiGenerateIONOTIFICATION")
}

var targetEnv = when (grgit.branch.current().name) {
	"uat" -> "uat"
	"main" -> "main"
	else -> "develop"
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateIONOTIFICATION") {
	group = "openapi"
	description = "description"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-io-notification/refs/heads/$targetEnv/openapi/p4pa-io-notification.openapi.yaml")
	outputDir.set("$projectDir/build/generated/ionotification")
	apiPackage.set("it.gov.pagopa.pu.p4paionotification.controller.generated")
	modelPackage.set("it.gov.pagopa.pu.p4paionotification.model.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java17",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true"
	))
	library.set("resttemplate")
}

