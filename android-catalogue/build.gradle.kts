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

/**
 * Regenerate PrismaTokens.kt by running Style Dictionary in ../design-system.
 * Soft-fails when npm is unavailable — generated tokens are committed to the
 * repo, so the build can proceed against the existing snapshot. Requiring a
 * Node toolchain to merely open the project would be hostile.
 */
tasks.register<Exec>("generateTokens") {
    group = "prisma"
    description = "Regenerate PrismaTokens.kt from design-system tokens via npm."
    workingDir = file("$rootDir/../design-system")
    // Use a shell so user's PATH (homebrew, nvm, etc.) is sourced reliably.
    commandLine("sh", "-c", "command -v npm >/dev/null 2>&1 && npm run build-tokens || (echo '[generateTokens] npm not found — using committed tokens snapshot.' && exit 0)")
    inputs.dir("$rootDir/../design-system/tokens")
    inputs.dir("$rootDir/../design-system/scripts")
    outputs.file("$rootDir/core-ui/src/main/java/xyz/ksharma/prisma/tokens/PrismaTokens.kt")
    isIgnoreExitValue = false
}
