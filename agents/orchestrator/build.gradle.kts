plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":agents:intent"))
    implementation(project(":agents:solve-arith"))
    implementation(project(":agents:io"))
    implementation(project(":agents:parsing"))
    implementation(project(":agents:verify"))
    implementation(project(":agents:solve-grid"))
    implementation(project(":agents:solve-graph"))
    implementation(libs.koog)
    implementation(libs.slf4jSimple)
}
