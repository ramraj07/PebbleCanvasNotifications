package com.ramraj.pebblecanvasnotifications;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSourceList {
			
	private Set<String> programWhiteList;
	private Set<String> programFullList;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefEditor;
	private final String FULL_LIST_PREF_NAME="programFullList";
	private final String WHITE_LIST_PREF_NAME="programWhiteList";
	private boolean initialized=false;
	public NotificationSourceList(Context context) {
		// new instance is being requested. check first if sharedprefs has any list saved
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefEditor = prefs.edit();
		if (!(prefs.contains("programListSaved") && 
				prefs.contains(FULL_LIST_PREF_NAME) &&
				prefs.contains(WHITE_LIST_PREF_NAME) )) {
			// first time. add a bunch of programs whitelisted by default to the prefs.
			programWhiteList = new HashSet<String>(Arrays.asList(new String[] { 
					"com.google.android.gm",
					"com.android.phone",
					"com.google.android.talk",
					"com.google.android.apps.maps",
					"com.nitrodesk.droid20.nitroid",
					"com.google.android.calendar",
					"com.whatsapp",
					"com.viber.voip",
					"com.skype.raider",
					"com.fsck.k9",
					"com.facebook.katana",
					"com.facebook.orca",
					"com.google.android.googlequicksearchbox",
					"com.google.android.apps.plus",
					"com.google.android.apps.googlevoice"
					}));  
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList);
			programFullList = new HashSet<String>(Arrays.asList(new String[] { 
					"com.google.android.gm",
					"com.android.phone",
					"com.google.android.talk"}));
			prefEditor.putStringSet(FULL_LIST_PREF_NAME,programFullList);
			prefEditor.putString("programListSaved","oh yes").commit();
			prefEditor.commit();
		} else {
			programFullList = prefs.getStringSet(FULL_LIST_PREF_NAME,new HashSet<String>());
			programWhiteList = prefs.getStringSet(WHITE_LIST_PREF_NAME, new HashSet<String>());
			
		}
		initialized=true;
	}
	
	public boolean checkIfProgramWhiteListed(String programName) {
		if (programWhiteList.contains(programName))	{
			if (!programFullList.contains(programName)) {
				// app is on the default white list but now needs
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
	/*public Set<String> getBlackListedProgramList() {
		dSet<String> programWhiteListCopy = new HashSet<String>(programWhiteList);
		programWhiteListCopy.retainAll(programFullList);
		return(programWhiteListCopy);
	}*/
	public Set<String> getWhiteListedProgramList() {
		//Set<String> programWhiteList = new HashSet<String>(programFullList);
		//programWhiteList.removeAll(programWhiteList);
		return(programWhiteList);
	}
	public void addProgramToWhiteList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();
		}
		if (!programWhiteList.contains(programName)) {
			programWhiteList.add(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList).commit();
		}
	}
	public void removeProgramFromWhiteList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			programWhiteList.remove(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList).commit();
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).commit();
			return;
		}
		if (programWhiteList.contains(programName)) {
			programWhiteList.remove(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList).commit();
		}
	}
}
