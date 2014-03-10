package com.ramraj.pebblecanvasnotifications;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSourceList {
			
	private Set<String> programBlackList;
	private Set<String> programFullList;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefEditor;
	private final String FULL_LIST_PREF_NAME="programFullList";
	private final String BLACK_LIST_PREF_NAME="programBlackList";
	private boolean initialized=false;
	public NotificationSourceList(Context context) {
		// new instance is being requested. check first if sharedprefs has any list saved
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefEditor = prefs.edit();
		if (!(prefs.contains("programListSaved") && 
				prefs.contains(FULL_LIST_PREF_NAME) &&
				prefs.contains(BLACK_LIST_PREF_NAME) )) {
			// first time. add a bunch of programs blacklisted by default to the prefs.
			programBlackList = new HashSet<String>(Arrays.asList(new String[] { 
					"net.dinglisch.android.taskerm",
					"com.gvoip",
					"com.android.vending",					
					"ch.bitspin.timely",
					"com.getpebble.android",
					"com.ttxapps.dropsync",
					"com.android.providers.downloads"}));  
			prefEditor.putStringSet(BLACK_LIST_PREF_NAME, programBlackList);
			programFullList = new HashSet<String>(Arrays.asList(new String[] { 
					"com.google.android.gm",
					"com.android.phone",
					"com.google.android.talk"}));
			prefEditor.putStringSet(FULL_LIST_PREF_NAME,programFullList);
			prefEditor.putString("programListSaved","oh yes").commit();
			prefEditor.commit();
		} else {
			programFullList = prefs.getStringSet(FULL_LIST_PREF_NAME,new HashSet<String>());
			programBlackList = prefs.getStringSet(BLACK_LIST_PREF_NAME, new HashSet<String>());
			
		}
		initialized=true;
	}
	
	public boolean checkIfProgramBlackListed(String programName) {
		if (programBlackList.contains(programName))	{
			if (!programFullList.contains(programName)) {
				// app is on the default black list but now needs
				// to be explicitly added to the full list
				programFullList.add(programName);
				prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();				
			}				
			return(true);
		} else if (programFullList.contains(programName)) 	return(false);
		else {
			//first time!
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();
			return(false);		
		}		
	}
	public Set<String> getFullProgramList() {
		Set<String> programListCopy = new HashSet<String>(programFullList);
		return(programListCopy);
	}
	public Set<String> getBlackListedProgramList() {
		Set<String> programBlackListCopy = new HashSet<String>(programBlackList);
		programBlackListCopy.retainAll(programFullList);
		return(programBlackListCopy);
	}
	public Set<String> getWhiteListedProgramList() {
		Set<String> programWhiteList = new HashSet<String>(programFullList);
		programWhiteList.removeAll(programBlackList);
		return(programWhiteList);
	}
	public void addProgramToBlackList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();
		}
		if (!programBlackList.contains(programName)) {
			programBlackList.add(programName);
			prefEditor.putStringSet(BLACK_LIST_PREF_NAME, programBlackList).commit();
		}
	}
	public void removeProgramFromBlackList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();
			return;
		}
		if (programBlackList.contains(programName)) {
			programBlackList.remove(programName);
			prefEditor.putStringSet(BLACK_LIST_PREF_NAME, programBlackList).commit();
		}
	}
}
