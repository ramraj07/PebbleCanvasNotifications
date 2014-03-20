package com.ramraj.pebblecanvasnotifications;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationSourceList {
			
	private Set<String> programWhiteList;
	private Set<String> programFullList;
	private Set<String> programImportantList;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefEditor;
	public static final String FULL_LIST_PREF_NAME="programFullList";
	public static final String WHITE_LIST_PREF_NAME="programWhiteList";
	public static final String BLACK_LIST_PREF_NAME="programBlackList";
	public static final String IMPORTANT_LIST_PREF_NAME = "programImportantList";
	public static final String USE_IMPORANT_LIST_PREF_NAME = "importantListEnabled";
	public static final String PROGRAM_LIST_SAVED_PREF_NAME = "programListSaved";

	private boolean initialized=false;
	public NotificationSourceList(Context context,Activity callingActivity) {
		// new instance is being requested. check first if sharedprefs has any list saved
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefEditor = prefs.edit();
		if (prefs.contains(BLACK_LIST_PREF_NAME)) {
			if (callingActivity ==null) {
				// means the class was initialized from a service				
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);									
				// build notification
				// the addAction re-use the same intent to keep the example short
				Notification n  = new Notification.Builder(context)
				        .setContentTitle(context.getString(R.string.notification_applist_policy_migrate_title))
				        .setContentText(context.getString(R.string.notification_applist_policy_migrate_subject))
				        .setTicker(context.getString(R.string.notification_applist_policy_migrate_subject))
				        .setSmallIcon(R.drawable.ic_pebblecanvasnotifications)
				        .setContentIntent(pIntent)
				        .setAutoCancel(false).build();		        			  
				NotificationManager notificationManager = 
				  (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);			
				notificationManager.notify(0, n); 
			}
		}
		if (!(prefs.contains(PROGRAM_LIST_SAVED_PREF_NAME) && 
				prefs.contains(FULL_LIST_PREF_NAME) &&
				(prefs.contains(WHITE_LIST_PREF_NAME) ||prefs.contains(BLACK_LIST_PREF_NAME))  )) {
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
			prefEditor.putString(PROGRAM_LIST_SAVED_PREF_NAME,"oh yes");
			prefEditor.apply();
		} else {
			programFullList = prefs.getStringSet(FULL_LIST_PREF_NAME,new HashSet<String>());
			programWhiteList = prefs.getStringSet(WHITE_LIST_PREF_NAME, new HashSet<String>());
			
		}
		programImportantList = prefs.getStringSet(IMPORTANT_LIST_PREF_NAME, new HashSet<String>());
		initialized=true;
	}
	
	public boolean checkIfProgramWhiteListed(String programName) {
		if (programWhiteList.contains(programName))	{
			if (!programFullList.contains(programName)) {
				// app is on the default white list but now needs
				// to be explicitly added to the full list
				programFullList.add(programName);
				prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();				
			}				
			return(true);
		} else if (programFullList.contains(programName)) 	return(false);
		else {
			//first time!
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();
			return(false);		
		}		
	}
	public boolean checkIfProgramIsImportant(String programName) {
		if(prefs.getBoolean(USE_IMPORANT_LIST_PREF_NAME, false)) {
			if(programImportantList.contains(programName)) return true;
			else return false;				
		}
		else return true;
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
	public Set<String> getImportantProgramList() {
		return(programImportantList);
	}
	public void addProgramToWhiteList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();
		}
		if (!programWhiteList.contains(programName)) {
			programWhiteList.add(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList).apply();
		}
	}
	public void removeProgramFromWhiteList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			programWhiteList.remove(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();
			return;
		}
		if (programWhiteList.contains(programName)) {
			programWhiteList.remove(programName);
			prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList).apply();
		}
	}
	public void addProgramToImportantList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();
		}
		if (!programImportantList.contains(programName)) {
			programImportantList.add(programName);
			prefEditor.putStringSet(IMPORTANT_LIST_PREF_NAME, programImportantList).apply();
		}
	}
	public void removeProgramFromImportantList(String programName) {
		if (!programFullList.contains(programName)) {
			programFullList.add(programName);
			programImportantList.remove(programName);
			prefEditor.putStringSet(IMPORTANT_LIST_PREF_NAME, programImportantList);
			prefEditor.putStringSet(FULL_LIST_PREF_NAME, programFullList).apply();
			return;
		}
		if (programImportantList.contains(programName)) {
			programImportantList.remove(programName);
			prefEditor.putStringSet(IMPORTANT_LIST_PREF_NAME, programImportantList).apply();
		}
	}

	public void keepOldBlackList() {
		if ((prefs.contains(PROGRAM_LIST_SAVED_PREF_NAME) && 
				prefs.contains(FULL_LIST_PREF_NAME) &&
				prefs.contains(BLACK_LIST_PREF_NAME) )) {
				Set<String> programBlackList = prefs.getStringSet(BLACK_LIST_PREF_NAME,new HashSet<String>());
				programWhiteList = programFullList;
				programWhiteList.removeAll(programBlackList);
				prefEditor.putStringSet(WHITE_LIST_PREF_NAME, programWhiteList);
				prefEditor.remove(BLACK_LIST_PREF_NAME).commit();
				
			}
		
	}
	public void discardOldBlackList() {
		prefEditor.remove(BLACK_LIST_PREF_NAME).commit();
	}
	
}
