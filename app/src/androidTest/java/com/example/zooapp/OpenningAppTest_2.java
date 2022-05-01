package com.example.zooapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenningAppTest_2 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openningAppTest_2() {

        ViewInteraction textView = onView(
                allOf(withText("Zoo Seeker"),
                        withParent(allOf(withId(androidx.appcompat.R.id.action_bar),
                                withParent(withId(androidx.appcompat.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Zoo Seeker")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.actions_search), withContentDescription("Search"),
                        withParent(withParent(withId(androidx.appcompat.R.id.action_bar))),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.added_text), withText("PLANNED ANIMALS"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView4.check(matches(withText("PLANNED ANIMALS")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.added_counter), withText("(0)"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView5.check(matches(withText("(0)")));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.planned_animals),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.plan_button), withText("PLAN"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

    }
}
