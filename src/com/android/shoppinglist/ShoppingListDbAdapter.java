
package com.android.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingListDbAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_TOBUY = "tobuy";
    public static final String KEY_ROWID = "_id";
    private static final String TAG = "ShoppingListDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
        "create table items (_id integer primary key autoincrement, name text not null, tobuy bool not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "items";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS items");
            onCreate(db);
        }
    }
    
    public ShoppingListDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
   
    public ShoppingListDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public long createItem(String title, String body, boolean toBuy) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, title);
        initialValues.put(KEY_TOBUY, toBuy);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
   
    public boolean deleteItem(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllItems() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_TOBUY}, null, null, KEY_NAME + " COLLATE NOCASE", null, null);
    }

    public Cursor fetchItem(long rowId) throws SQLException {
        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public boolean updateItem(long rowId, boolean isChecked) {
        ContentValues args = new ContentValues();
        args.put(KEY_TOBUY, isChecked);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public String fetchItemsToBuy() throws SQLException{
    	Cursor mCursor =    			
    			mDb.query(true, DATABASE_TABLE, new String[] {KEY_NAME}, 
    					KEY_TOBUY + "=1", null, null, null, null, null);

    	String itemString = "";
    	while(mCursor.moveToNext()){
    		itemString += mCursor.getString(mCursor.getColumnIndex(KEY_NAME))+ "," ;
    	}
    	
    	if(itemString.length() > 0)
    		itemString = itemString.substring(0, itemString.length() - 1);
    	
    	return itemString;
    }
}
