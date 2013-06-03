/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.shoppinglist;

import com.android.shoppinglist.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ShoppingList extends ListActivity {
    private static final int NEW_ITEM = Menu.FIRST;
    private static final int SETTINGS_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int CONTEXT_MENU_FEATURE_ID = 6;
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    
    private ShoppingListDbAdapter mDbHelper;
    BroadcastReceiver broadcastReceiver;    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new ShoppingListDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
        broadcastReceiver = new SMSListener();
    }

    private void fillData() {
    	Cursor notesCursor = mDbHelper.fetchAllItems();
        startManagingCursor(notesCursor);
        String[] from = new String[]{ShoppingListDbAdapter.KEY_NAME};
        int[] to = new int[]{R.id.text1};
        ItemCursorAdapter  notes = 
            new ItemCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);        
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, NEW_ITEM , 0,R.string.new_item);
        menu.add(0, SETTINGS_ID, 1, R.string.settings);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
        if (featureId != CONTEXT_MENU_FEATURE_ID) {
			switch (item.getItemId()) {
			case NEW_ITEM:
				showDialog(NEW_ITEM);
				break;
			case SETTINGS_ID:
				Intent intent = new Intent(ShoppingList.this,
						com.android.shoppinglist.Settings.class);
				startActivity(intent);
				break;
			}
		}
		return super.onMenuItemSelected(featureId, item);
    }
    
    public boolean onOptionsItemSelected(){    	
    	return true;
    }
    

    @Override
	protected Dialog onCreateDialog(int id) {
    	switch(id){
    	case NEW_ITEM:
    		return createAlertDialog(this);
    	}
    	return null;
    }
    
    private Dialog createAlertDialog(Context ctx){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("New Item");
    	alert.setMessage("Enter new item name:");

    	final EditText input = new EditText(this);
    	input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(SMSHelper.getSMSLength())});
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	  
	    	  mDbHelper.createItem(input.getText().toString(), "a", true);
	    	  input.setText("");
	    	  fillData();
	    	}
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    		  input.setText("");
    	  }
    	});    	
    	
    	return alert.create();
    }

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteItem(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }  
}
