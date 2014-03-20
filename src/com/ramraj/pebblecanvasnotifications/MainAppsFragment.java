package com.ramraj.pebblecanvasnotifications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class MainAppsFragment extends Fragment implements DialogInterface.OnClickListener {
	private Activity context;
	 private ListView lv;
	 private View rootView;
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_main_applist, container, false);
         
        return rootView;
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Make sure that we are currently visible
            // If we are becoming invisible, then...
            if (this.isResumed() && isVisibleToUser) {
                onResume();
                // TODO stop audio playback
            }
      
    }
public void onResume() {
	
	super.onResume();
	context = getActivity();
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	//context = getApplicationContext();
	if (prefs.contains(NotificationSourceList.BLACK_LIST_PREF_NAME)) {
		//class initialized from an activity, show dialog.
		new AlertDialog.Builder(context)
	    .setTitle(context.getString(R.string.alert_applist_policy_migrate_title))
	    .setMessage(context.getString(R.string.alert_applist_policy_migrate_subject))
	    .setNegativeButton(context.getString(R.string.alert_applist_policy_migrate_option_keep),this)
	    .setPositiveButton(context.getString(R.string.alert_applist_policy_migrate_option_discard),this) 
	    .setCancelable(true).show();

	}
	 final NotificationSourceList notificationSourceList = new NotificationSourceList(context,context);
	    Set<String> fullList = notificationSourceList.getFullProgramList();
	    Set<String> whiteList = notificationSourceList.getWhiteListedProgramList();
	    //setContentView(R.layout.activity_main);
	    
	    
		

	    
	    
	    CheckBox enabled= (CheckBox)rootView.findViewById(R.id.checkBoxEnablePebblePlusPlus);
		//if (enabled!=null)
	    enabled.setChecked(prefs.getBoolean(NowPlayingPlugin.SERVICE_WANTED,true));	    
		    enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
					prefs.edit().putBoolean(NowPlayingPlugin.SERVICE_WANTED, arg1).commit();
				}});
		
		    

        lv = (ListView) rootView.findViewById(R.id.listView1);
        // Instanciating an array list (you don't need to do this, you already have yours)
        // This is the array adapter, it takes the context of the activity as a first // parameter, the type of list view as a second parameter and your array as a third parameter
        String[] temparr1=fullList.toArray(new String[fullList.size()]);        
        
        
        final PackageManager pm = context.getPackageManager();
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
        new ArrayAdapter<String>(context,android.R.layout.simple_list_item_multiple_choice,(String[]) listWithNames.toArray());
        if(lv.getFooterViewsCount()==0) {
        TextView tv=new TextView(context);
        tv.setText(R.string.settings_app_list_will_become_longer);
        
        lv.addFooterView(tv);
        }
        lv.setAdapter(arrayAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayList<View> allchildren = lv.getFocusables(View.FOCUSABLES_ALL);
        for (int l=0;l<list.size();l++) {
        	if(whiteList.contains(list.get(l))) {
        		lv.setItemChecked(l, true);
        	} 
        }
     // listener for the first one 
        lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
						// TODO Auto-generated method stub
						//`Log.i("CANV_K9MAIL","user clicked"+arg2+" - "+arg3);
						ListView vv = (ListView)arg0;
						boolean itemSelectionStatus = (vv.isItemChecked(arg2));
						vv.setItemChecked(arg2,itemSelectionStatus);
						if (itemSelectionStatus) {
							// item has been checked as wanted
							notificationSourceList.addProgramToWhiteList(list.get(arg2));
						} else {
							// blacklist it
							notificationSourceList.removeProgramFromWhiteList(list.get(arg2));
						}
							
					}

            });
       
    new CheckAccessibilityInBG(context).execute("");
    if (!prefs.contains("firstNTimes"))  prefs.edit().putInt("firstNTimes", 1).commit();
    int firstNTimes =prefs.getInt("firstNTimes", 4); 
    if (firstNTimes<4) {
    	
    	
		View linearLayout =  rootView.findViewById(R.id.errorContainers);
    //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
	linearLayout.setBackgroundColor(Color.YELLOW);
    TextView valueTV = new TextView(context);
    valueTV.setText(R.string.new_add_icons);
    valueTV.setTextColor(Color.BLACK);
    valueTV.setId(5);
    valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    ((LinearLayout) linearLayout).addView(valueTV);
    prefs.edit().putInt("firstNTimes", firstNTimes+1).apply();
    }
        
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
    		View linearLayout =  rootView.findViewById(R.id.errorContainers);
            //LinearLayout layout = (LinearLayout) findViewById(R.id.info);
    		linearLayout.setBackgroundColor(Color.RED);
            TextView valueTV = new TextView(myCtx);
            valueTV.setText(R.string.switch_on_accessibility_message);
            valueTV.setId(5);
            valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            ((LinearLayout) linearLayout).removeAllViews();
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


	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch(which) {
        case DialogInterface.BUTTON_POSITIVE:
      	  (new NotificationSourceList(context,null)).discardOldBlackList();
            
            Toast.makeText(context, "Old app-list discarded",Toast.LENGTH_LONG).show();
            onResume();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
      	  (new NotificationSourceList(context,null)).keepOldBlackList();
            Toast.makeText(context, "Old app-list carried-forward",Toast.LENGTH_LONG).show();
            onResume();
            break;
        case DialogInterface.BUTTON_NEUTRAL:
            //Toast.makeText(context, "",Toast.LENGTH_LONG).show();
            break;
}

	}

}
