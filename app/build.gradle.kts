plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":utils"))
    // Orchestrator agent module
    implementation(project(":agents:orchestrator"))
    // Koog Agents library (required by the Getting Started guide)
    implementation(libs.koog)
    implementation(libs.slf4jSimple)
    implementation(libs.ktorClientCio)
}

application {
    // Define the Fully Qualified Name for the application main class
    // Attention: Kotlin génère une classe <FileName>Kt. Le fichier s'appelle `AppKt.kt`,
    // donc la classe d'entrée est `AppKtKt`.
    mainClass = "fr.nicolaslinard.koog.kmp.app.AppKtKt"
    applicationDefaultJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8"
    )
}
