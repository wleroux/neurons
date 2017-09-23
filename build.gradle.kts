plugins {
  id("org.gradle.kotlin.kotlin-dsl") version "0.11.2"
  id("application")
  id("idea")
}

val kotlinVersion by project
val lwjglVersion by project
configure<ApplicationPluginConvention> {
  mainClassName = "com.neurons.MainKt"
}

repositories {
  mavenCentral()
}

dependencies {
  // LWJGL OpenCL
  compile("org.lwjgl:lwjgl-opencl:$lwjglVersion")
  compile("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")

  // Kotlin
  compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:$kotlinVersion")
  compile("org.jetbrains.kotlin:kotlin-runtime:$kotlinVersion")
  testCompile("junit:junit:4.12")
  testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}
