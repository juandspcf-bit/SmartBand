package com.misawabus.project.heartRate.fragments.summaryFragments.utils

import com.misawabus.project.heartRate.R

class UtilsSummaryFragments {

    companion object{

        val mapCategories = mapOf<String, Int>(
            "Normal" to R.drawable.speedometer_vector_pure_normal,
            "Elevated" to R.drawable.speedometer_vector_pure_elevated,
            "High Blood Pressure \n (Hypertension) Stage I" to R.drawable.speedometer_vector_pure_high_stage1,
            "High Blood Pressure \n(Hypertension) Stage 2" to R.drawable.speedometer_vector_pure_high_stage2,
            "Hypertensive Crisis" to R.drawable.speedometer_vector_pure_hypertensive_crisis,
            "no category" to R.drawable.archive
        )

        fun  getBPCategory(valueHp: Double?, valueLp:Double?):String{
            val notNullValueHp:Double = valueHp ?: 0.0
            val notNullValueLp:Double = valueLp ?: 0.0
            return when {
                ( (0 < notNullValueHp && notNullValueHp < 120) && (0 < notNullValueLp && notNullValueLp < 80)) -> "Normal"
                ((notNullValueHp >= 120 && notNullValueHp < 130) && (0 < notNullValueLp && notNullValueLp < 80)) -> "Elevated"
                (notNullValueHp in 130.0..139.0 || (notNullValueLp in 80.0..89.0)) -> "High Blood Pressure \n (Hypertension) Stage I"
                (notNullValueHp >= 180 && notNullValueLp >= 120) -> "Hypertensive Crisis"
                (notNullValueHp >= 140 || notNullValueLp >= 90) -> "High Blood Pressure \n(Hypertension) Stage 2"

                else -> {
                    "no category"
                }
            }

        }
    }




}