package com.ramraj.pebblecanvasnotifications;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.pennas.pebblecanvas.plugin.PebbleCanvasPlugin;
import com.ramraj.pebblecanvasnotifications.R;

public class NowPlayingPlugin extends PebbleCanvasPlugin {
	public static final String LOG_TAG = "CANV_K9MAIL";
	
	private static final int ID_NEW_EMAIL = 1;
	private static final int ID_NOTIFICATION_ICON = 2;
	
	private static final String[] MASKS = { "%T", "%S","%s","%F", "%Y","%D","%d", "%G",};
	private static final int MASK_TITLE = 0;
	private static final int MASK_SUBJECT = 1;
	private static final int MASK_SUBJECTB = 2;	
	private static final int MASK_FROM = 3;
	private static final int MASK_TITLE2= 4;
	private static final int MASK_SUBJECT2 = 5;
	private static final int MASK_SUBJECT2B = 6;
	private static final int MASK_FROM2 = 7;
	
	


	  /* rest of your code */

	
	
	// send plugin metadata to Canvas when requested
	@Override
	protected ArrayList<PluginDefinition> get_plugin_definitions(Context context) {
		//`Log.i(LOG_TAG, "get1_plugin_definitions");
		
		// create a list of plugins provided by this app
		ArrayList<PluginDefinition> plugins = new ArrayList<PluginDefinition>();
		
		// new email (text)
		TextPluginDefinition tplug = new TextPluginDefinition();
		tplug.id = ID_NEW_EMAIL;
		tplug.name = context.getString(R.string.plugin_name_now_playing);
		tplug.format_mask_descriptions = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.format_mask_descs)));
		// populate example content for each field (optional) to be display in the format mask editor
		ArrayList<String> examples = new ArrayList<String>();
		examples.add(current_track.from);
		examples.add(current_track.subject);
		examples.add(current_track.pkname);
		examples.add(current_track.from2);
		examples.add(current_track.subject2);
		examples.add(current_track.pkname2);
		tplug.format_mask_examples = examples;
		tplug.format_masks = new ArrayList<String>(Arrays.asList(MASKS));
		tplug.default_format_string = "%T - %S";
		plugins.add(tplug);
		
		/*ImagePluginDefinition iplug = new ImagePluginDefinition();
		iplug.id = ID_NOTIFICATION_ICON;
		iplug.name = context.getString(R.string.plugin_name_notification_icon);
		plugins.add(iplug);*/
				
		return plugins;
	}
	
	private static boolean process_just_started = true;
	private static boolean got_now_playing = false;
	
	// send current text values to canvas when requested
	/*@Override
	protected String get_format_mask_value(int def_id, String format_mask, Context context) {
			return null;
	}*/
	
	// save values to preferences every time they change, because:
	//  - this process might be killed
	//  - values may not be requested by canvas straight away
	//  - will return values on first load
	
	private static void load_from_prefs(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		current_track.pkname = prefs.getString(MASKS[MASK_FROM], null);
		current_track.pkname2 = prefs.getString(MASKS[MASK_FROM2], null);
		current_track.from = prefs.getString(MASKS[MASK_TITLE], null);
		current_track.subject = prefs.getString(MASKS[MASK_SUBJECT], null);
		current_track.subjectb = prefs.getString(MASKS[MASK_SUBJECTB], null);
		current_track.from2 = prefs.getString(MASKS[MASK_TITLE2], null);
		current_track.subject2 = prefs.getString(MASKS[MASK_SUBJECT2], null);
		current_track.subject2b = prefs.getString(MASKS[MASK_SUBJECT2B], null);
		
	    
	    //`Log.i(LOG_TAG, "loaded email from = " + current_track.from+ " subj = "				 + current_track.subject );
	    
	}
	
	private static void save_to_prefs(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putString(MASKS[MASK_FROM], current_track.pkname).commit();
		prefs.edit().putString(MASKS[MASK_SUBJECT], current_track.subject).commit();
		prefs.edit().putString(MASKS[MASK_SUBJECTB], current_track.subjectb).commit();
		prefs.edit().putString(MASKS[MASK_FROM2], current_track.pkname2).commit();
		prefs.edit().putString(MASKS[MASK_SUBJECT2], current_track.subject2).commit();
		prefs.edit().putString(MASKS[MASK_SUBJECT2B], current_track.subject2b).commit();
		prefs.edit().putString(MASKS[MASK_TITLE], current_track.from).commit();
		prefs.edit().putString(MASKS[MASK_TITLE2], current_track.from2).commit();
		
		/*Bitmap realImage = current_track.icon;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);   
		byte[] b = baos.toByteArray(); 

		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		//textEncode.setText(encodedImage);

		
		
		prefs.edit().putString("image_data",encodedImage).commit();*/
		

		
	}


	private static Track current_track = new Track();
	
	
	public static class Track {
		String from,subject,subjectb,from2,subject2,subject2b,pkname,pkname2;
		Bitmap icon;
		/*public Track() {
			from=" ";
			subject = " ";
			subjectb = " ";
			from2 = " ";
			subject2=" ";
			subject2b=" ";
			pkname = " ";
			pkname2= " ";
			
		}*/
	}
	 
	
	
	public static void set_notification_details(Context context,Track track) {
		got_now_playing=true;
		if (track.pkname.equals(current_track.pkname)){} else  {
			current_track.from2=current_track.from;
			current_track.subject2 = current_track.subject;
			current_track.pkname2 = current_track.pkname;
		}
		current_track.subject= track.subject;
		current_track.from = track.from;
		current_track.pkname = track.pkname;
		current_track.icon = track.icon;
        Log.i("HiHiHi",track.from);

		//Log.i("Canvas notifi", "request check for bitmap update");
        if(current_track.subject== null) current_track.subjectb = null;
        else {
		String splits[] = current_track.subject.split("[\r\n]+");
		if (splits.length>1){
			current_track.subject = splits[0];
			current_track.subjectb = splits[1];}
			else 
				current_track.subjectb=" ";
        }
        if( current_track.subject2==null) current_track.subject2b = null;
        else {
		String splits2[] = current_track.subject2.split("[\r\n]+");
		if (splits2.length>1){
			current_track.subject2 = splits2[0];
			current_track.subject2b = splits2[1];}
			else 
				current_track.subject2b=" ";
        }
		
				

		notify_canvas_updates_available(ID_NEW_EMAIL, context);
		//notify_canvas_updates_available(ID_NOTIFICATION_ICON, context);
		save_to_prefs(context);
	}
	
	@Override
	protected String get_format_mask_value(int def_id, String format_mask,
			Context context, String param) {
				if (process_just_started) {
					//`Log.i(LOG_TAG, "process_just_started");
					process_just_started = false;
					if (!got_now_playing) {
						load_from_prefs(context);
					}
				}
				
				if (def_id == ID_NEW_EMAIL) {
					// which field to return current value for?
					//`Log.i(LOG_TAG, "get_format_mask_value id new email" );
					String returnString=null;
					if (format_mask.equals(MASKS[MASK_FROM])) {
						returnString = current_track.pkname;
					} else if (format_mask.equals(MASKS[MASK_SUBJECT])) {
						returnString = current_track.subject;
					} else if (format_mask.equals(MASKS[MASK_SUBJECTB])) {
						returnString = current_track.subjectb;
					} else if (format_mask.equals(MASKS[MASK_FROM2])) {
						returnString = current_track.pkname2;
					} else if (format_mask.equals(MASKS[MASK_SUBJECT2])) {
						returnString = current_track.subject2;
					} else if (format_mask.equals(MASKS[MASK_SUBJECT2B])) {
						returnString = current_track.subject2b;
					} else if (format_mask.equals(MASKS[MASK_TITLE])) {
						returnString = current_track.from;
					} else if (format_mask.equals(MASKS[MASK_TITLE2])) {
						returnString = current_track.from2;
					}
  			///	if (!returnString.isEmpty() && returnString.length()>41 ) return returnString.substring(0,38);
				//	else 
						return returnString;
				}
				//`Log.i(LOG_TAG, "no matching mask found");
			
		return null;
	}

	@Override
	protected Bitmap get_bitmap_value(int def_id, Context context, String param) {
		if (def_id== ID_NOTIFICATION_ICON) {
			
		//	Log.i("Canvas notifi", "bitmap requested");
			return current_track.icon;
		}
		
		return null;
	}
	
}
