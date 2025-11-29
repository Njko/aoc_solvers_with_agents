plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":agents:intent"))
    implementation(project(":agents:solve-arith"))
    implementation(libs.koog)
    implementation(libs.slf4jSimple)
}
