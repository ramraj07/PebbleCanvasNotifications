package com.ramraj.pebblecanvasnotifications;
import com.ramraj.pebblecanvasnotifications.NotificationSourceList;
import com.ramraj.pebblecanvasnotifications.NowPlayingPlugin;
import com.ramraj.pebblecanvasnotifications.NowPlayingPlugin.Track;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.ImageView;


public class NotificationService extends AccessibilityService {

	private String toWrite;
	private NotificationSourceList notificationSourceList; 
	private Track notifiDetails = new Track();
	private boolean serviceWanted=true;
	
	private Bitmap bitmapGlobal = null;
	
	
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
	
//    private PebbleKit.PebbleDataReceiver dataReceiver;
//    private PebbleKit.PebbleAckReceiver ackReceiver;
//    private PebbleKit.PebbleNackReceiver nackReceiver;
//    private final UUID watchAppUUID = UUID.fromString("42c86ea4-1c3e-4a07-b889-2cccca914198");
	
	
/////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}

	
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		if (!serviceWanted) return;
		if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			
			//Log.i("CANV_K9MAIL", "notification: " + event.getText());
			String pkname = (String) event.getPackageName();
			if (notificationSourceList.checkIfProgramWhiteListed(pkname)) {
								
				toWrite="===============================\npackage: "+pkname +
						"\n";
			    	
			     
				
				// following code "adapted" from 
				// http://stackoverflow.com/questions/9292032/extract-notification-text-from-parcelable-contentview-or-contentintent
				
				
				
				Notification notification = (Notification) event.getParcelableData();
				if (notification== null) return;				
				//if (!((notification.flags & Notification.FLAG_NO_CLEAR) == 0)) return;// we don't want persistent notifications clouding the stream.
				
				
				String finalNotifSource,notifContents,notifTitle;
									try {
			    RemoteViews views= notification.bigContentView;
			    if (views==null) views = notification.contentView;
			    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	ViewGroup localView = (ViewGroup) inflater.inflate(views.getLayoutId(), null);												    
			    views.reapply(getApplicationContext(), localView);			    
				//`Log.i("CANV_K9MAIL", toWrite);
				notifTitle=getTextRecursively(localView,"title","",false);
				if(notification.number>1 && 
						notifTitle.toLowerCase().indexOf("new messages")!=-1) 
					notifContents=getTextRecursively(localView,"text","\n",false);
				else notifContents=getTextRecursively(localView,"text","\n",false);
				//bitmapGlobal = getIconRecursively(localView);//bitmapGlobal;
				toWrite = notifTitle+"\n"+notifContents;
									
				if (pkname.equals("com.google.android.googlequicksearchbox")) {
					finalNotifSource="Search";					
				} else if (pkname.equals("com.google.android.apps.maps")) {
					finalNotifSource="Maps";					
				} else if (pkname.equals("com.android.email")) {
					finalNotifSource="Email";
					if (notification.number>1) toWrite = notifContents;
						
				} else if (pkname.equals("com.google.android.talk")) {
					finalNotifSource="Talk";
				} else if (pkname.equals("com.google.android.gm")) {
					finalNotifSource="Gmail";
					if (notification.number>1) toWrite = notifContents;					
				} else if (pkname.equals("com.whatsapp")) {
					finalNotifSource="WhatsApp";
				} else {
					   
					try {
						final PackageManager pm = getApplicationContext().getPackageManager();
				        ApplicationInfo ai;				     
						ai = pm.getApplicationInfo( pkname, 0);
						finalNotifSource = (String) pm.getApplicationLabel(ai);
		            } catch (final NameNotFoundException e) {
		            	finalNotifSource="Other";
		            }
					
					
				}
									} catch(Exception ex) {
										//`Log.i("CANV_K9MAIL","ERRRRRRRRRRRRRRORRRRRRRRRRRRRRR in inflating layout");
										return;
									}
									try {
									Drawable icon = getApplicationContext().getPackageManager().getApplicationIcon(pkname);
									notifiDetails.icon = drawableToBitmap(icon);
									}
									catch(Exception ex)
									{
										notifiDetails.icon=null;
										}
								
									
				notifiDetails.from=notifTitle;
				notifiDetails.subject = notifContents;
				notifiDetails.pkid = pkname;
				notifiDetails.pkname = finalNotifSource;
				if (bitmapGlobal!=null) bitmapGlobal=null;
				serviceWanted = NowPlayingPlugin.set_notification_details(getApplicationContext(), notifiDetails);
				
				
				//if (toWrite.length()>100)toWrite=toWrite.substring(0,100);
				/*Intent msgIntent = new Intent(this, SendNotificationsToPebbleService.class);
				msgIntent.putExtra(SendNotificationsToPebbleService.PARAM_IN_NOTIF, toWrite);
				msgIntent.putExtra(SendNotificationsToPebbleService.PARAM_IN_APPSOURCE, finalNotifSource);
				startService(msgIntent);*/
			 }
	    }
	}
	String truncateToWatchFaceWidth(String input,int widthToFit) {
		final byte[] ASCIIwidths = new byte[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,2,-1,-1,8,14,11,
				-1,5,5,-1,-1,2,4,2,2,8,6,8,8,9,8,8,8,9,9,7,2,8,8,8,2,7,10,9,8,8,7,7,8,8,
				2,4,8,7,10,9,8,8,8,9,8,8,8,9,10,8,8,8,4,7,4,-1,10,-1,10,9,8,8,7,7,8,8,2,
				4,8,7,10,9,8,8,8,9,8,8,8,9,10,8,8,8,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
				-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 2,-1,-1,-1,-1,-1,-1,-1,
				-1};
		final int maxchars = (int) Math.floor((double)widthToFit/(2+1));// 2 is the width of a fullstop and 1 is the space padding after it
		boolean previousWasSpace=false;
		int counter=0,charInInt; 
		char[] inputArray;
		inputArray= input.toCharArray();
		char[] outputArray = new char[maxchars];
		int totalWidth=0;		
		for (int l=0;l<inputArray.length;l++) {
			charInInt = (int)inputArray[l];
			
			if (charInInt<256 && ASCIIwidths[charInInt]!=-1) {
					if (charInInt==32) {
						if (previousWasSpace) continue;	
						previousWasSpace=true;
					} else previousWasSpace=false;
					totalWidth+=ASCIIwidths[charInInt]+1;
					
					if (totalWidth>widthToFit) break;
					outputArray[counter++]=inputArray[l];
					
			}
		}	 
		if (counter>1) return(String.valueOf(outputArray).substring(0,counter));
		else return("");
	}
	Bitmap getIconRecursively(ViewGroup parent) {
		for(int i = 0; i < parent.getChildCount(); i++)
	    {
	        View child = parent.getChildAt(i);            
	        if(child instanceof ViewGroup) 
	        {
	        	 return getIconRecursively((ViewGroup)child);
	        }
	        else if(child instanceof ImageView) {
	        	//String tag = ((ImageView) child).getTag();
	        	String txtId = ((ImageView) child).toString();	        	
	        	try 
	        	{
			        if (txtId.substring(txtId.indexOf("android:id")).indexOf("icon")!=-1) {		    	        
			        	ImageView casted = ((ImageView) child);
			        	//casted.buildDrawingCache();
			        	return ((BitmapDrawable)casted.getDrawable()).getBitmap();
			        }
	        	} 
	        	finally {}	        	
	        }
	    }
		return null;
	    
	}
	String getTextRecursively(ViewGroup parent,String idFilter,String trailingCharacter,boolean truncate) 
	{    
		String result="",resultText,truncatedResultText;
		boolean firstImageNotEncountered = true;
		//`Log.i("CANV_BITMAP", "notification parsing");
	    for(int i = 0; i < parent.getChildCount(); i++)
	    {
	        View child = parent.getChildAt(i);            
	        if(child instanceof ViewGroup) 
	        {
	        	result+=getTextRecursively((ViewGroup)child,idFilter,trailingCharacter,truncate);
	        }
	        else if(child instanceof TextView)
	        {
	        	String txtId = ((TextView) child).toString();	        	
	        	try {
		        	//txtId.substring(txtId.indexOf("android:id/"))
		        	//String subText = txtId.substring(txtId.indexOf("android:id/")-10);
			        if (txtId.substring(txtId.indexOf("android:id")).indexOf(idFilter)!=-1) { 
			        	// see if the id of this textview contains the word "text"
			        	
			        	resultText=(((TextView) child).getText().toString());
			        	//if (resultText.length()>truncateLength) result+=resultText.substring(0,truncateLength-1)+trailingCharacter;
			        	//else result+=resultText;
			        	if(truncate) truncatedResultText =truncateToWatchFaceWidth(resultText,143);
			        	else truncatedResultText =resultText;
			        	if (truncatedResultText.length()>1) result+=truncatedResultText+trailingCharacter;
			        }
	        	}
	        	finally
	        	{
	        		
	        	}
	            
	        }
	     
	    }
	    return(result);
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onServiceConnected() {
	    AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
	    info.feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC;
	    info.notificationTimeout = 100;
	    setServiceInfo(info);
	    notificationSourceList = new NotificationSourceList(getApplicationContext(),null);
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    serviceWanted = prefs.getBoolean(NowPlayingPlugin.SERVICE_WANTED, true);
	    
	}

}
