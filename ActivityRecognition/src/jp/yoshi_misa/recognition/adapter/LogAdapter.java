package jp.yoshi_misa.recognition.adapter;

import jp.yoshi_misa.recognition.Consts;
import jp.yoshi_misa.recognition.R;
import jp.yoshi_misa.recognition.R.id;
import jp.yoshi_misa.recognition.R.layout;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

public class LogAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    class ViewHolder {
        TextView time;
        TextView activityTypeConfidence;
    }

    public LogAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 新しくViewを作ります
        final View view = mInflater.inflate(R.layout.list_item, null);

        ViewHolder holder = new ViewHolder();
        holder.time = (TextView) view.findViewById(R.id.time);
        holder.activityTypeConfidence = (TextView) view
                .findViewById(R.id.activityTypeConfidence);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Viewを再利用してデータをセットします
        ViewHolder holder = (ViewHolder) view.getTag();

        // Cursorからデータを取り出します
        // final int id =
        // cursor.getInt(cursor.getColumnIndexOrThrow(Consts._ID));

        final int activityType = cursor.getInt(cursor
                .getColumnIndexOrThrow(Consts.ACTIVITY_TYPE));

        final int confidence = cursor.getInt(cursor
                .getColumnIndexOrThrow(Consts.CONFIDENCE));

        final long time = cursor.getLong(cursor
                .getColumnIndexOrThrow(Consts.TIME));

        // final long elapsed_realtime_millis = cursor.getLong(cursor
        // .getColumnIndexOrThrow(Consts.ELAPSED_REALTIME_MILLIS));

        // 画面にセットします

        holder.time.setText(DateFormat.format("yyyy/MM/dd hh:mm:ss", time));
        holder.activityTypeConfidence.setText(getNameFromType(activityType)
                + "(" + confidence + ")");
    }

    private String getNameFromType(int activityType) {
        switch (activityType) {
        case DetectedActivity.IN_VEHICLE:
            return "車で移動";
        case DetectedActivity.ON_BICYCLE:
            return "自転車で移動";
        case DetectedActivity.ON_FOOT:
            return "徒歩で移動";
        case DetectedActivity.STILL:
            return "静止";
        case DetectedActivity.UNKNOWN:
            return "不明";
        case DetectedActivity.TILTING:
            return "傾いた";
        }

        return "unknown - " + activityType;
    }
}
