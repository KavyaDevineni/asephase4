package com.example.gpsbasedincidentreporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.database.IncidentsAdapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener{ 

	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	TextView txtLat;
	Button btnIncidets;
	String lat;
	String provider;
	protected double latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	IncidentsAdapter incidentsdbadapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setActivityBackgroundColor(Color.GREEN);
	//code to communicate with db
	incidentsdbadapter = new IncidentsAdapter();
	incidentsdbadapter = incidentsdbadapter.open(getApplicationContext());
	//Cursor cursor = incidentsdbadapter.fetchIncident(2);
	Cursor cursor = incidentsdbadapter.fetchAllIncidents();
	
	setContentView(R.layout.activity_main);
	txtLat = (TextView) findViewById(R.id.textview1);
	btnIncidets = (Button) findViewById(R.id.button1);	
	StringBuilder builder=new StringBuilder();
	while(cursor.moveToNext()){
		builder.append(cursor.getString(1)+", "+cursor.getString(2)+", "+cursor.getString(3)+", "+cursor.getString(4)+
				cursor.getString(5)+"\n\n");
	}
	
	txtLat.setText("List of All Incidents:\n============================\n"+builder.toString());
	
	//Code for notifications
	NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
    .setSmallIcon(R.drawable.ic_launcher) // notification icon
    .setContentTitle("Incidents/Scheduled Report") // title for notification
    .setContentText(builder.toString()) // message for notification
    .setAutoCancel(false); // clear notification after click
	Intent intent = new Intent(this, MainActivity.class);
	PendingIntent pi = PendingIntent.getActivity(this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
	mBuilder.setContentIntent(pi);
	NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	
    mNotificationManager.notify(0, mBuilder.build());
    
    

	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	
	btnIncidets.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			double lng=0;
			double lat=0;
			// TODO Auto-generated method stub
			   Criteria c=new Criteria();
			   //criteria object will select best service based on
			   //Accuracy, power consumption, response, bearing and monetary cost
			   //set false to use best service otherwise it will select the default Sim network
			   //and give the location based on sim network 
			   //now it will first check satellite than Internet than Sim network location
			   String provider=locationManager.getBestProvider(c, false);
			   //now you have best provider
			   //get location
			   Location l=locationManager.getLastKnownLocation(provider);
			   if(l!=null)
			   {
			     //get latitude and longitude of the location
			     lng=l.getLongitude();
			     lat=l.getLatitude();
			     latitude=lat;
			     longitude=lng;
			  }
			   else
			   {
				   Log.e(CONNECTIVITY_SERVICE, "No Location found");
			   }
			   
			   //txtLat.setText("Langitude:"+lng+", Latitude: "+lat);
			    
			   //Get the location address via Google API
			  /* JSONObject ret = getLocationInfo(); 
			   JSONObject location;
			   String location_string=null;
			   try {
			       location = ret.getJSONArray("results").getJSONObject(0);
			       location_string = location.getString("formatted_address");
			       Log.d("test", "formattted address:" + location_string);
			   } catch (JSONException e1) {
			       e1.printStackTrace();

			   }
*/			   
			   Geocoder geocoder;
			   List<Address> addresses = null;
				geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

				try {
						addresses = geocoder.getFromLocation(lng, lat, 1);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Here 1 represent max location result to returned, by documents it recommended 1 to 5

				if(addresses.size()>=1){
				String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
				String city = addresses.get(0).getLocality();
				String state = addresses.get(0).getAdminArea();
				String country = addresses.get(0).getCountryName();
				String postalCode = addresses.get(0).getPostalCode();
				String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
				
				Cursor cursor = incidentsdbadapter.fetchIncident(address, city, state, country, postalCode, knownName);
				StringBuilder builder=new StringBuilder();
				while(cursor.moveToNext()){
					builder.append(cursor.getString(1)+", "+cursor.getString(2)+", "+cursor.getString(3)+", "+cursor.getString(4)+
							cursor.getString(5)+"\n\n");
				}
				
				txtLat.setText("address:" + address + ", city:" + city+", state:"+state+", country:"+country+
						", postalCode:"+postalCode+", konwnName:"+knownName);
				txtLat.setText(builder.toString());
				
				//Code for notifications
				NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(MainActivity.this)
			    .setSmallIcon(R.drawable.ic_launcher) // notification icon
			    .setContentTitle("Incidents/Scheduled Report") // title for notification
			    .setContentText(builder.toString()) // message for notification
			    .setAutoCancel(false); // clear notification after click
				Intent intent = new Intent(MainActivity.this, MainActivity.class);
				PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
				mBuilder.setContentIntent(pi);
				NotificationManager mNotificationManager =
			            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				
			    mNotificationManager.notify(0, mBuilder.build());
				
			}
			else
			 txtLat.setText("You either have internet disabled/not working in your device or No incidents found for current location!!! ");
				//txtLat.setText(location_string);
	}});
	incidentsdbadapter.close();
	
	}
	
	public void setActivityBackgroundColor(int color) {
	    View view = this.getWindow().getDecorView();
	    view.setBackgroundColor(color);
	}
	
	public JSONObject getLocationInfo() {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onLocationChanged(Location location) {
	txtLat = (TextView) findViewById(R.id.textview1);
	txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
	Geocoder geocoder;
	List<Address> addresses = null;
	geocoder = new Geocoder(this, Locale.getDefault());

	try {
		    //txtLat.setText("Geocoder methods implemented: "+geocoder.isPresent());

			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); 
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // Here 1 represent max location result to returned, by documents it recommended 1 to 5
	
	/*JSONObject ret = getLocationInfo(); 
	   
	   String location_string=null;
	   try {
		   JSONObject location1 = ret.getJSONArray("results").getJSONObject(0);
	       location_string = location1.getString("formatted_address");
	       Log.d("test", "formattted address:" + location_string);
	   } catch (JSONException e1) {
	       e1.printStackTrace();

	   }
*/
   if(addresses.size()>=1){
	String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
	String city = addresses.get(0).getLocality();
	String state = addresses.get(0).getAdminArea();
	String country = addresses.get(0).getCountryName();
	String postalCode = addresses.get(0).getPostalCode();
	String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
	
	txtLat.setText("address:" + address + ", city:" + city+", state:"+state+", country:"+country+
			", postalCode:"+postalCode+", konwnName"+knownName);
	
	Cursor cursor = incidentsdbadapter.fetchIncident(address, city, state, country, postalCode, knownName);
	StringBuilder builder=new StringBuilder();
	while(cursor.moveToNext()){
		builder.append(cursor.getString(1)+", "+cursor.getString(2)+", "+cursor.getString(3)+", "+cursor.getString(4)+
				cursor.getString(5)+"\n\n");
	}
	
	/*txtLat.setText("address:" + address + ", city:" + city+", state:"+state+", country:"+country+
			", postalCode:"+postalCode+", konwnName:"+knownName);*/
	txtLat.setText(builder.toString());
	
	//Code for notifications
	NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(MainActivity.this)
    .setSmallIcon(R.drawable.ic_launcher) // notification icon
    .setContentTitle("Incidents/Scheduled Report") // title for notification
    .setContentText(builder.toString()) // message for notification
    .setAutoCancel(false); // clear notification after click
	Intent intent = new Intent(MainActivity.this, MainActivity.class);
	PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
	mBuilder.setContentIntent(pi);
	NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	
    mNotificationManager.notify(0, mBuilder.build());
	
    }
   else
	 txtLat.setText("You either have internet disabled/not working in your device or No incidents found for current location!!! ");
   //txtLat.setText(location_string);
   
   
   }

	@Override
	public void onProviderDisabled(String provider) {
	Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
	Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	Log.d("Latitude","status");
	}
}
