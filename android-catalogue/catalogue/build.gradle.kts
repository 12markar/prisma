plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "xyz.ksharma.prisma.catalogue"
    compileSdk = 36

    defaultConfig {
        applicationId = "xyz.ksharma.prisma.catalogue"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
    buildFeatures { compose = true }
}

// Compose Compiler stability + recomposition reports.
// Run: `./gradlew :catalogue:assembleDebug -PcomposeCompilerReports=true`
// Output: catalogue/build/compose_compiler/ — inspect *-classes.txt for stability.
val composeCompilerReports = providers.gradleProperty("composeCompilerReports").orNull?.toBoolean() ?: false
if (composeCompilerReports) {
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":components"))

    // Material icons (catalogue chrome only — TODO: replace with design-system-supplied set)
    implementation("androidx.compose.material:material-icons-extended")

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive)
    implementation(libs.compose.material3.adaptive.layout)
    implementation(libs.compose.material3.adaptive.navigation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.collections.immutable)

    // Catalogue browser auto-generation
    implementation(libs.showkase)
    ksp(libs.showkase.processor)

    // Server-driven UI demo — JSON wire format, WebSocket transport, remote images
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.coil.compose)

    debugImplementation(libs.compose.ui.tooling)
    // LeakCanary — debug-only memory leak detector. Ships nothing in release.
    debugImplementation(libs.leakcanary)

    testImplementation(libs.junit)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
