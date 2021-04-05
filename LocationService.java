package gujarat.classified.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import gujarat.classified.R;
import gujarat.classified.singleton.Constants;
import gujarat.classified.singleton.PrefUtils;

public class LocationService extends Service {

private SharedPreferences mPref;



private LocationCallback locationCallback = new LocationCallback() {


@Override
 public void onLocationResult(LocationResult locationResult) {
super.onLocationResult(locationResult);
if (locationResult != null && locationResult.getLastLocation() != null) {
double latitude = locationResult.getLastLocation().getLatitude();
double longitude = locationResult.getLastLocation().getLongitude();
Log.v("ResponseNew","Latitude: "+latitude+"\n"+"Longitude: "+longitude);


getCompleteAddressString(latitude,longitude);
}
}
};

@SuppressLint("LongLogTag")
private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
String strAdd = "";
mPref = PreferenceManager.getDefaultSharedPreferences(this);
try {
Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
if (addresses != null) {
Address returnedAddress = addresses.get(0);
StringBuilder strReturnedAddress = new StringBuilder("");

for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

Log.e("Address",strReturnedAddress.toString());
Log.e("City",returnedAddress.getLocality());
Log.e("pincode",returnedAddress.getPostalCode());
Log.e("land",returnedAddress.getSubLocality());
Log.e("state",returnedAddress.getAdminArea());

Log.e("latitute", String.valueOf(returnedAddress.getLatitude()));
Log.e("longitute",String.valueOf(returnedAddress.getLongitude()));
Log.v("ResponseNew",returnedAddress.getLocality());


}
mPref.edit().putString(Constants.PREF_CURRENT_CITY, returnedAddress.getLocality()).apply();
mPref.edit().putString(Constants.PREF_SELECTED_CURRENT_AREA, returnedAddress.getLocality()).apply();
mPref.edit().putString(Constants.PREF_CURRENT_AREA, returnedAddress.getLocality()).apply();

String currentCity = returnedAddress.getLocality();

// Toast.makeText(this, ""+currentCity, Toast.LENGTH_LONG).show();

 PrefUtils.setTempCity(currentCity,getApplicationContext());

System.out.println("Current City: "+currentCity);

strAdd = strReturnedAddress.toString();
Log.w("My Current location address", strReturnedAddress.toString());
stopLocationService();
} else {
Log.w("My Current location address", "No Address returned!");
}
} catch (Exception e) {
e.printStackTrace();
Log.w("My Current location address", "Cannot get Address!");
}
return strAdd;
}


@Nullable
 @Override
 public IBinder onBind(Intent intent) {
throw new UnsupportedOperationException("Not Yet Implemented!");
}

private void startLocationService() {
String channelID = "location_notification_channel";
NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
Intent resultIntent = new Intent();

PendingIntent pendingIntent = PendingIntent.
getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);

builder.setSmallIcon(R.mipmap.ic_launcher);
builder.setContentTitle("Location Service");
builder.setDefaults(NotificationCompat.DEFAULT_ALL);
builder.setContentText("Running");
builder.setContentIntent(pendingIntent);
builder.setAutoCancel(false);
builder.setPriority(NotificationCompat.PRIORITY_MAX);

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
CharSequence name;
NotificationChannel notificationChannel = new NotificationChannel(channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
notificationChannel.setDescription("This Channel is used by Location Service");

notificationManager.createNotificationChannel(notificationChannel);
}
}

LocationRequest locationRequest = new LocationRequest();
locationRequest.setInterval(4000);
locationRequest.setFastestInterval(2000);
locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
 // ActivityCompat#requestPermissions
 // here to request the missing permissions, and then overriding
 // public void onRequestPermissionsResult(int requestCode, String[] permissions,
 // int[] grantResults)
 // to handle the case where the user grants the permission. See the documentation
 // for ActivityCompat#requestPermissions for more details.
 return;
}
LocationServices.getFusedLocationProviderClient(getApplicationContext())
.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

startForeground(Constant.LOCATION_SERVICE_ID,builder.build());
}

private void stopLocationService(){
LocationServices.getFusedLocationProviderClient(getApplicationContext())
.removeLocationUpdates(locationCallback);
stopForeground(true);
stopSelf();
}

public int onStartCommand(Intent intent, int flags, int startId){
if(intent != null){
String action = intent.getAction();

if(action != null){
if(action.equals(Constant.ACTION_START_LOCATION_SERVICE)){
startLocationService();
} else if (action.equals(Constant.ACTION_STOP_LOCATION_SERVICE)) {
stopLocationService();
}
}
}
return super.onStartCommand(intent,flags,startId);
}
}
