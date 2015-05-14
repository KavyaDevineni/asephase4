package com.android.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gpsbasedincidentreporter.R;

public class IncidentsDBHelper extends SQLiteOpenHelper {
	
	// Name and version of Database.
		public static final String DATABASE_NAME = "incidents_database";
		public static final int DATABASE_VERSION = 1;

		// Names of Tables in Database
		public static final String DATABASE_TABLE_1 = "Incidents";
		//public static final String DATABASE_TABLE_2 = "un_common_names";

		// Columns present in DATABASE_TABLE_1
		public static final String ROWID="Rowid";
		public static final String TYPE = "Type";
		public static final String LOCATION = "Location";
		public static final String DESCRIPTION = "Description";
		public static final String EST_CLEARENCE_TIME= "EstClearenceTime";
		public static final String STARTTIME= "StartTime";


		// SQL query string for creating DATABASE_TABLE_1
		static final String CREATE_DATABASE_TABLE_1 =
				"create table " + DATABASE_TABLE_1 + " (" + ROWID + 
				" integer primary key autoincrement, " + TYPE +
				" text not null, " + LOCATION + " text not null, " +DESCRIPTION + " text not null, "+
				EST_CLEARENCE_TIME+ " text not null, "+STARTTIME + " text not null"+")";

		// TAGs for tables. Used in Log Cat.
		public static final String TAG_1 = "INCIDENTS_TABLE";

		// Object for a SQLiteDatabase
		public SQLiteDatabase mDb;
		private Context context;

		// Constructor
		public IncidentsDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Creating Table
			Log.i(TAG_1, "Creating Table: " + CREATE_DATABASE_TABLE_1);
			db.execSQL(CREATE_DATABASE_TABLE_1);
			insertDataIntoIncidents(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG_1, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			// We have to drop existing database tables
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_1);
			// Now, re-create the database.
			onCreate(db);

		}

		private void insertDataIntoIncidents(SQLiteDatabase db) {

			try{
				InputStream is = context.getResources().openRawResource(R.raw.incidents);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String strLine = null;

				while ((strLine = (br.readLine()).trim()) != null) {
					String[] temp;

					temp = strLine.split(",");

					ContentValues initialValues = new ContentValues();

					initialValues.put(TYPE, temp[0].trim());
					initialValues.put(LOCATION, temp[1].trim());
					initialValues.put(DESCRIPTION, temp[2].trim());
					initialValues.put(EST_CLEARENCE_TIME, temp[3].trim());
					initialValues.put(STARTTIME, temp[4].trim());

					db.insert(DATABASE_TABLE_1, null, initialValues);
				}

				is.close();
			}
			catch (Exception e){
				Log.i(TAG_1, "Error while inserting common names into table");
			}

		}
}
