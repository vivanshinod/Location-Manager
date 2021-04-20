# Location-Manager

This project is useful for fetch user location with help of gps and wifi in Android 11 

First create Background service named LocationService and add it in AndroidManifest.xml like below

<service android:name=".Services.LocationService"
 android:exported="false" android:enabled="true"/>
 
 then add below permissions
 
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>

now make one Constant class and add below parameters
    static final int LOCATION_SERVICE_ID = 190;
    static  final String ACTION_START_LOCATION_SERVICE = "startLocation";
    static  final String ACTION_STOP_LOCATION_SERVICE = "stopLocation";
    public static final String PREF_SELECTED_CURRENT_AREA = "prefselectedcurrentarea";
    public static final String PREF_SELECTED_AREA = "prefselectedarea";
    public static final String PREF_CURRENT_AREA = "prefcurrentarea";

then in the onCreate() of your MainActitivy add below code to start service like below:

private boolean isLocationServiceRunning(){
ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

if(activityManager != null) {
for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {

if (LocationService.class.getName().equals(service.service.getClassName())) {
if (service.foreground) {
return true;
}
}
}
return false;
}
return false;
}

private void startLocationService(){
if(!isLocationServiceRunning()){
Intent intent = new Intent(getApplicationContext(),LocationService.class);
intent.setAction("startLocation");
startService(intent);

    Toast.makeText(HomePageActivity.this, "Location Service Started!", Toast.LENGTH_SHORT).show();
  }
}

private void stopLocationService(){
if(isLocationServiceRunning()){
Intent i = new Intent(getApplicationContext(),LocationService.class);
i.setAction("stopLocation");
startService(i);
Toast.makeText(HomePageActivity.this, "Location Service Stopped!", Toast.LENGTH_SHORT).show();
}
}
@Override
protected void onStop() {
super.onStop();
stopLocationService();
}

@Override
protected void onDestroy() {
super.onDestroy();
stopLocationService();
}
