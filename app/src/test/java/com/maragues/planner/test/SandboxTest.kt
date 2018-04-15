package com.maragues.planner.test

import com.maragues.planner.persistence.entities.Tag
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.ReplaySubject
import org.junit.Ignore
import org.junit.Test
import timber.log.Timber

/**
 * Created by miguelaragues on 10/3/18.
 */
@Ignore
class SandboxTest : BaseUnitTest() {
    @Test
    fun groupBy() {
        val tagSubject = ReplaySubject.create<Tag>()

        tagSubject
                .scan(setOf(), BiFunction<Set<Tag>, Tag, Set<Tag>> { oldTags, newTag ->
                    val tagSet = mutableSetOf(newTag)

                    tagSet.addAll(oldTags)

                    tagSet
                })
                .subscribe(
                        { Timber.d("Emitting $it") },
                        Throwable::printStackTrace
                )

        tagSubject.onNext(Tag("1"))
        tagSubject.onNext(Tag("2"))
    }

    @Test
fun sublist(){
        val singleList = listOf(1)

        Timber.d("list "+singleList+", sublist "+singleList.subList(1, singleList.size))
    }
}