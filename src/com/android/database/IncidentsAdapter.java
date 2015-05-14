package com.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class IncidentsAdapter {
	// Database table name
		public static final String DATABASE_TABLE_1 = "Incidents";

		
		// Columns present in DATABASE_TABLE_1
				public static final String ROWID="Rowid";
				public static final String TYPE = "Type";
				public static final String LOCATION = "Location";
				public static final String DESCRIPTION = "Description";
				public static final String EST_CLEARENCE_TIME= "EstClearenceTime";
				public static final String STARTTIME= "StartTime";

		// Object for SQLiteDatabase
		private SQLiteDatabase database;

		// 
		public static final String TAG = "Incidents";	
		private IncidentsDBHelper incidents_db_helper;

		public IncidentsAdapter() {
		}

		public IncidentsAdapter open(Context context) throws SQLException {
			Log.i(TAG, "OPening DataBase Connection....");
			incidents_db_helper = new IncidentsDBHelper(context);
			database = incidents_db_helper.getWritableDatabase();
			return this;
		}

		public void close() {
			database.close();
		}

		public boolean deleteIncident(long rowId) {
			return database.delete(DATABASE_TABLE_1, ROWID + "=" + rowId, null) > 0;
		}

		public Cursor fetchAllIncidents() {	 
			return database.query(DATABASE_TABLE_1, new String[] {ROWID, TYPE , LOCATION,DESCRIPTION,EST_CLEARENCE_TIME,STARTTIME}, null, null, null, null, TYPE);
		}

		public Cursor fetchIncident(long incidentid) throws SQLException {

			Cursor mCursor = database.query(true, DATABASE_TABLE_1, new String[] {
					ROWID, TYPE , LOCATION,DESCRIPTION,EST_CLEARENCE_TIME,STARTTIME}, ROWID + "=" +
							incidentid, null, null, null, null, null);

			if(mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		public Cursor fetchIncident(String address,String city, String state, String country, String postalcode, String knownname) throws SQLException {

			Cursor mCursor = database.query(true, DATABASE_TABLE_1, new String[] {
					ROWID, TYPE , LOCATION,DESCRIPTION,EST_CLEARENCE_TIME,STARTTIME}, LOCATION+" like %"+city+"% OR "+LOCATION+" like %"+state+"% OR "+
							LOCATION+" like %"+country+"% OR "+LOCATION+" like %"+address+"% OR "+LOCATION+" like %"+knownname+"% OR "+LOCATION+" like %"+postalcode+"%", null, null, null, null, null);
			
			Log.i("LocationQuery", LOCATION+" like %"+city+"% OR "+LOCATION+" like %"+state+"% OR "+
							LOCATION+" like %"+country+"% OR "+LOCATION+" like %"+address+"% OR "+LOCATION+" like %"+knownname+"% OR "+LOCATION+" like %"+postalcode+"%");

			if(mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}


		public Cursor fetchIncidentCount(long incidentid) throws SQLException {

			Cursor mCursor = database.query(true, DATABASE_TABLE_1, new String[] {ROWID, TYPE , LOCATION,DESCRIPTION,EST_CLEARENCE_TIME,STARTTIME}, 
					ROWID + "=" + incidentid, null, null, null, null, null);

			if(mCursor!=null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}

		public boolean updateIncident(int commonNameId, String type, String location,String description,String Estcltime,String stTime) {
			ContentValues args = new ContentValues();
			args.put(TYPE, type);
			args.put(LOCATION, location);
			args.put(DESCRIPTION, description);
			args.put(EST_CLEARENCE_TIME, Estcltime);
			args.put(STARTTIME, stTime);

			return database.update(DATABASE_TABLE_1, args, ROWID + "=" + commonNameId, null) > 0;
		}
}
