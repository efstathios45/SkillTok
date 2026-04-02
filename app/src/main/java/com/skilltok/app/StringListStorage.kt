package com.skilltok.app

private const val SEP = '\u0001'

fun List<String>.toStorageField(): String = joinToString(SEP.toString())

fun String.fromStorageField(): List<String> =
    if (isBlank()) emptyList() else split(SEP).map { it.trim() }.filter { it.isNotEmpty() }
