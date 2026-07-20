package com.brunoapp.fittrack.domain.model

import com.brunoapp.fittrack.core.constants.Objective

/** Clean domain model for the user profile. */
data class Profile(
    val name: String = "",
    val heightCm: Double? = null,
    val weightInitialKg: Double? = null,
    val weightGoalKg: Double? = null,
    val objective: Objective = Objective.MAINTAIN,
    val defaultRestSeconds: Int = 120,
    val weeklyCheckDay: Int = 0 // 0 = Monday … 6 = Sunday
)
