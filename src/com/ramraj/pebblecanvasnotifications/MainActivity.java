package com.ramraj.pebblecanvasnotifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ramraj.pebblecanvasnotifications.NotificationSourceList;
import com.ramraj.pebblecanvasnotifications.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 private ListView lv;
	    
	public void onResume() {
		
		super.onResume();
		 final NotificationSourceList notificationSourceList = new NotificationSourceList(getApplicationContext());
		    Set<String> fullList = notificationSourceList.getFullProgramList();
		    Set<String> blackList = notificationSourceList.getBlackListedProgramList();
		    setContentView(R.layout.activity_main);
		    CheckBox enabled= (CheckBox)findViewById(R.id.checkBoxEnablePebblePlusPlus);
			//if (enabled!=null)
			    enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						prefs.edit().putBoolean("serviceWanted", arg1).commit();
					}});
			

	        lv = (ListView) findViewById(R.id.listView1);
	        // Instanciating an array list (you don't need to do this, you already have yours)
	        // This is the array adapter, it takes the context of the activity as a first // parameter, the type of list view as a second parameter and your array as a third parameter
	        String[] temparr1=fullList.toArray(new String[fullList.size()]);        
	        
	        
	        final PackageManager pm = getApplicationContext().getPackageManager();
	        ApplicationInfo ai;
	        List<String> listWithNames = Arrays.asList(temparr1);
	        
	        
	        final List<String> list = new ArrayList<String>(listWithNames);
	        
	        
	        for(int l=0;l<list.size();l++) {
	        	try {
	                ai = pm.getApplicationInfo( list.get(l), 0);
	            } catch (final NameNotFoundException e) {
	                ai = null;
	            }
	        	if (ai!=null) listWithNames.set(l, (String) pm.getApplicationLabel(ai));
	        }
	        ArrayAdapter<String> arrayAdapter =      
	        new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,(String[]) listWithNames.toArray());
	        
	        TextView tv=new TextView(getApplicationContext());
	        tv.setText(R.string.settings_app_list_will_become_longer);
	        lv.addFooterView(tv);
	        
	        lv.setAdapter(arrayAdapter);
	        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        for (int l=0;l<list.size();l++) if(!blackList.contains(list.get(l))) lv.setItemChecked(l, true);
	     // listener for the first one 
	        lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
							// TODO Auto-generated method stub
							//`Log.i("CANV_K9MAIL","user clicked"+arg2+" - "+arg3);
							ListView vv = (ListView)arg0;
							boolean itemSelectionStatus = !(vv.isItemChecked(arg2));
							vv.setItemChecked(arg2,!itemSelectionStatus);
							if (!itemSelectionStatus) {
								// item has been checked as wanted
								notificationSourceList.removeProgramFromBlackList(list.get(arg2));
							} else {
								// blacklist it
								notificationSourceList.addProgramToBlackList(list.get(arg2));
							}
								
						}

	            });
           new CheckAccessibilityInBG(getApplicationContext()).execute("");
   		
	        
	}
	

    private class CheckAccessibilityInBG extends AsyncTask<String, Void, String> {
        private Context myCtx;
        public CheckAccessibilityInBG(Context ctx) {
        	myCtx = ctx;
        }
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			SystemClock.sleep(2000);
			//PebbleKit.startAppOnPebble(getApplicationContext(), SendNotificationsToPebbleService.watchAppUUID);
			if(!isAccessibilityEnabled(myCtx,"com.ramraj.pebblecanvasnotifications")) {
	        	// some times this stupid thing doesn't show up the first time	        	
	        	SystemClock.sleep(2000);
	        	if(!isAccessibilityEnabled(myCtx,"com.ramraj.pebblecanvasnotifications")) {
	        		SystemClock.sleep(2000);
		        	if(!isAccessibilityEnabled(myCtx,"com.ramraj.pebblecanvasnotifications")) {
		        		return("not found");
		        	}

	        	}
	        }
			
			return null;
		}

        @Override
        protected void onPostExecute(String result) {
        	if (result!=null ) {
        	/*AlertDialog.Builder builder = new AlertDialog.Builder(myCtx);
            builder.setTitle("Accessibility not enabled");
            builder.setMessage("In order for Pebble++ to work, it needs access to the phone's notification system. Please \"check\" the box next to \"Pebble++\" in the Accessibility Settings window to enable this.")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("OK",new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
		        	startActivityForResult(intent, 0);	        							
				} }).show();
        	}*/
        		View linearLayout =  findViewById(R.id.errorContainers);
                //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
        		linearLayout.setBackgroundColor(Color.RED);
                TextView valueTV = new TextView(myCtx);
                valueTV.setText("In order for the plugin to work, it needs access to the phone's notification system. Please \"check\" the box next to \"Pebble++\" in the Accessibility Settings window to enable this.");
                valueTV.setId(5);
                
                valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

                ((LinearLayout) linearLayout).addView(valueTV);
                
                Button btn1 = new Button(myCtx);
                btn1.setText("Go to Settings");

                ((LinearLayout)linearLayout).addView(btn1);
                btn1.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			        	startActivityForResult(intent, 0);
					}});
                
                
            }
        }
    }

	
	
	public static boolean isAccessibilityEnabled(Context context, String id) {

	    AccessibilityManager am = (AccessibilityManager) context
	            .getSystemService(Context.ACCESSIBILITY_SERVICE);
	    List<AccessibilityServiceInfo> runningServices = am
	            .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
	    for (AccessibilityServiceInfo service : runningServices) {
	    	//`Log.i("CANV_K9MAIL","acc: "+service.getId());
	        if (service.getId().startsWith(id)) {
	            return true;
	        }
	    }
	    return false;
	}
	
}
