package fr.snoring.anti_snoring.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.activity.AntiSnoringActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AntiSnoringEspressoTest {

    @Rule public GrantPermissionRule recordAudioPermission = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);
    @Rule public GrantPermissionRule readExtStoragePermission = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ControlledActivityTestRule<AntiSnoringActivity> activityRule = new ControlledActivityTestRule<>(AntiSnoringActivity.class);

    private UiDevice mDevice;

    @Test
    public void mainViewIsDisplayed() {
        onView(withId(R.id.main))        
                .check(matches(isDisplayed()));
    }

    @Test
    public void volumeViewIsDisplayed() {
        onView(withId(R.id.volume))        
                .check(matches(isDisplayed()));
    }

    @Test
    public void sonSelectionneViewIsDisplayed() {
        onView(withId(R.id.son_selectionne))        
                .check(matches(isDisplayed()));
    }

    @Test
    public void sleepingViewIsDisplayed() {
        onView(withId(R.id.sleeping))
                .check(matches(isDisplayed()));
    }

    @Test
    public void restartingActivityIsOk() throws Throwable {
        for(int i=0;i<10;i++) {
            activityRule.relaunchActivity();
        }
    }

    @Test
    public void changingSensibilityIsOk() throws InterruptedException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressMenu();
        Thread.sleep(1000);
        onView(withText(R.string.sensibilite))
                .perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.seekbar)).perform(setProgress(10));
        onView(withId(R.id.seekbar)).perform(setProgress(50));
        onView(withId(R.id.cross)).perform(click());
    }

    @Test
    //TODO Improve this one
    public void changerSonMenuOptionIsDisplayed() throws InterruptedException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressMenu();
        Thread.sleep(1000);
        onView(withText(R.string.changer_son))
                .perform(click());
    }

    @Test
    public void quitterMenuOptionIsDisplayed() throws InterruptedException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressMenu();
        Thread.sleep(1000);
        onView(withText(R.string.quitter))
                .perform(click());
    }

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }
}