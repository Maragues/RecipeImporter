package com.maragues.planner.recipes

import android.content.ClipData
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.maragues.planner.common.inflate
import com.maragues.planner.common.loadUrl
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.RecipesAdapter.RecipeViewHolder
import com.maragues.planner_kotlin.R
import kotlinx.android.synthetic.main.item_recipe.view.dragHandle
import kotlinx.android.synthetic.main.item_recipe.view.recipeImage
import kotlinx.android.synthetic.main.item_recipe.view.recipeTitle

/**
 * Created by miguelaragues on 6/1/18.
 */
internal class RecipesAdapter(val items: List<Recipe>, val listener: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecipeViewHolder(parent.inflate(R.layout.item_recipe))

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    internal class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.recipeTitle
        val imageView: ImageView = itemView.recipeImage

        private val dragListener = DragTouchListener()

        fun bind(recipe: Recipe, listener: (Recipe) -> Unit) = with(itemView) {
            dragListener.recipeId = recipe.id!!
            dragHandle.setOnTouchListener(dragListener)

            title.text = recipe.title
            imageView.loadUrl(recipe.screenshot)

            title.setOnClickListener { listener(recipe) }
        }

        inner class DragTouchListener(var recipeId: Long = 0) : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    ACTION_DOWN -> {
                        val clipData = ClipData.newPlainText("recipeId", recipeId.toString())

                        val shadow = View.DragShadowBuilder(imageView)

                        if (VERSION.SDK_INT >= VERSION_CODES.N) {
                            v.startDragAndDrop(clipData, shadow, recipeId, 0)
                        } else {
                            @Suppress("DEPRECATION")
                            v.startDrag(clipData, shadow, recipeId, 0)
                        }

                        return true
                    }
                }

                return false
            }

        }
    }


}