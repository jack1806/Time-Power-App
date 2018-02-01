package com.example.android.timepower;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.timepower.contract.sharedPrefContractClass;
import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.custom.adapters.TimeTableRecyclerViewAdapter;
import com.example.android.timepower.custom.objects.timeTable;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.custom.objects.sharedPrefLinker;


import java.util.ArrayList;
import java.util.Calendar;

public class EditTimeTable extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    String TAG = "EditTimeTable.java";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public SharedPreferences preferences;
    public Button mAddButton;
    //SharedPreferences mSharedPreferences;
    //sharedPrefLinker prefLinker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        final timeTable table = new timeTable();

        mAddButton = (Button)findViewById(R.id.edit_time_table_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),AddElementActivity.class);
                String day = table.getDays().get(mViewPager.getCurrentItem());
                intent.putExtra(intentContractClass.editTimeTable_To_AddElement_Day,day);
                startActivity(intent);
                finish();
            }
        });

        //timeTable mTable = new timeTable();
        //Calendar calendar = Calendar.getInstance();
        //String mDay = calendar.getTime().toString().split(" ")[0];

        //mViewPager.setCurrentItem(mTable.getDays().indexOf(mDay)+1);

        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_time_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditTimeTable.this,MainActivity.class));
        finish();
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            SharedPreferences preferences;
            timeTable table = new timeTable();
            View rootView = inflater.inflate(R.layout.fragment_edit_time_table, container, false);
            String day = table.getDays().get(getArguments().getInt(ARG_SECTION_NUMBER)-1);
            preferences = getContext().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                    sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
            //final TextView addHeader = (TextView)rootView.findViewById(R.id.add_header);
            //final TextView addSubHeader = (TextView)rootView.findViewById(R.id.add_sub_title);
            sharedPrefLinker prefLinker = new sharedPrefLinker();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
            RecyclerView timeTableRecyclerView = (RecyclerView)rootView.findViewById(R.id.edit_time_table_recycler_view);
            //ImageView element_add_image = (ImageView)rootView.findViewById(R.id.element_add_image_button);
            /*Button button = (Button)rootView.findViewById(R.id.element_add_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timeTableElement element = new timeTableElement(addHeader.getText().toString(),
                            addSubHeader.getText().toString(),8,9);
                    timeTable table = prefLinker.getTimeTable(preferences);
                    timeTableElement result = table.addElement(day,element);
                    if(result==null) {
                        String dataJSON = prefLinker.getString(table);
                        Log.d("Element : \n",dataJSON);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                                dataJSON);
                        editor.commit();
                        Toast.makeText(getContext(), "DONE !", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), "Clashed !", Toast.LENGTH_SHORT).show();
                }
            });*/
            ArrayList<timeTableElement> dayElements = prefLinker.getTimeTable(preferences).
                    getDayElements(day);
            TimeTableRecyclerViewAdapter viewAdapter = new TimeTableRecyclerViewAdapter(dayElements);
            timeTableRecyclerView.setAdapter(viewAdapter);
            timeTableRecyclerView.setLayoutManager(layoutManager);
            /*Button addElementButton = (Button)rootView.findViewById(R.id.edit_time_table_add_button);
            addElementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),AddElementActivity.class);
                    intent.putExtra(intentContractClass.editTimeTable_To_AddElement_Day,day);
                    startActivity(intent);
                    ((EditTimeTable)getContext()).finish();
                }
            });*/
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mon";
                case 1:
                    return "Tue";
                case 2:
                    return "Wed";
                case 3:
                    return "Thu";
                case 4:
                    return "Fri";
                case 5:
                    return "Sat";
                case 6:
                    return "Sun";
            }
            return null;
        }
    }
}
