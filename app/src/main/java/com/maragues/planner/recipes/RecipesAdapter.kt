package com.maragues.planner.recipes

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.maragues.planner.common.inflate
import com.maragues.planner.common.loadUrl
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.RecipesAdapter.RecipeViewHolder
import com.maragues.planner_kotlin.R
import kotlinx.android.synthetic.main.item_recipe.view.recipeImage
import kotlinx.android.synthetic.main.item_recipe.view.recipeTitle

/**
 * Created by miguelaragues on 6/1/18.
 */
class RecipesAdapter(val items: List<Recipe>, val listener: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder =
            RecipeViewHolder(parent.inflate(R.layout.item_recipe))

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.recipeTitle
        val image: ImageView = itemView.recipeImage

        fun bind(recipe: Recipe, listener: (Recipe) -> Unit) = with(itemView) {
            title.text = recipe.title
            image.loadUrl(recipe.screenshot)

            setOnClickListener { listener(recipe) }
        }
    }
}