package com.ramraj.pebblecanvasnotifications;

import com.ramraj.pebblecanvasnotifications.R;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements android.app.ActionBar.TabListener
{

	 private ViewPager viewPager;
	    private TabsPageAdapter mAdapter;
	    private ActionBar actionBar;
	    // Tab titles
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        String[] tabs = {getString(R.string.tab_main_list),
	        		getString(R.string.tab_important_list)	  };
	        // Initialization
	        viewPager = (ViewPager) findViewById(R.id.pager);
	        actionBar = getActionBar();
	        mAdapter = new TabsPageAdapter(getSupportFragmentManager());
	 
	        viewPager.setAdapter(mAdapter);
	        actionBar.setHomeButtonEnabled(false);
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
	 
	        // Adding Tabs
	        for (String tab_name : tabs) {
	            actionBar.addTab(actionBar.newTab().setText(tab_name)
	                    .setTabListener(this));
	        }
	        /**
	         * on swiping the viewpager make respective tab selected
	         * */
	        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	         
	            @Override
	            public void onPageSelected(int position) {
	                // on changing the page
	                // make respected tab selected
	                actionBar.setSelectedNavigationItem(position);
	            }
	         
	            @Override
	            public void onPageScrolled(int arg0, float arg1, int arg2) {
	            }
	         
	            @Override
	            public void onPageScrollStateChanged(int arg0) {
	            }
	        });
	        
	    }
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
