package com.ramraj.pebblecanvasnotifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import com.pennas.pebblecanvas.plugin.PebbleCanvasPlugin;
import com.ramraj.pebblecanvasnotifications.R;

public class NowPlayingPlugin extends PebbleCanvasPlugin {
	public static final String LOG_TAG = "CANV_K9MAIL";
	
	private static final int ID_NEW_EMAIL = 1;
	private static final int ID_NOTIFICATION_ICON = 2;
	private static final int ID_NOTIFICATION_ICON2 = 3;
	private static final int ID_NOTIFICATION_ICON3 = 4;
	
	private static final String[] MASKS = { "%T", "%S","%s","%F", "%Y","%D","%d", "%G","%R","%A","%a","%H"};//,"pkid1","pkidarray[2]"};
	private static final List<String> MASKS_LIST = Arrays.asList(MASKS);
	private static final int[] MASK_TITLES = {-1, 0, 4, 8};
	private static final int[] MASK_SUBJECTS = {-1, 1,5,9};
	private static final int[] MASK_SUBJECTBS ={-1, 2,6,10};	
	private static final int[] MASK_FROMS = {-1,3,7,11};
	private static final String[] MASK_PKIDS = {"","pkid1","pkid2","pkid3"};
	
	public static final String SERVICE_WANTED = "serviceWanted";
	


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
		examples.add(current_track.fromarray[1]);
		examples.add(current_track.subjectarray[1]);
		examples.add(current_track.pknamearray[1]);
		examples.add(current_track.fromarray[2]);
		examples.add(current_track.subjectarray[2]);
		examples.add(current_track.pknamearray[2]);
		examples.add(current_track.fromarray[3]);
		examples.add(current_track.subjectarray[3]);
		examples.add(current_track.pknamearray[3]);
		tplug.format_mask_examples = examples;
		tplug.format_masks = new ArrayList<String>(Arrays.asList(MASKS));
		tplug.default_format_string = "%T - %S";
		plugins.add(tplug);
		
		ImagePluginDefinition iplug = new ImagePluginDefinition();
		iplug.id = ID_NOTIFICATION_ICON;
		iplug.name = context.getString(R.string.plugin_name_notification_icon);
		plugins.add(iplug);
		
		ImagePluginDefinition iplug2 = new ImagePluginDefinition();
		iplug2.id = ID_NOTIFICATION_ICON2;
		iplug2.name = context.getString(R.string.plugin_name_notification_icon2);
		plugins.add(iplug2);
	
		ImagePluginDefinition iplug3 = new ImagePluginDefinition();
		iplug3.id = ID_NOTIFICATION_ICON3;
		iplug3.name = context.getString(R.string.plugin_name_notification_icon3);
		plugins.add(iplug3);
		
		
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
		Map map = prefs.getAll();
		for (int i=1;i<4;i++) {
			current_track.pknamearray[i] = (String)map.get(MASKS[MASK_FROMS[i]]);
			current_track.fromarray[i] = (String)map.get(MASKS[MASK_TITLES[i]]);
			current_track.subjectarray[i] = (String)map.get(MASKS[MASK_SUBJECTS[i]]);
			current_track.subjectbarray[i] = (String)map.get(MASKS[MASK_SUBJECTBS[i]]);
			current_track.pkidarray[i] = (String)map.get(MASK_PKIDS[i]);
			try {
				Drawable icon = context.getPackageManager().getApplicationIcon(current_track.pkidarray[i]);
				current_track.iconarray[i]= NotificationService.drawableToBitmap(icon);
			} catch(Exception ex) { current_track.iconarray[i]=null;}

		}
		
		
		
	    
	}
	
	private static boolean save_to_prefs(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
for(int i=1;i<4;i++) {
		prefs.edit().putString(MASKS[MASK_FROMS[i]], current_track.pknamearray[i]);
		prefs.edit().putString(MASKS[MASK_SUBJECTS[i]], current_track.subjectarray[i]);
		prefs.edit().putString(MASKS[MASK_SUBJECTBS[i]], current_track.subjectbarray[i]);
		prefs.edit().putString(MASKS[MASK_TITLES[i]], current_track.fromarray[i]);
		prefs.edit().putString(MASK_PKIDS[i], current_track.pkidarray[i]);
}
prefs.edit().commit();
				/*Bitmap realImage = current_track.iconarray[1];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);   
		byte[] b = baos.toByteArray(); 

		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		//textEncode.setText(encodedImage);

		
		
		prefs.edit().putString("image_data",encodedImage).commit();*/
		return(prefs.getBoolean(SERVICE_WANTED, true));

		
	}


	private static Track current_track = new Track();
	
	
	public static class Track {
		String[] fromarray,subjectarray,subjectbarray,pknamearray,pkidarray;
		Bitmap[] iconarray;
		String from,subject,subjectb,pkname,pkid;
		Bitmap icon;
		boolean importantApp;
		public Track() {
			fromarray = new String[4];
			subjectarray = new String[4];
			subjectbarray = new String[4];
			pknamearray = new String[4];
			pkidarray = new String[4];
			iconarray = new Bitmap[4];
			
		}
	}
	private static String copyToPosition(Track track,int from,int to) {
		String sourcepkname = track.pknamearray[to];
		track.subjectarray[to]= track.subjectarray[from];
		track.fromarray[to] = track.fromarray[from];
		track.pknamearray[to] = track.pknamearray[from];
		track.iconarray[to] = track.iconarray[from];
		track.pkidarray[to] = track.pkidarray[from];
	    return sourcepkname;
	}
	
	
	public static boolean set_notification_details(Context context,Track track) {
		got_now_playing=true;
		
		current_track.subjectarray[0]= track.subject;
		current_track.fromarray[0] = track.from;
		current_track.pknamearray[0] = track.pkname;
		current_track.iconarray[0] = track.icon;
		current_track.pkidarray[0] = track.pkid;
		
		/*
		if (!track.pknamearray[1].equals(current_track.pknamearray[1])) {
			current_track.fromarray[2]=current_track.fromarray[1];
			current_track.subjectarray[2] = current_track.subjectarray[1];
			current_track.pknamearray[2] = current_track.pknamearray[1];
			current_track.iconarray[2]=current_track.iconarray[1];
			current_track.pkidarray[2] = current_track.pkidarray[1];
			notify_canvas_updates_available(ID_NOTIFICATION_ICON2, context);
		}
		current_track.subjectarray[1]= track.subjectarray[1];
		current_track.fromarray[1] = track.fromarray[1];
		current_track.pknamearray[1] = track.pknamearray[1];
		current_track.iconarray[1] = track.iconarray[1];
		current_track.pkidarray[1] = track.pkidarray[1];*/
		if(track.importantApp) {
			if (track.pkname.equals(current_track.pknamearray[1]))
				copyToPosition(current_track,0,1);				
			else if (track.pkname.equals(current_track.pknamearray[2])) {
				copyToPosition(current_track,1,2);
				copyToPosition(current_track,0,1);
			} else {
				copyToPosition(current_track,2,3);
				copyToPosition(current_track,1,2);
				copyToPosition(current_track,0,1);
			}
			
		} else {
			if (track.pkname.equals(current_track.pknamearray[2])) 
				copyToPosition(current_track,0,2);
			 else  {
				copyToPosition(current_track,2,3);
				copyToPosition(current_track,0,2);
			}		
		}
		

		//Log.i("Canvas notifi", "request check for bitmap update");
		for (int i=1;i<4;i++) {
	        if(current_track.subjectarray[i]== null) 
	        	current_track.subjectbarray[i] = null;
	        else {
			String splits[] = current_track.subjectarray[i].split("[\r\n]+");
			if (splits.length>1){
				current_track.subjectarray[i] = splits[0];
				current_track.subjectbarray[i] = splits[1];}
				else 
					current_track.subjectbarray[i]=" ";
	        }
		}
                
		
				

		notify_canvas_updates_available(ID_NEW_EMAIL, context);
		notify_canvas_updates_available(ID_NOTIFICATION_ICON, context);
		notify_canvas_updates_available(ID_NOTIFICATION_ICON2, context);
		notify_canvas_updates_available(ID_NOTIFICATION_ICON3, context);

		
		return save_to_prefs(context);
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
				int maskIndex = MASKS_LIST.indexOf(format_mask);
				if(maskIndex!=-1)
					for(int i=1;i<4;i++) {
						if(maskIndex==MASK_FROMS[i]) 
							return current_track.pknamearray[i];
						if(maskIndex==MASK_SUBJECTS[i])
							return current_track.subjectarray[i];
						if(maskIndex==MASK_SUBJECTBS[i])
							return current_track.subjectbarray[i];
						if(maskIndex==MASK_TITLES[i])
							return current_track.fromarray[i];
							
					}
				}
			
		return null;
	}

	@Override
	protected Bitmap get_bitmap_value(int def_id, Context context, String param) {
		if (def_id== ID_NOTIFICATION_ICON) {
			return current_track.iconarray[1];
		} 
		if (def_id== ID_NOTIFICATION_ICON2) {
			return current_track.iconarray[2];
		}
		if (def_id== ID_NOTIFICATION_ICON3) {
			return current_track.iconarray[3];
		}

		return null;
	}
	
}
