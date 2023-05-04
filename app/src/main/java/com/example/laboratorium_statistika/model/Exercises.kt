package com.example.laboratorium_statistika.model

data class Exercises(
    val questions: List<Questions>,
    var score: Int = 0
)

class Questions(
    val id: Int = 0,
    val question: String,
    val choices: List<Choices>,
    var answerIndex: Int,
    var isCorrect: Boolean = false
)

class Choices(
    val choice: String,
    var isCorrect: Boolean = false,
    var selected: Boolean = false
)