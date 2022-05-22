package com.example.zooapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PersistenceTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void persistenceTest() {

        // Initialize three animal in the plan list
        {
            ViewInteraction actionMenuItemView = onView(
                    allOf(withId(R.id.actions_search), withContentDescription("Search"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(androidx.appcompat.R.id.action_bar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView.perform(click());

            ViewInteraction appCompatImageButton = onView(
                    allOf(withContentDescription("Navigate up"),
                            childAtPosition(
                                    allOf(withId(androidx.appcompat.R.id.action_bar),
                                            childAtPosition(
                                                    withId(androidx.appcompat.R.id.action_bar_container),
                                                    0)),
                                    1),
                            isDisplayed()));
            appCompatImageButton.perform(click());

            ViewInteraction actionMenuItemView2 = onView(
                    allOf(withId(R.id.actions_search), withContentDescription("Search"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(androidx.appcompat.R.id.action_bar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView2.perform(click());

            ViewInteraction recyclerView = onView(
                    allOf(withId(R.id.animalListView),
                            childAtPosition(
                                    withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                    0)));
            recyclerView.perform(actionOnItemAtPosition(3, click()));

            ViewInteraction actionMenuItemView3 = onView(
                    allOf(withId(R.id.actions_search), withContentDescription("Search"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(androidx.appcompat.R.id.action_bar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView3.perform(click());

            ViewInteraction appCompatImageView = onView(
                    allOf(withClassName(is("androidx.appcompat.widget.AppCompatImageView")), withContentDescription("Search"),
                            childAtPosition(
                                    allOf(withClassName(is("android.widget.LinearLayout")),
                                            childAtPosition(
                                                    withId(R.id.searchAnimalBar),
                                                    0)),
                                    1),
                            isDisplayed()));
            appCompatImageView.perform(click());

            ViewInteraction searchAutoComplete = onView(
                    allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                            childAtPosition(
                                    allOf(withClassName(is("android.widget.LinearLayout")),
                                            childAtPosition(
                                                    withClassName(is("android.widget.LinearLayout")),
                                                    1)),
                                    0),
                            isDisplayed()));
            searchAutoComplete.perform(replaceText("fo"), closeSoftKeyboard());

            ViewInteraction recyclerView2 = onView(
                    allOf(withId(R.id.animalListView),
                            childAtPosition(
                                    withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                    0)));
            recyclerView2.perform(actionOnItemAtPosition(0, click()));

            ViewInteraction actionMenuItemView4 = onView(
                    allOf(withId(R.id.actions_search), withContentDescription("Search"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(androidx.appcompat.R.id.action_bar),
                                            1),
                                    0),
                            isDisplayed()));
            actionMenuItemView4.perform(click());

            ViewInteraction recyclerView3 = onView(
                    allOf(withId(R.id.animalListView),
                            childAtPosition(
                                    withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                    0)));
            recyclerView3.perform(actionOnItemAtPosition(4, click()));

            ViewInteraction textView = onView(
                    allOf(withId(R.id.added_counter), withText("(3)"),
                            withParent(withParent(withId(android.R.id.content))),
                            isDisplayed()));
            textView.check(matches(withText("(3)")));

            ViewInteraction textView2 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Gorillas"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView2.check(matches(withText("Gorillas")));

            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Lions"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView3.check(matches(withText("Lions")));

            ViewInteraction textView4 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Arctic Foxes"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView4.check(matches(withText("Arctic Foxes")));
        }

        // Close and relaunch the app
        {
            pressBackUnconditionally();
            mActivityTestRule.finishActivity();
            mActivityTestRule.launchActivity(new Intent());
        }

        // Check if the plan list remains the same after closing the app
        {
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.added_counter), withText("(3)"),
                            withParent(withParent(withId(android.R.id.content))),
                            isDisplayed()));
            textView5.check(matches(withText("(3)")));

            ViewInteraction textView6 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Gorillas"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView6.check(matches(withText("Gorillas")));

            ViewInteraction textView7 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Lions"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView7.check(matches(withText("Lions")));

            ViewInteraction textView8 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Arctic Foxes"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView8.check(matches(withText("Arctic Foxes")));
        }


    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
