package barqsoft.footballscores.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ViewHolder;
import barqsoft.footballscores.adapters.ScoresAdapter;
import barqsoft.footballscores.service.MyFetchService;
import barqsoft.footballscores.utils.Utilies;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;
    private String mPageTitle;
    public static final String NETWORK_AVAILABLE_INDEX = "network_available";

    public MainScreenFragment() {
    }

    public void setTitle(String title){
        mPageTitle = title;
    }

    private void update_scores()
    {
        Context context = getContext();
        boolean networkAvailable = Utilies.isNetworkAvailable(context);
        if(!networkAvailable) {
            Toast.makeText(context,
                    context.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        service_start.putExtra(NETWORK_AVAILABLE_INDEX, networkAvailable);
        getActivity().startService(service_start);
    }

//    NOPE
//    public void setFragmentDate(String date)
//    {
//        Log.i("MainScreenFragment", "FRAGMENT :: " + mPageTitle + " ... SETTING DATE :: " + date);
//        fragmentdate[0] = date;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        fragmentdate[0] = getArguments().getString(PagerFragment.DATE_INDEX);
//        Log.i("MainScreenFragment", "onCreateView .. from ARGS .. DATE :: " + fragmentdate[0]);

        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(),null,0);
        score_list.setAdapter(mAdapter);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        /**
         * Add empty view to notify the user there are no games that day
         */
        TextView emptyView = (TextView) rootView.findViewById(R.id.tv_emptyview);
        emptyView.append(" " + mPageTitle);
        Animation animation = AnimationUtils.makeInAnimation(getContext(), true);
        emptyView.setAnimation(animation);
        score_list.setEmptyView(emptyView);

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        Log.i("MainScreenFragment", "sending fragment date :: " + fragmentdate[0]);
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null,null,fragmentdate, DatabaseContract.scores_table.DATE_COL+" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

}
