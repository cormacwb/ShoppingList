package com.android.shoppinglist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.telephony.SmsManager;

public class SMSHelper {
	
	private static int SMS_LENGTH = 160;
	
	public static int getSMSLength(){
		return SMS_LENGTH;
	}

	public void sendSMS(String phoneNumber, String message){
    	SmsManager smsManager = SmsManager.getDefault();    	
    	List<String> messages = splitStringIntoSMSSizedSegments(message);
    	
    	for(Iterator<String>s = messages.iterator(); s.hasNext();){
    		smsManager.sendTextMessage(phoneNumber, null, s.next(), null, null);
    	}
    }
	
	private List<String> splitStringIntoSMSSizedSegments(String message){		
		List<String> output = new ArrayList<String>();
		if(message.length() <= SMS_LENGTH){
			output.add(message);			
		}
		else{
			String[] separatedItems = message.split(",");
			String currentMessage = "";
			
			for(String item: separatedItems){
				String potentialMessage = currentMessage + "," + item;
				if(potentialMessage.length() > SMS_LENGTH){
					if(currentMessage.length() > 0)
						currentMessage = currentMessage.substring(1);
					
					output.add(currentMessage);
					currentMessage = "";
				}
				else{					
					currentMessage = potentialMessage;
				}
			}
			
			if(currentMessage.length() > 0)
				currentMessage = currentMessage.substring(1);
			
			output.add(currentMessage);

		}		
		return output;
	}
}
