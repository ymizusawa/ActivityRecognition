package jp.yoshi_misa.recognition;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class RecognitionIntentService extends IntentService {

    public RecognitionIntentService() {
        super("RecognitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!ActivityRecognitionResult.hasResult(intent)) {
            return;
        }

        // 認識結果を取得する
        ActivityRecognitionResult result = ActivityRecognitionResult
                .extractResult(intent);

        DetectedActivity mostProbableActivity = result
                .getMostProbableActivity();
        int activityType = mostProbableActivity.getType();
        int confidence = mostProbableActivity.getConfidence();
        long time = result.getTime();
        long elapsedRealtimeMillis = result.getElapsedRealtimeMillis();

        // DBに保存
        ContentValues values = new ContentValues();
        values.put(Consts.ACTIVITY_TYPE, activityType);
        values.put(Consts.CONFIDENCE, confidence);
        values.put(Consts.TIME, time);
        values.put(Consts.ELAPSED_REALTIME_MILLIS, elapsedRealtimeMillis);
        getContentResolver().insert(Consts.RECOGNITION_URL, values);

        // Log.d(TAG, "Receive recognition.");
        // Log.d(TAG, " activityType - " + activityType); // 行動タイプ
        // Log.d(TAG, " confidence - " + confidence); // 確実性（精度みたいな）
        // Log.d(TAG,
        // " time - "
        // + DateFormat.format("hh:mm:ss.sss", result.getTime())); // 時間
        // Log.d(TAG,
        // " elapsedTime - "
        // + DateFormat.format("hh:mm:ss.sss",
        // result.getElapsedRealtimeMillis())); // よく分からん

        // // 画面に結果を表示するために、Broadcast で通知。
        // // MainActivity にしかけた BroadcastReceiver で受信する。
        // Intent notifyIntent = new Intent("receive_recognition");
        // notifyIntent.putExtra("activity_type", activityType);
        // notifyIntent.putExtra("confidence", confidence);
        // notifyIntent.putExtra("time", result.getTime());
        //
        // LocalBroadcastManager.getInstance(this).sendBroadcast(notifyIntent);
    }
}