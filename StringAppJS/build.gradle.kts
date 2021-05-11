plugins {
    id("org.jetbrains.kotlin.js")
}

group = "copatych"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation(project(":EngineeringCalculatorLib"))
    implementation("org.jetbrains:kotlin-react:17.0.1-pre.148-kotlin-1.4.21")
    implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.148-kotlin-1.4.21")
    implementation(npm("react", "17.0.1"))
    implementation(npm("react-dom", "17.0.1"))

    implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.21")
    implementation(npm("styled-components", "~5.2.1"))
}
