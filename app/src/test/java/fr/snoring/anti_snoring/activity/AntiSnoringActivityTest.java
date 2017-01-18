package fr.snoring.anti_snoring.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SeekBar;

import com.qxxmucxymh.hathpsneoi122008.AdListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AntiSnoringActivityTest {

    private AntiSnoringActivity antiSnoringActivity;

    @Before
    public void setUp() throws Exception {
        antiSnoringActivity = mock(AntiSnoringActivity.class);
        when(antiSnoringActivity.getWindow()).thenReturn(mock(Window.class));

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onCreate() throws Exception {
        antiSnoringActivity.onCreate(mock(Bundle.class));
    }

    @Test
    public void onCreateOptionsMenu() throws Exception {
        antiSnoringActivity.onCreateOptionsMenu(mock(Menu.class));
    }

    @Test
    public void onOptionsItemSelected() throws Exception {
        antiSnoringActivity.onOptionsItemSelected(mock(MenuItem.class));
    }

    @Test
    public void onDestroy() throws Exception {
        antiSnoringActivity.onDestroy();
    }

    @Test
    public void onBackPressed() throws Exception {
        antiSnoringActivity.onBackPressed();
    }

    @Test
    public void onProgressChanged() throws Exception {
        antiSnoringActivity.onProgressChanged(mock(SeekBar.class), 1, true);
    }

    @Test
    public void onStartTrackingTouch() throws Exception {
        antiSnoringActivity.onStartTrackingTouch(mock(SeekBar.class));
    }

    @Test
    public void onStopTrackingTouch() throws Exception {
        antiSnoringActivity.onStopTrackingTouch(mock(SeekBar.class));
    }

    @Test
    public void onActivityResult() throws Exception {
        antiSnoringActivity.onActivityResult(1, 2, mock(Intent.class));
    }

    @Test
    public void onAdCached() throws Exception {
        // /antiSnoringActivity.onAdCached(AdConfig.AdType.appwall);
    }

    @Test
    public void onError() throws Exception {
        antiSnoringActivity.onError(AdListener.ErrorType.INTERNAL, "Internal Error");
    }

    @Test
    public void onAdClosed() throws Exception {
        antiSnoringActivity.onAdClosed();
    }

    @Test
    public void onAdLoading() throws Exception {
        antiSnoringActivity.onAdLoading();
    }

    @Test
    public void onAdLoaded() throws Exception {
        antiSnoringActivity.onAdLoaded();
    }

    @Test
    public void onAdExpanded() throws Exception {
        antiSnoringActivity.onAdExpanded();
    }

    @Test
    public void onAdClicked() throws Exception {
        antiSnoringActivity.onAdClicked();
    }

    @Test
    public void getIndexApiAction() throws Exception {
        antiSnoringActivity.getIndexApiAction();
    }

    @Test
    public void onStart() throws Exception {
        antiSnoringActivity.onStart();
    }

    @Test
    public void onStop() throws Exception {
        antiSnoringActivity.onStop();
    }

    @Test
    public void onRequestPermissionsResult() throws Exception {
        antiSnoringActivity.onRequestPermissionsResult(1, new String[]{"toto"}, new int[]{0});
    }

}