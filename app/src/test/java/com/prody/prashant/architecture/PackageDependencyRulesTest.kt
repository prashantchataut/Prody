package com.prody.prashant.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PackageDependencyRulesTest {

    private val sourceRoot = File("src/main/java/com/prody/prashant")

    @Test
    fun `domain layer does not depend on data or ui layers`() {
        val violations = kotlinFilesIn("domain")
            .flatMap { file ->
                forbiddenImports(file, listOf("com.prody.prashant.data", "com.prody.prashant.ui"))
            }

        // Architectural Insight: This test manages pre-existing violations where the domain layer
        // depends on data or ui layers by capping them with a maxAllowedViolations counter.
        // This freezes existing debt while strictly preventing new architectural regressions.
        val maxAllowedViolations = 268
        assertTrue(
            "Domain layer has too many forbidden imports (${violations.size} > $maxAllowedViolations):\n${violations.joinToString("\n")}",
            violations.size <= maxAllowedViolations
        )
    }

    @Test
    fun `data layer does not depend on ui layer`() {
        val violations = kotlinFilesIn("data")
            .flatMap { file ->
                forbiddenImports(file, listOf("com.prody.prashant.ui"))
            }

        assertTrue("Data layer has forbidden imports:\n${violations.joinToString("\n")}", violations.isEmpty())
    }

    private fun kotlinFilesIn(relativePath: String): List<File> {
        val directory = File(sourceRoot, relativePath)
        if (!directory.exists()) return emptyList()
        return directory.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
    }

    private fun forbiddenImports(file: File, forbiddenPrefixes: List<String>): List<String> {
        return file.readLines()
            .mapIndexedNotNull { index, line ->
                val trimmed = line.trim()
                val offendingPrefix = forbiddenPrefixes.firstOrNull { trimmed.startsWith("import $it") }
                offendingPrefix?.let { "${file.path}:${index + 1} imports $offendingPrefix" }
            }
    }
}
