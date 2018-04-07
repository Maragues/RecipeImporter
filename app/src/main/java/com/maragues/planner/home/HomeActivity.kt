package com.maragues.planner.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import com.maragues.planner.common.BaseActivity
import com.maragues.planner.home.HomeActivity.NavigationAdapter.Companion.PLANNER_POSITION
import com.maragues.planner.home.HomeActivity.NavigationAdapter.Companion.RECIPES_POSITION
import com.maragues.planner.recipes.RecipesListFragment
import com.maragues.planner_kotlin.R
import com.maragues.planner_kotlin.R.id
import com.maragues.planner_kotlin.R.id.homeViewPage
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_home.homeBottomNavigation
import kotlinx.android.synthetic.main.activity_home.homeViewPage
import kotlinx.android.synthetic.main.activity_recipes_list.toolbar
import javax.inject.Inject

class HomeActivity : BaseActivity(), HasSupportFragmentInjector {
    companion object {
        fun createIntentAfterAddingRecipe(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        initNavigation()
    }

    private fun initNavigation() {
        initViewPager()

        (homeBottomNavigation as BottomNavigationView).setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
    }

    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            id.section_recipes -> viewPager.currentItem = RECIPES_POSITION
            id.section_planner -> viewPager.currentItem = PLANNER_POSITION
        }

        return true
    }

    private fun initViewPager() {
        viewPager = homeViewPage

        homeViewPage.adapter = NavigationAdapter(supportFragmentManager)
    }

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }

    private class NavigationAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
        companion object {
            const val SECTIONS = 2
            const val RECIPES_POSITION: Int = 0
            const val PLANNER_POSITION = 1
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                RECIPES_POSITION -> return RecipesListFragment()
                else -> {
                }
            }
            return Fragment()
        }

        override fun getCount() = SECTIONS
    }
}
