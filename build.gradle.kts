buildscript {
    repositories {
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
    }
}

group = "copatych"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
        mavenCentral()
        jcenter()
    }
}