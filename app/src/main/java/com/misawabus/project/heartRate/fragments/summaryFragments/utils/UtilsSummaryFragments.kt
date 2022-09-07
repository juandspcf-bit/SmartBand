package com.misawabus.project.heartRate.fragments.summaryFragments.utils

import com.misawabus.project.heartRate.R

class UtilsSummaryFragments {

    companion object{

        val mapCategories = mapOf<String, Int>(
            "Normal" to R.drawable.speedometer0,
            "Elevated" to R.drawable.speedometer1,
            "High Blood Pressure \n (Hypertension) Stage I" to R.drawable.speedometer2,
            "High Blood Pressure \n(Hypertension) Stage 2" to R.drawable.speedometer2,
            "Hypertensive Crisis" to R.drawable.speedometer2,
            "no category" to R.drawable.archive
        )

        fun  getBPCategory(valueHp: Double?, valueLp:Double?):String{
            val notNullValueHp:Double = valueHp ?: 0.0
            val notNullValueLp:Double = valueLp ?: 0.0
            return when {
                ( (0 < notNullValueHp && notNullValueHp <= 120) && (0 < notNullValueHp && notNullValueLp <= 80)) -> "Normal"
                ((notNullValueHp > 120 && notNullValueHp < 130) && (0 < notNullValueHp && notNullValueLp <= 80)) -> "Elevated"
                (notNullValueHp in 130.0..139.0 || (80 < notNullValueHp && notNullValueLp <= 89)) -> "High Blood Pressure \n (Hypertension) Stage I"
                (notNullValueHp > 139 || notNullValueLp > 89) -> "High Blood Pressure \n(Hypertension) Stage 2"
                (notNullValueHp >= 180 && notNullValueLp >= 120) -> "Hypertensive Crisis"
                else -> {
                    "no category"
                }
            }

        }
    }




}