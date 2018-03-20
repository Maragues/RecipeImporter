package com.maragues.planner.interactors

import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.repositories.RecipeTagRepository
import com.maragues.planner.persistence.repositories.TagRepository
import io.reactivex.Completable
import io.reactivex.CompletableSource
import javax.inject.Inject

/**
 * Created by miguelaragues on 10/3/18.
 */
class TagInteractor
@Inject constructor(val tagRepository: TagRepository, val recipeTagRepository: RecipeTagRepository) {
    fun storeOrUpdate(recipeId: Long, tags: Set<Tag>): CompletableSource {
        return Completable.fromAction({
            tagRepository.insert(tags.toList())

            recipeTagRepository.replaceTags(recipeId, tags)
        })
    }
}