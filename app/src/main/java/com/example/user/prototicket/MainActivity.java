package com.example.user.prototicket;

import android.support.v4.app.FragmentTransaction;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;


/**
 * Created by User on 11/12/2017.
 */

public class MainActivity extends FragmentActivity {

    MainPagerAdapter mainPagerAdapter;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final ActionBar actionBar = getActionBar();



        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mainPagerAdapter);

        // Action Bar

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        actionBar.addTab(
                actionBar.newTab()
                    .setText("Verificar")
                    .setTabListener(tabListener)
        );


        actionBar.addTab(
                actionBar.newTab()
                        .setText("Crear")
                        .setTabListener(tabListener)
        );
        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageSelected(int position){
                        getActionBar().setSelectedNavigationItem(position);
                    }
                }
        );
    }


    public class MainPagerAdapter extends FragmentStatePagerAdapter {
        public MainPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int idx){
            switch (idx){
                case 0: return Verify.newInstance("First fragment");
                default:  return Create.newInstance("Main Verify");
            }
        }

        @Override
        public int getCount(){
            return 2;
        }
    }
}
