package fr.snoring.anti_snoring.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.qxxmucxymh.hathpsneoi122008.AdConfig;
import com.qxxmucxymh.hathpsneoi122008.AdConfig.AdType;
import com.qxxmucxymh.hathpsneoi122008.AdListener;
import com.qxxmucxymh.hathpsneoi122008.AdView;
import com.qxxmucxymh.hathpsneoi122008.Main;

import java.io.IOException;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.activity.preferences.SoundPreference;
import fr.snoring.anti_snoring.sound.SoundFile;
import fr.snoring.anti_snoring.utils.FileUtils;
import fr.snoring.anti_snoring.utils.PollTask;

public class AntiSnoringActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AdListener {

    public static String TAG = "anti-snoring";

    private static final int CHOIX_FICHIER_AUDIO = 100;

    private Main main; // Declare here

    private MenuInflater menuInflater;

    private SoundPreference soundPreference;

    private PollTask pollTask;

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     *                           is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        // AIRPUSH BEGIN
        AdConfig.setAppId(79190); // setting appid.
        AdConfig.setApiKey("1351602905122008414"); // setting apikey
        AdConfig.setCachingEnabled(true); // Enabling SmartWall ad caching.
        AdConfig.setPlacementId(0); // pass the placement id.
        AdView.setAdListener(this);// Add listener for Inline 360 ads
        // AIRPUSH END

        setContentView(R.layout.main);

        // Ask the user authorization to record sound
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        } else {
            initPollTask();
        }

        menuInflater = getMenuInflater();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // AIRPUSH BEGIN
        // Initialize Airpush
        main = new Main(this, this);

        // for calling banner 360
        main.start360BannerAd(this);

        // for calling Smartwall ad
        main.startInterstitialAd(AdType.smartwall, this);
        // AIRPUSH END
    }

    private void initPollTask() {

        soundPreference = new SoundPreference(AntiSnoringActivity.this);

        // Load the default sounds
        soundPreference.loadDefaultSounds(getResources());

        soundPreference.updateSoundText(this);

        // Init Poll Task
        try {
            pollTask = new PollTask(this, soundPreference.getCurrentSound());
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater.inflate(R.menu.liste_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sensibilite:

                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.layout_seekbar);
                dialog.setTitle(R.string.reglage_sensibilite);
                SeekBar seek = (SeekBar) dialog.findViewById(R.id.seekbar);
                if (pollTask != null) {
                    seek.setProgress(pollTask.getmThreshold() * 10);
                }
                seek.setOnSeekBarChangeListener(this);

                dialog.show();
                return true;
            case R.id.changer_son:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.choix_son);
                builder.setItems(R.array.sons_liste, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        TypedArray sonsFichier = getResources().obtainTypedArray(R.array.sons_fichier);
                        // External file selected
                        if (sonsFichier.getText(item).toString().equals("OTHER")) {
                            // Select a recording
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("audio/*");
                            startActivityForResult(Intent.createChooser(i, getString(R.string.select_audio)),
                                    CHOIX_FICHIER_AUDIO);

                        } else { // Internal file selected
                            SoundFile selectedSound = soundPreference.getDefaultSounds().get(item);
                            soundPreference.savePreference(AntiSnoringActivity.this, selectedSound);
                            if (pollTask != null) {
                                pollTask.getAudioPlayer().changerSon(AntiSnoringActivity.this,
                                        sonsFichier.getResourceId(item, 0));
                            }
                            dialog.dismiss();
                        }
                        sonsFichier.recycle();

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.quitter:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollTask != null) {
            pollTask.release();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            //Show the cached SmartWall Ad
            main.showCachedAd(AdType.smartwall, this);
        } catch (Exception e) {
            super.onBackPressed();
        }
        super.onBackPressed();
        if (pollTask != null) {
            pollTask.release();
        }
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        if (pollTask != null) {
            pollTask.changeThreshold(this, arg1);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        LinearLayout v = (LinearLayout) findViewById(R.id.main);
        v.removeView(arg0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent sonReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, sonReturnedIntent);

        switch (requestCode) {
            case CHOIX_FICHIER_AUDIO:
                if (resultCode == RESULT_OK) {
                    Uri uri = sonReturnedIntent.getData();
                    String fileName = FileUtils.getFilename(this, uri);
                    String prefSon = uri.toString();
                    soundPreference.savePreference(AntiSnoringActivity.this, new SoundFile(fileName, -1, prefSon));
                    if (pollTask != null) {
                        pollTask.getAudioPlayer().release();
                        pollTask.getAudioPlayer().create(this, uri.toString());
                    }
                }

        }
    }

    @Override
    public void onAdCached(AdType adType) {
        //This will get called when an ad is cached.

    }

    @Override
    public void onError(AdListener.ErrorType errorCode, String errorMessage) {
        /* This will get called when any error has occurred. This will also get called if the SDK notices any integration mistakes.
         You can check the ErrorType to know the error type. */
    }


    @Override
    public void onAdClosed() {
        //This will get called when an ad is closing/resizing from an expanded state.
    }

    @Override
    public void onAdLoading() {
        //This will get called when a rich media ad is loading.
    }

    @Override
    public void onAdLoaded() {
        //This will get called when an ad has loaded.
    }


    @Override
    public void onAdExpanded() {
        //This will get called when an ad is showing on a user's screen. This may cover the whole UI.
    }

    @Override
    public void onAdClicked() {
        //This will get called when ad is clicked.
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AntiSnoring Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) AntiSnoringActivity.super.finish();
        if (!permissionToWriteAccepted) AntiSnoringActivity.super.finish();
        initPollTask();
    }
}
