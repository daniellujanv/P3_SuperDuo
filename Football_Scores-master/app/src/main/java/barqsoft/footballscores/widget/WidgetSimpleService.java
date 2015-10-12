package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.ScoresDBHelper;

/**
 *
 * Created by daniellujanvillarreal on 10/12/15.
 */
public class WidgetSimpleService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
       Context context = getApplicationContext();

        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String[] selectionArgs = new String[]{
                mformat.format(date)
        };

        Cursor mCursor = context.getContentResolver()
               .query(DatabaseContract.scores_table.buildScoreWithDate(), null
                       , null
                       , selectionArgs
                       , null);

        // Build the widget update for today
        RemoteViews updateViews = buildView(context, mCursor);

        Intent intentMainAct = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainAct, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_wrapper, pendingIntent);

        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetProvider.class);

        manager.updateAppWidget(thisWidget, updateViews);
//        Log.d("SimpleService", "Updated v10 Widget!!!!!!");
        stopSelf();
        return START_NOT_STICKY;
    }

    private RemoteViews buildView(Context context, Cursor cursor){
        RemoteViews views = new RemoteViews(context.getPackageName()
                , R.layout.widget);

        views.setTextViewText(R.id.matchesnumber_textview, cursor.getCount()+"");
//        Log.d("WidgetSimpleService", "UPDATING SCORE");

        return views;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
