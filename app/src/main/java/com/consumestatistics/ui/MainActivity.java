package com.consumestatistics.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.consumestatistics.R;
import com.consumestatistics.entity.BankList;
import com.consumestatistics.entity.Consume;
import com.consumestatistics.provider.ProviderConstants;
import com.consumestatistics.utils.DbUtil;
import com.consumestatistics.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends ActionBarActivity /*implements NavigationDrawerFragment.NavigationDrawerCallbacks */ {

    public static ArrayList<Consume> consumes;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
//
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
        //当程序第一次运行时,必须先去读取原有sms数据库中的数据并保存在本地数据库中
        checkIsFirstSetup();
        initConsumeList();
        InitViews();
        registerContentObservers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(smsContentObserver);
    }

    ListView consume_list;
    MyAdapter myAdatpter;

    private void InitViews() {
        myAdatpter = new MyAdapter();
        consume_list = (ListView) findViewById(R.id.consume_list);
        consume_list.setAdapter(myAdatpter);
    }

    private void initConsumeList() {
        consumes = DbUtil.queryAllLocalData(this);
    }

    /**
     * 根据versionCode来判断是否为第一次启动，若是第一次启动，则读取sms数据库
     * 注意，根据版本号判断意味着每次升级版本都会被判断为第一次启动
     */
    private void checkIsFirstSetup() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.consumestatistics", 0);
            int currentVersion = info.versionCode;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = prefs.getInt("Version_Code", 0);
            if (currentVersion > lastVersion) {
                //如果当前版本大于上次版本，该版本属于第一次启动，读取数据库
                DbUtil.ReadMmsDB(this);
                //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
                prefs.edit().putInt("Version_Code", currentVersion).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SMSContentObserver smsContentObserver = new SMSContentObserver();

    private void registerContentObservers() {
        getContentResolver().registerContentObserver(ProviderConstants.ConsumeColumns.CONTENT_URI, true, smsContentObserver);
    }

    public class SMSContentObserver extends ContentObserver {
        public SMSContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            LogUtil.log("the sms table has changed");
            consumes = DbUtil.queryAllLocalData(MainActivity.this);
            myAdatpter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
//    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//        }
//    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
//            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//        private ListView consume_list;
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            consume_list = (ListView) rootView.findViewById(R.id.consume_list);
//            consume_list.setAdapter(new MyAdapter());
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }
    protected void ShowLogToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void ShowShortToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected class MyAdapter extends BaseAdapter {
        ArrayList<Consume> datas;

        public MyAdapter() {
            datas = new ArrayList<Consume>();
            StatisticesConsume();
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public void notifyDataSetChanged() {
            StatisticesConsume();
            super.notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item,
                        null);
                holder = new ViewHolder();
                holder.tv_concume_body = (TextView) convertView.findViewById(R.id.tv_concume_body);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_concume_body.setText(BankList.get(datas.get(position).BANK) + ":" + datas.get(position).AMOUNT);
            return convertView;
        }

        public final class ViewHolder {
            public TextView tv_concume_body;
        }

        private void StatisticesConsume() {
            if (consumes == null) return;
            for (int i = 0; i < consumes.size(); i++) {
                Consume consume = consumes.get(i);
                if (!datas.contains(consume)) {
                    datas.add(consume);
                } else {
                    datas.get(datas.indexOf(consume)).AMOUNT += consume.AMOUNT;
                }

            }
        }

    }

}