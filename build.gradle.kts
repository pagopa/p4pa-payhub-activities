import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.0.1.5171"
	`java-library`
	`maven-publish`
	jacoco
	id("com.intershop.gradle.jaxb") version "7.0.1"
	id("org.openapi.generator") version "7.10.0"
}

group = "it.gov.pagopa.payhub"
version = rootProject.file("version").readText().trim()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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
val hibernateValidatorVersion = "8.0.2.Final"
val commonsCompressVersion = "1.27.1"
val commonsLang3Version = "3.17.0"
val commonsTextVersion = "1.13.0"
val jacksonModuleVersion = "2.18.2"
val activationVersion = "2.1.3"
val jaxbVersion = "4.0.5"
val jaxbApiVersion = "4.0.2"
val jsoupVersion = "1.18.3"
val openApiToolsVersion = "0.2.6"
val temporalVersion = "1.27.0"
val protobufJavaVersion = "3.25.5"
val openCsvVersion = "5.9"
val mapStructVersion = "1.6.3"
val podamVersion = "8.0.2.RELEASE"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")

	//apache commons
	implementation("org.apache.commons:commons-compress:$commonsCompressVersion")
	implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
	implementation("org.apache.commons:commons-text:$commonsTextVersion")

	// Security fixes
	implementation("com.google.protobuf:protobuf-java:$protobufJavaVersion")

	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jacksonModuleVersion")

 	//temporal
	implementation("io.temporal:temporal-sdk:$temporalVersion"){
		exclude(group = "com.google.protobuf", module = "protobuf-java")
	}

    //mail
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.retry:spring-retry")
	implementation("org.jsoup:jsoup:$jsoupVersion")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.projectlombok:lombok")
	testImplementation("uk.co.jemos.podam:podam:$podamVersion")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	/**
	 * Mapstruct
	 * https://mapstruct.org/
	 * mapstruct dependencies must always be placed after the lombok dependency
	 * or the generated mappers will return an empty object
	 **/
	implementation("org.mapstruct:mapstruct:$mapStructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

	//openCsv
	implementation("com.opencsv:opencsv:$openCsvVersion")

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
		register("Opi14TreasuryFlow") {
			extension = true
			args = listOf("-xmlschema","-Xsimplify")
			outputDir = file("$projectDir/build/generated/jaxb/java")
			schema = file("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xsd")
			bindings = layout.files("src/main/resources/xsd/OPI_GIORNALE_DI_CASSA_V_1_4.xjb")
		}
		register("Opi161TreasuryFlow") {
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
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

tasks.register<Jar>("sourcesJar") {
	group = "build"
	description = "Assembles a JAR archive containing the main source code."

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	from(sourceSets["main"].allSource)
	inputs.dir("$projectDir/build/generated/src/main/java")
	archiveClassifier.set("sources")

	dependsOn("dependenciesBuild")
}

tasks.register<Jar>("javadocJar") {
	group = "build"
	description = "Assembles a JAR archive containing the Javadoc."

	from(tasks.javadoc.get().destinationDir)
	archiveClassifier.set("javadoc")

	dependsOn(tasks.javadoc)
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
	dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
	group = "AutomaticallyGeneratedCode"
	description = "grouping all together automatically generate code tasks"

	dependsOn(
		"jaxb",
		"openApiGenerateP4PAAUTH",
		"openApiGenerateIONOTIFICATION",
		"openApiGenerateORGANIZATION",
		"openApiGenerateDEBTPOSITIONS",
		"openApiGenerateCLASSIFICATION",
		"openApiGeneratePAGOPAPAYMENTS",
		"openApiGeneratePROCESSEXECUTIONS"
	)
}

tasks.register<GenerateTask>("openApiGenerateP4PAAUTH") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-auth/refs/heads/develop/openapi/p4pa-auth.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.auth.generated")
	apiPackage.set("it.gov.pagopa.pu.auth.controller.generated")
	modelPackage.set("it.gov.pagopa.pu.auth.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	)
	)
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGenerateIONOTIFICATION") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-io-notification/refs/heads/develop/openapi/p4pa-io-notification.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.ionotification.generated")
	apiPackage.set("it.gov.pagopa.pu.ionotification.client.generated")
	modelPackage.set("it.gov.pagopa.pu.ionotification.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGenerateORGANIZATION") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-organization/refs/heads/develop/openapi/generated.openapi.json")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.organization.generated")
	apiPackage.set("it.gov.pagopa.pu.organization.client.generated")
	modelPackage.set("it.gov.pagopa.pu.organization.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGenerateDEBTPOSITIONS") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-debt-positions/refs/heads/develop/openapi/generated.openapi.json")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.debtposition.generated")
	apiPackage.set("it.gov.pagopa.pu.debtposition.client.generated")
	modelPackage.set("it.gov.pagopa.pu.debtposition.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGenerateCLASSIFICATION") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-classification/refs/heads/develop/openapi/generated.openapi.json")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.classification.generated")
	apiPackage.set("it.gov.pagopa.pu.classification.client.generated")
	modelPackage.set("it.gov.pagopa.pu.classification.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGeneratePAGOPAPAYMENTS") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-pagopa-payments/refs/heads/develop/openapi/p4pa-pagopa-payments.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.pagopapayments.generated")
	apiPackage.set("it.gov.pagopa.pu.pagopapayments.client.generated")
	modelPackage.set("it.gov.pagopa.pu.pagopapayments.dto.generated")
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGeneratePROCESSEXECUTIONS") {
	group = "AutomaticallyGeneratedCode"
	description = "openapi"

	generatorName.set("java")
	remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-process-executions/refs/heads/develop/openapi/generated.openapi.json")
	outputDir.set("$projectDir/build/generated")
	invokerPackage.set("it.gov.pagopa.pu.processexecutions.generated")
	apiPackage.set("it.gov.pagopa.pu.processexecutions.client.generated")
	modelPackage.set("it.gov.pagopa.pu.processexecutions.dto.generated")
	typeMappings.set(mapOf(
		"LocalDateTime" to "java.time.LocalDateTime"
	))
	configOptions.set(mapOf(
		"swaggerAnnotations" to "false",
		"openApiNullable" to "false",
		"dateLibrary" to "java8",
		"serializableModel" to "true",
		"useSpringBoot3" to "true",
		"useJakartaEe" to "true",
		"serializationLibrary" to "jackson",
		"generateSupportingFiles" to "true",
		"generateConstructorWithAllArgs" to "true",
		"generatedConstructorWithRequiredArgs" to "true",
		"additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
	))
	library.set("resttemplate")
}
