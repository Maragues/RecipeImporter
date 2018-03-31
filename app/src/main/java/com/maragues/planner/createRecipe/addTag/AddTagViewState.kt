package com.maragues.planner.createRecipe.addTag

import com.maragues.planner.persistence.entities.Tag

/**
 * Created by miguelaragues on 3/3/18.
 */
internal data class AddTagViewState(val filteredTags: List<Tag>, val createEnabled: Boolean)