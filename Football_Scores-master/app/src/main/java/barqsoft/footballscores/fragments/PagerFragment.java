package barqsoft.footballscores.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.fragments.MainScreenFragment;
import barqsoft.footballscores.widget.WidgetProvider;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public static final String DATE_INDEX = "date";
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private String TAG = getClass().getSimpleName();
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        int toSelect = -1;
        Long selectedGameTime = null;
        Calendar calendarSelected = null;
        /**
         * time of selected game
         */
        if(getArguments() != null){
            selectedGameTime = getArguments().getLong(WidgetProvider.EXTRA_DATE);
            calendarSelected = Calendar.getInstance();
            calendarSelected.setTimeInMillis(selectedGameTime);
        }


        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            Log.i("PagerFragment", "fragmentDate :: "+fragmentdate.getTime());

            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

            viewFragments[i] = new MainScreenFragment();
            /**
             * This line was causing a WEIRD behaviour in the MSFragment...
             * passing the date through the intent is better and more reliable
             */
//            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
            viewFragments[i].setTitle(mPagerAdapter.getPageTitle(i).toString());

            Bundle args = new Bundle();
            args.putString(DATE_INDEX, mformat.format(fragmentdate));
            viewFragments[i].setArguments(args);
            /**
             * if day of selected game  == day of page .. then start in this page
             */
            if(calendarSelected != null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fragmentdate);
                if(calendarsMatch(calendar, calendarSelected)){
                    toSelect = i;
//                    Log.i(TAG, "SON IGUALEEEEES");
                }
            }
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setOffscreenPageLimit(2);
        if(toSelect != -1){
            mPagerHandler.setCurrentItem(toSelect);
        }else{
            mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        }
        return rootView;
    }

    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }

    private boolean calendarsMatch(Calendar calendarOne, Calendar calendarTwo){
        return (calendarOne.get(Calendar.DAY_OF_YEAR) == calendarTwo.get(Calendar.DAY_OF_YEAR))
                && (calendarOne.get(Calendar.YEAR) == calendarTwo.get(Calendar.YEAR));
    }
}
