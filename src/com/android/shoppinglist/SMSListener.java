package com.android.shoppinglist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSListener extends BroadcastReceiver {
	
	private ShoppingListDbAdapter mDbHelper;	

	@Override
	public void onReceive(Context context, Intent intent) {
		String listRequestCode = getRequestCode(context);
		
		if(listRequestCode == "" || listRequestCode == "NULL")
			return;		
		
		SMSHelper helper = new SMSHelper();
    	Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";  
        String phoneNumber = null;
        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += "SMS from " + msgs[i].getOriginatingAddress();                     
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n"; 
                phoneNumber = msgs[i].getOriginatingAddress();
            }
            
            if(str.toUpperCase().contains(listRequestCode.toUpperCase())){
            	mDbHelper = new ShoppingListDbAdapter(context);
                mDbHelper.open();
                String itemsToBuy = mDbHelper.fetchItemsToBuy();
                if(phoneNumber == null)
                	Toast.makeText(context, "Could not read phone number", Toast.LENGTH_SHORT).show();
                helper.sendSMS(phoneNumber, itemsToBuy);
            }
        } 

	}
	
	private String getRequestCode(Context context){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	return sharedPreferences.getString("prefListRequestCode", "NULL");
	}
	
    private boolean containsListRequestCode(Context context, String smsContents){
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	String listRequestCode = sharedPreferences.getString("prefListRequestCode", "NULL");
    	
    	return smsContents.toUpperCase().contains(listRequestCode.toUpperCase());
    }

}
