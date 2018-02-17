package com.maragues.planner.recipes.model

import com.maragues.planner.recipes.model.MealType.DINNER


/**
 * Created by miguelaragues on 12/2/18.
 */
enum class MealType {
    LUNCH, DINNER
}

class MealTypeComparator : Comparator<MealType> {
    override fun compare(o1: MealType, o2: MealType): Int {
        if (o1 == o2) return 0

        if (o2 == DINNER) return 1

        return -1
    }

}