import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

// Apply Detekt config to every subproject that uses the plugin.
// This single config file under <root>/detekt.yml is the source of truth.
subprojects {
    plugins.withId("io.gitlab.arturbosch.detekt") {
        extensions.configure<DetektExtension> {
            toolVersion = libs.versions.detekt.get()
            config.setFrom(files("${rootProject.rootDir}/detekt.yml"))
            buildUponDefaultConfig = true
            autoCorrect = false
        }
    }
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(files("$rootDir/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = false
}

dependencies {
    detektPlugins(libs.slack.compose.lints)
}

// `npm run build-tokens` — invoked from any module's preBuild via dependsOn(rootProject.tasks.named("generateTokens"))
tasks.register<Exec>("generateTokens") {
    group = "prisma"
    description = "Regenerate PrismaTokens.kt by running Style Dictionary in ../design-system."
    workingDir = file("$rootDir/../design-system")
    commandLine("npm", "run", "build-tokens")
    inputs.dir("$rootDir/../design-system/tokens")
    inputs.dir("$rootDir/../design-system/scripts")
    outputs.file("$rootDir/core-ui/src/main/java/xyz/ksharma/prisma/tokens/PrismaTokens.kt")
}
