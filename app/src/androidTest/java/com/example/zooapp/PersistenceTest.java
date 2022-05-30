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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

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


    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void persistenceTest2() {

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.clear_button), withText("CLEAR"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.main_actions_search), withContentDescription("Search"),
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
                allOf(withId(R.id.main_actions_search), withContentDescription("Search"),
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
                allOf(withId(R.id.main_actions_search), withContentDescription("Search"),
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
        searchAutoComplete.perform(replaceText("bi"), closeSoftKeyboard());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.animalListView),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(3, click()));

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.main_actions_search), withContentDescription("Search"),
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
        recyclerView3.perform(actionOnItemAtPosition(13, click()));

        ViewInteraction textView0 = onView(
                allOf(withId(R.id.added_counter), withText("(3)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView0.check(matches(withText("(3)")));

        ViewInteraction textView = onView(
                allOf(withId(R.id.planned_animal_text), withText("Flamingos"),
                        withParent(withParent(withId(R.id.planned_animals))),
                        isDisplayed()));
        textView.check(matches(withText("Flamingos")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.planned_animal_text), withText("Toucan"),
                        withParent(withParent(withId(R.id.planned_animals))),
                        isDisplayed()));
        textView2.check(matches(withText("Toucan")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.planned_animal_text), withText("Crocodiles"),
                        withParent(withParent(withId(R.id.planned_animals))),
                        isDisplayed()));
        textView3.check(matches(withText("Crocodiles")));



        // Close and relaunch the app
        {
            pressBackUnconditionally();
            mActivityTestRule.finishActivity();
            mActivityTestRule.launchActivity(new Intent());
        }

        // Check if the plan list remains the same after re-opening the app
        {
            ViewInteraction textView00 = onView(
                    allOf(withId(R.id.added_counter), withText("(3)"),
                            withParent(withParent(withId(android.R.id.content))),
                            isDisplayed()));
            textView00.check(matches(withText("(3)")));
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Flamingos"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView5.check(matches(withText("Flamingos")));

            ViewInteraction textView6 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Toucan"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView6.check(matches(withText("Toucan")));

            ViewInteraction textView7 = onView(
                    allOf(withId(R.id.planned_animal_text), withText("Crocodiles"),
                            withParent(withParent(withId(R.id.planned_animals))),
                            isDisplayed()));
            textView7.check(matches(withText("Crocodiles")));

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
