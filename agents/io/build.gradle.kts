plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(project(":utils"))
    implementation(libs.koog)
}
