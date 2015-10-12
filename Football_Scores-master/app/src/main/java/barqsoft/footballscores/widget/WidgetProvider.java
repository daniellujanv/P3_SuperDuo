package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by daniellujanvillarreal on 10/9/15.
 */
public class WidgetProvider extends AppWidgetProvider{

    public static final String WIDGET_DATASETCHANGED = "football_scores.action.WIDGET_DATASETCHANGED";
    public static final String WIDGET_ONCLICK = "football_scores.action.WIDGET_ONCLICK";
    private String TAG = getClass().getSimpleName();
    public static final String EXTRA_DATE = "com.example.android.stackwidget.EXTRA_DATE";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
//        if(WIDGET_DATASETCHANGED.equals(intent.getAction())){
////            Log.d(TAG, "received DATASETCHANGED intent");
//        }else
        if(WIDGET_ONCLICK.equals(intent.getAction())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.putExtras(intent.getExtras());
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.d(TAG, "OnUpdate");

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {


            // Add the app widget ID to the intent extras.
            // Instantiate the RemoteViews object for the app widget layout.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

                // Set up the RemoteViews object to use a RemoteViews adapter.
                // This adapter connects
                // to a RemoteViewsService  through the specified intent.
                // This is how you populate the data.
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    // Set up the intent that starts the StackViewService, which will
                    // provide the views for this collection.
                    Intent serviceIntent = new Intent(context, WidgetListViewService.class);
                    rv.setRemoteAdapter(R.id.widget_listView, serviceIntent);
                }else {
                    // Set up the intent that starts the StackViewService, which will
                    // provide the views for this collection.
                    Intent serviceIntent = new Intent(context, WidgetListViewService.class);
                    rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_listView, serviceIntent);
                }

                // The empty view is displayed when the collection has no items.
                // It should be in the same layout used to instantiate the RemoteViews
                // object above.
                rv.setEmptyView(R.layout.widget, R.id.widget_emptyView);

                // This section makes it possible for items to have individualized behavior.
                // It does this by setting up a pending intent template. Individuals items of a collection
                // cannot set up their own pending intents. Instead, the collection as a whole sets
                // up a pending intent template, and the individual items set a fillInIntent
                // to create unique behavior on an item-by-item basis.
                Intent toastIntent = new Intent(context, WidgetProvider.class);
                // Set the action for the intent.
                // When the user touches a particular view, it will have the effect of
                // broadcasting TOAST_ACTION.
                toastIntent.setAction(WidgetProvider.WIDGET_ONCLICK);
                toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(
                        context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setPendingIntentTemplate(R.id.widget_listView, onClickPendingIntent);


                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                rv.setOnClickPendingIntent(R.id.widget_listview_wrapper, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

//                Intent intent = new Intent(context, MainActivity.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//                rv.setOnClickPendingIntent(R.id.widget_listview_wrapper, pendingIntent);
            }else{
                /**
                 * Service to populate pre honeycomb view
                 */
                context.startService(new Intent(context, WidgetSimpleService.class));
            }
        }
    }
}
