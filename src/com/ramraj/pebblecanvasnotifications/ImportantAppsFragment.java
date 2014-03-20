package com.ramraj.pebblecanvasnotifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ImportantAppsFragment extends Fragment {
	private Activity context;
	private View rootView;
	private ListView lv;
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        rootView = inflater.inflate(R.layout.fragment_important_applist, container, false);
	         
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
		final NotificationSourceList notificationSourceList = new NotificationSourceList(context,context);
		Set<String> fullList = notificationSourceList.getFullProgramList();
		Set<String> whiteList = notificationSourceList.getWhiteListedProgramList();
	    Set<String> importantList = notificationSourceList.getImportantProgramList();
	    
		 CheckBox importantEnabled= (CheckBox)rootView.findViewById(R.id.checkBoxEnableStarredApps);
			//if (enabled!=null)
		 importantEnabled.setChecked(prefs.getBoolean(
				 NotificationSourceList.USE_IMPORANT_LIST_PREF_NAME,false));	    
		 importantEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
						prefs.edit().putBoolean(NotificationSourceList.USE_IMPORANT_LIST_PREF_NAME, arg1).commit();
					}});
	
		 
	        lv = (ListView) rootView.findViewById(R.id.listView1a);
	        // Instanciating an array list (you don't need to do this, you already have yours)
	        // This is the array adapter, it takes the context of the activity as a first // parameter, the type of list view as a second parameter and your array as a third parameter
	        whiteList.retainAll(fullList);
	        String[] temparr1=whiteList.toArray(new String[whiteList.size()]);        
	        
	        
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
	   
	        
	        lv.setAdapter(arrayAdapter);
	        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        ArrayList<View> allchildren = lv.getFocusables(View.FOCUSABLES_ALL);
	        for (int l=0;l<list.size();l++) {
	        	if(importantList.contains(list.get(l))) {
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
								notificationSourceList.addProgramToImportantList(list.get(arg2));
							} else {
								// blacklist it
								notificationSourceList.removeProgramFromImportantList(list.get(arg2));
							}
								
						}

	            });

	}

}
