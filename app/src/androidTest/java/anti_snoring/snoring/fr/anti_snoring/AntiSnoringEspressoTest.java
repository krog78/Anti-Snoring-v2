package anti_snoring.snoring.fr.anti_snoring;

import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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

    @Rule
    public ActivityTestRule<AntiSnoringActivity> mActivityRule =
            new ActivityTestRule(AntiSnoringActivity.class);

    @Test
    public void mainViewIsDisplayed() {
        onView(withId(R.id.main))        
                .perform(click())               
                .check(matches(isDisplayed()));
    }

    @Test
    public void volumeViewIsDisplayed() {
        onView(withId(R.id.volume))        
                .perform(click())               
                .check(matches(isDisplayed()));
    }

    @Test
    public void sonSelectionneViewIsDisplayed() {
        onView(withId(R.id.son_selectionne))        
                .perform(click())               
                .check(matches(isDisplayed()));
    }

    @Test
    public void sleepingViewIsDisplayed() {
        onView(withId(R.id.sleeping))        
                .perform(click())               
                .check(matches(isDisplayed()));
    }
}