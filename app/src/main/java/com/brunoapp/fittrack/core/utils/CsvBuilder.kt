package com.brunoapp.fittrack.core.utils

/** RFC-4180-ish CSV building. Unit-tested. */
object CsvBuilder {

    fun escape(field: String): String =
        if (field.contains(',') || field.contains('"') || field.contains('\n')) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else field

    fun buildRow(fields: List<String>): String =
        fields.joinToString(",") { escape(it) }

    fun build(header: List<String>, rows: List<List<String>>): String =
        buildString {
            appendLine(buildRow(header))
            rows.forEach { appendLine(buildRow(it)) }
        }
}
