package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Utilies;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.adapters.ScoresAdapter;

/**
 * Created by daniellujanvillarreal on 10/9/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetListViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext(), intent);
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        mCursor = mContext.getContentResolver().query(
                DatabaseContract.BASE_CONTENT_URI, null, null, null,
                DatabaseContract.scores_table.DATE_COL+" DESC");
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("WidgetService", "COUNT :: "+ mCursor.getCount());
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        mCursor.moveToPosition(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item_widget);

        rv.setTextViewText(R.id.home_name, mCursor.getString(ScoresAdapter.COL_HOME));
        rv.setTextViewText(R.id.away_name, mCursor.getString(ScoresAdapter.COL_AWAY));
        rv.setTextViewText(R.id.data_textview, mCursor.getString(ScoresAdapter.COL_MATCHTIME));
        rv.setTextViewText(R.id.score_textview,
                Utilies.getScores(mCursor.getInt(ScoresAdapter.COL_HOME_GOALS)
                        , mCursor.getInt(ScoresAdapter.COL_AWAY_GOALS)));
        rv.setTextViewText(R.id.away_name, mCursor.getString(ScoresAdapter.COL_AWAY));
        rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                mCursor.getString(ScoresAdapter.COL_HOME)));
        rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                mCursor.getString(ScoresAdapter.COL_AWAY)));

// Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(WidgetProvider.EXTRA_ITEM, position);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(mCursor.getString(ScoresAdapter.COL_DATE));
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        extras.putLong(WidgetProvider.EXTRA_DATE, date.getTime());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.widget_listitem_wrapper, fillInIntent);
//        Intent fillInIntent = new Intent(mContext, MainActivity.class);
//        Bundle extras = new Bundle();
//        extras.putInt("clicked_view_index", position);
//        fillInIntent.putExtras(extras);
//        // Make it possible to distinguish the individual on-click
//        // action of a given item
//        rv.setOnClickFillInIntent(R.id.widget_listitem_wrapper, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
