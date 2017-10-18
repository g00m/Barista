package com.schibsted.spain.barista

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import com.schibsted.spain.barista.custom.DisplayedMatchers.displayedAnd
import com.schibsted.spain.barista.custom.NestedEnabledScrollToAction.nestedScrollToAction
import com.schibsted.spain.barista.internal.failurehandler.SpyFailureHandler
import com.schibsted.spain.barista.internal.failurehandler.description
import com.schibsted.spain.barista.internal.resourceMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf

object BaristaClickActions {

    @JvmStatic
    fun clickBack() {
        pressBack()
    }

    @JvmStatic
    fun click(resId: Int) {
        clickOn(resId.resourceMatcher())
    }

    @JvmStatic
    fun click(text: String) {
        clickOn(withText(text))
    }

    private fun clickOn(viewMatcher: Matcher<View>) {
        val spyHandler = SpyFailureHandler()
        try {
            try {
                clickDisplayedView(viewMatcher, spyHandler)
            } catch (fistError: RuntimeException) {
                try {
                    scrollAndClickView(viewMatcher, spyHandler)
                } catch (secondError: RuntimeException) {
                    scrollAndClickDisplayedView(viewMatcher, spyHandler)
                }
            }
        } catch (fatalError: RuntimeException) {
            spyHandler.resendFirstError("Could not click view ${viewMatcher.description()}")
        }
    }

    private fun scrollAndClickView(viewMatcher: Matcher<View>, handler: SpyFailureHandler) {
        onView(viewMatcher).withFailureHandler(handler).perform(nestedScrollToAction(), click())
    }

    private fun scrollAndClickDisplayedView(viewMatcher: Matcher<View>, failureHandler: SpyFailureHandler) {
        onView(allOf(
                viewMatcher,
                isDescendantOfA(allOf(
                        isDisplayed(),
                        anyOf(
                                isAssignableFrom(ScrollView::class.java),
                                isAssignableFrom(HorizontalScrollView::class.java),
                                isAssignableFrom(ListView::class.java),
                                isAssignableFrom(NestedScrollView::class.java)
                        )
                ))
        ))
                .withFailureHandler(failureHandler)
                .perform(scrollTo(), click())
    }

    private fun clickDisplayedView(viewMatcher: Matcher<View>, failureHandler: SpyFailureHandler) {
        onView(displayedAnd(viewMatcher))
                .withFailureHandler(failureHandler)
                .perform(click())
    }
}