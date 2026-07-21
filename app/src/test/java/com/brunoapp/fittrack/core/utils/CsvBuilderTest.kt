package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvBuilderTest {

    @Test
    fun `plain fields are not quoted`() {
        assertEquals("a,b,c", CsvBuilder.buildRow(listOf("a", "b", "c")))
    }

    @Test
    fun `fields with commas are quoted`() {
        assertEquals("\"a,b\",c", CsvBuilder.buildRow(listOf("a,b", "c")))
    }

    @Test
    fun `quotes are doubled`() {
        assertEquals("\"say \"\"hola\"\"\"", CsvBuilder.buildRow(listOf("say \"hola\"")))
    }

    @Test
    fun `newlines force quoting`() {
        assertEquals("\"a\nb\"", CsvBuilder.buildRow(listOf("a\nb")))
    }

    @Test
    fun `build produces header plus rows`() {
        val csv = CsvBuilder.build(
            header = listOf("fecha", "peso_kg"),
            rows = listOf(listOf("2026-07-21", "82.5"))
        )
        assertEquals("fecha,peso_kg\n2026-07-21,82.5\n", csv)
    }
}
