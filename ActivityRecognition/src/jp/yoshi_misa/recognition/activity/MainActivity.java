package jp.yoshi_misa.recognition.activity;

import jp.yoshi_misa.recognition.Consts;
import jp.yoshi_misa.recognition.R;
import jp.yoshi_misa.recognition.R.id;
import jp.yoshi_misa.recognition.R.layout;
import jp.yoshi_misa.recognition.R.string;
import jp.yoshi_misa.recognition.adapter.LogAdapter;
import jp.yoshi_misa.recognition.service.RecognitionIntentService;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;

public class MainActivity extends FragmentActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        OnCheckedChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ActionBar actionBar;
    private ActivityRecognitionClient mClient;
    protected PendingIntent mPendingIntent;

    private LogAdapter mAdapter;
    private ListView listView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mClient == null) {
            mClient = new ActivityRecognitionClient(this, this, this);
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                "service", false)) {
            if(!mClient.isConnected()) {
               mClient.connect();
            }
        }

        actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        // ActionBarの設定
        setActionBarSwitch(actionBar);

        listView1 = (ListView) findViewById(R.id.listView1);

        //
        mAdapter = new LogAdapter(this, null, true);
        listView1.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Loaderの初期化
        getLoaderManager().initLoader(0, null, this);
    }

    private void setActionBarSwitch(ActionBar actionBar) {
        Switch actionBarSwitch = new Switch(this);
        actionBarSwitch
                .setChecked(PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("service", false));
        actionBarSwitch.setTextOn(getString(R.string.on));
        actionBarSwitch.setTextOff(getString(R.string.off));
        actionBarSwitch.setPadding(0, 0, 20, 0);
        actionBarSwitch.setOnCheckedChangeListener(this);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                        | Gravity.RIGHT));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mClient.connect();
        } else {
            if (mClient == null || !mClient.isConnected()) {
                return;
            }

            mClient.removeActivityUpdates(mPendingIntent);
            mClient.disconnect();
        }

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("service", isChecked).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getLoaderManager().destroyLoader(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int data, Bundle bundle) {
        return new CursorLoader(this, Consts.RECOGNITION_URL, null, null, null,
                Consts.TIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, RecognitionIntentService.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mClient.requestActivityUpdates(20 * 1000, mPendingIntent);
    }

    @Override
    public void onDisconnected() {
        mClient = null;
    }

}