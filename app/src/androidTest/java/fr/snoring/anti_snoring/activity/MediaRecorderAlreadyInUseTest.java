package fr.snoring.anti_snoring.activity;

import android.Manifest;
import android.media.MediaRecorder;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import fr.snoring.anti_snoring.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MediaRecorderAlreadyInUseTest {

    @Rule public GrantPermissionRule recordAudioPermission = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);
    @Rule public GrantPermissionRule readExtStoragePermission = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ControlledActivityTestRule<AntiSnoringActivity> activityRule = new ControlledActivityTestRule<>(AntiSnoringActivity.class);

    private static MediaRecorder MEDIA_RECORDER;

    @BeforeClass
    public static void setupClass() throws IOException {
        MEDIA_RECORDER = new MediaRecorder();
        MEDIA_RECORDER.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        MEDIA_RECORDER.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        MEDIA_RECORDER.setOutputFile("/dev/null");
        MEDIA_RECORDER.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        MEDIA_RECORDER.prepare();
        MEDIA_RECORDER.start();
    }

    @AfterClass
    public static void tearDown(){
        MEDIA_RECORDER.release();
    }

    @Test
    public void mainViewIsDisplayed() {
        onView(withId(R.id.main))        
                .check(matches(isDisplayed()));
    }

}