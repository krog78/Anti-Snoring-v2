package fr.snoring.anti_snoring.activity;

import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.qxxmucxymh.hathpsneoi122008.AdConfig;
import com.qxxmucxymh.hathpsneoi122008.AdConfig.AdType;
import com.qxxmucxymh.hathpsneoi122008.AdListener;
import com.qxxmucxymh.hathpsneoi122008.AdView;
import com.qxxmucxymh.hathpsneoi122008.Main;

import java.io.IOException;

import fr.snoring.anti_snoring.R;
import fr.snoring.anti_snoring.preferences.SoundPreference;
import fr.snoring.anti_snoring.sound.SoundFile;
import fr.snoring.anti_snoring.utils.FileUtils;
import fr.snoring.anti_snoring.utils.PollTask;

public class AntiSnoringActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AdListener {

    private static final int CHOIX_FICHIER_AUDIO = 100;
    private Main main; // Declare here

    private MenuInflater menuInflater;

    private SoundPreference soundPreference;

    private PollTask pollTask;

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

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

        // AIRPUSH BEGIN
        AdConfig.setAppId(79190); // setting appid.
        AdConfig.setApiKey("1351602905122008414"); // setting apikey
        AdConfig.setCachingEnabled(true); // Enabling SmartWall ad caching.
        AdConfig.setPlacementId(0); // pass the placement id.
        AdView.setAdListener(this);// Add listener for Inline 360 ads
        // AIRPUSH END

        setContentView(R.layout.main);

        // Verify that the device has a mic first
        PackageManager pmanager = this.getPackageManager();
        if (!pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            Toast.makeText(this, "This device doesn't have a mic!", Toast.LENGTH_LONG).show();
            finish();
        }

        // Ask the user authorization to record sound
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, requestCode);
            }else{
                initPollTask();
            }
        } else {
            initPollTask();
        }

        menuInflater = getMenuInflater();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        soundPreference = new SoundPreference(this);

        // Init Poll Task
        try {
            pollTask = new PollTask(this, soundPreference.getPreference());
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

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.layout_seekbar);
                dialog.setTitle(R.string.reglage_sensibilite);
                SeekBar seek = (SeekBar) dialog.findViewById(R.id.seekbar);
                if (pollTask != null) {
                    seek.setProgress(pollTask.getmThreshold() * 10);
                }
                seek.setOnSeekBarChangeListener(this);
                ImageView close = (ImageView) dialog.findViewById(R.id.cross);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                dialog.show();
                return true;
            case R.id.changer_son:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.choix_son);
                builder.setItems(R.array.sounds_texts, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        TypedArray soundIds = getResources().obtainTypedArray(R.array.sounds_ids);
                        String selectedSoundName = soundIds.getText(item).toString();
                        int selectedSoundId = soundIds.getResourceId(item, 0);
                        // External file selected
                        if (selectedSoundName.equals("OTHER")) {
                            // Select a recording
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("audio/*");
                            startActivityForResult(Intent.createChooser(i, getString(R.string.select_audio)),
                                    CHOIX_FICHIER_AUDIO);

                        } else { // Internal file selected
                            SoundFile selectedSound = soundPreference.getInternalSounds().get(selectedSoundId);
                            soundPreference.savePreference(selectedSound);
                            if (pollTask != null) {
                                pollTask.getAudioPlayer().changerSon(AntiSnoringActivity.this,
                                        selectedSoundId);
                            }
                            dialog.dismiss();
                        }
                        soundIds.recycle();

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.confidentiality_rules:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://theslygecompany.wordpress.com/2017/02/07/antisnoring-confidentiaty-rules/"));
                startActivity(browserIntent);
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
        if (pollTask != null) {
            pollTask.release();
        }
        super.onBackPressed();
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
                    soundPreference.savePreference(new SoundFile(fileName, prefSon));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) {
            AntiSnoringActivity.super.finish();
        } else if (!permissionToWriteAccepted) {
            AntiSnoringActivity.super.finish();
        } else {
            initPollTask();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
