package com.pdroid84.locadd.locationaddress;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested = true;
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "DEB";
    private TextView mTextView;
    protected Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textBox);
        mTextView.setText("Hi..Hi..");
        Log.d("DEB", "onCreate is called");
        mResultReceiver = new AddressResultReceiver(null);
        //create an instance of GoogleApiClient using the GoogleApiClient.Builder APIs in your activity's onCreate() method
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DEB", "onStart is called");
        //To gracefully manage the lifecycle of the connection, you should call connect() during the activity's onStart()
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.d("DEB", "onStop is called");
        //call disconnect() during the onStop() method
        mGoogleApiClient.disconnect();
        //make sure super is called at the end
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void startIntentService() {
        Log.d("DEB", "startIntentService is called");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("DEB", "onConnected is called");
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            String mLatitude = String.valueOf(mLastLocation.getLatitude());
            String mLongitude = String.valueOf(mLastLocation.getLongitude());
            Log.d("DEB", "The values are -> Laitude= " + mLatitude + " and Longitude= " + mLongitude);
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "no_geocoder_available", Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressRequested) {
            //    startIntentService();
            }
        }
    }
        @Override
        public void onConnectionSuspended ( int i){

        }

        @Override
        public void onConnectionFailed (ConnectionResult connectionResult){

        }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d("DEB", "onReceiveResult is called");
            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            Log.d("DEB", "Return code = " + resultCode + " & Address = " + mAddressOutput);
            Toast.makeText(getBaseContext(), "Return code = " + resultCode + " & Address = " + mAddressOutput, Toast.LENGTH_LONG).show();
            address = resultData.getParcelable("ADDRESS_DATA");
            if (address != null) {
                Log.i(TAG, "address_found-> MainActivity");

                Log.d(TAG, "Country Name = " + address.getCountryName());
                Log.d(TAG, "Country code = " + address.getCountryCode());
                Log.d(TAG, "Admin Area = " + address.getAdminArea());
                Log.d(TAG, "SubAdmin Area = " + address.getSubAdminArea());
                Log.d(TAG, "Locality Name = " + address.getLocality());
                Log.d(TAG, "Sub Locality Name = " + address.getSubLocality());
                Log.d(TAG, "Locale = " + address.getLocale());
                Log.d(TAG, "Postal Code = " + address.getPostalCode());
                Log.d(TAG, "Premises = " + address.getPremises());
                Log.d(TAG, "Feature Name = " + address.getFeatureName());
                Log.d(TAG, "SubThroughfare = " + address.getSubThoroughfare());
                Log.d(TAG, "Throughfare = " + address.getThoroughfare());
                Log.d(TAG, "Phone = " + address.getPhone());
                Log.d(TAG, "Extras = " + address.getExtras());
                Log.d(TAG, "Url = " + address.getUrl());
               // updateUI();

                String mData = "Country Name = " + address.getCountryName() + "<->Country code = " + address.getCountryCode() + "<->Admin Area = " + address.getAdminArea()
                         + "<->Locality Name = " + address.getLocality() + "<->Sub Locality Name = " + address.getSubLocality() + "<->Locale = " + address.getLocale() + "<->Postal Code = " + address.getPostalCode()
                         + "<->Premises = " + address.getPremises() + "<->Feature Name = " + address.getFeatureName() + "<->SubThroughfare = " + address.getSubThoroughfare()
                                 + "<->Throughfare = " + address.getThoroughfare() + "<->Phone = " + address.getPhone() + "<->Extras = " + address.getExtras() + "<->Url = " + address.getUrl();
                runOnUiThread(new DebRunnable(mData));

            }
        }
    }

    protected  void showData(String mData) {
        Log.d("DEB", "showData.. is called");
        Toast.makeText(getApplicationContext(),"Details:->"+mData,Toast.LENGTH_LONG).show();
    }

    public void getAdrs(View v) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        Log.d("DEB", "getAdrs is called");
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        updateUI();
    }

    public void updateUI() {
        Log.d("DEB", "updateUI is called");
       // Toast.makeText(getBaseContext(),"Country Name = " + address.getCountryName() + "Country code = " + address.getCountryCode() + "Admin Area = " + address.getAdminArea()
       //         + "Locality Name = " + address.getLocality() + "Sub Locality Name = " + address.getSubLocality() + "Locale = " + address.getLocale() + "Postal Code = " + address.getPostalCode(),Toast.LENGTH_LONG).show();
        if(address != null ) {
            mTextView.setText(address.getCountryName());
        }
        mTextView.setText("Updated now...");
    }

    class DebRunnable implements Runnable {

        String mData;

        public DebRunnable (String mData) {
            this.mData = mData;
        }
        @Override
        public void run() {
            mTextView.setText(mData);
            showData(mData);

        }
    }

}