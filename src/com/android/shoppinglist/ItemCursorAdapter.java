package com.android.shoppinglist;

import com.android.shoppinglist.*;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ItemCursorAdapter extends SimpleCursorAdapter {

	private Cursor cursor;
	private Context context;
	private ShoppingListDbAdapter mDbHelper;
	
	public ItemCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to){
		super(context, layout, c, from, to);
		this.cursor = c;
		this.context = context;
		mDbHelper = new ShoppingListDbAdapter(context);
		mDbHelper.open();
	}
	
	public View getView(int pos, View inView, ViewGroup parent){
		View view = inView;

			if (view == null){
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.notes_row, null);
			}
			this.cursor.moveToPosition(pos);
			String title = this.cursor.getString(this.cursor.getColumnIndex(ShoppingListDbAdapter.KEY_NAME));
			int toBuy = this.cursor.getInt(this.cursor.getColumnIndexOrThrow(ShoppingListDbAdapter.KEY_TOBUY));
			int id = this.cursor.getInt(this.cursor.getColumnIndexOrThrow(ShoppingListDbAdapter.KEY_ROWID));
			//boolean toBuy = this.cursor.get
			if(title != null){
				TextView tvTitle = (TextView)view.findViewById(R.id.text1);
				tvTitle.setText(title);
				CheckBox cb = (CheckBox)view.findViewById(R.id.checkbox_cheese);
				cb.setTag(id);
				cb.setOnCheckedChangeListener(null);
				cb.setChecked(toBuy > 0);	
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
		            @Override
		            public void onCheckedChanged(CompoundButton cb, boolean flag) {
		                // TODO Auto-generated method stub
		                int id = (Integer)cb.getTag();
		                try {
							mDbHelper.updateItem(id,flag);
							cursor.requery();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        });
						
			}
		
		return view;
	}
}
