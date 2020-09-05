package in.afckstechnologies.employeeattendance.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


import in.afckstechnologies.employeeattendance.R;
import in.afckstechnologies.employeeattendance.adapter.EmployeeAttendanceListAdpter;
import in.afckstechnologies.employeeattendance.adapter.EmployeeLeaveListAdpter;
import in.afckstechnologies.employeeattendance.jsonparser.JsonHelper;
import in.afckstechnologies.employeeattendance.models.EmployeeAttendanceDAO;
import in.afckstechnologies.employeeattendance.models.EmployeeLeaveDAO;
import in.afckstechnologies.employeeattendance.models.MonthNameDAO;
import in.afckstechnologies.employeeattendance.utils.AppStatus;
import in.afckstechnologies.employeeattendance.utils.Config;
import in.afckstechnologies.employeeattendance.utils.Constant;
import in.afckstechnologies.employeeattendance.utils.FeesListener;
import in.afckstechnologies.employeeattendance.utils.GPSTracker;
import in.afckstechnologies.employeeattendance.utils.SmsListener;
import in.afckstechnologies.employeeattendance.utils.VersionChecker;
import in.afckstechnologies.employeeattendance.utils.WebClient;
import in.afckstechnologies.employeeattendance.view.ApplyEmployeeLeaveTodayView;
import in.afckstechnologies.employeeattendance.view.DisplayEmployeeLeaveOptionsView;

public class EmployeeAttendanceActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    Button enteryAttendance, existAttendance;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x7;
    static final Integer ACCESS_FINE_LOCATION = 0x14;
    static final Integer ACCESS_COARSE_LOCATION = 0x15;
    JSONObject jsonObject, jsonObj, jsonLeadObj, jsonLeadObjLeave,jsonObj1;
    JSONArray jsonArray;
    AlertDialog alertDialog1;
    CharSequence[] values = {" Pune Camp ", " Pimpri "};
    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    String address = "";
    String msg = "";
    boolean status;
    boolean flag = true;
    MediaPlayer mp;


    String TrackResponse = "";
    int ea_id;
    String id = "", leaveAvalResponse = "", trainerResponse = "", temLocationResponse = "", employeeLeaveRespone = "";
    String center_id = "", checkAttendaneResponse = "", checkEvengAttendaneResponse = "",
            monthResponse = "", monthId = "", employeeAttendanceRespone = "", totalLeavesResponse = "";
    ProgressDialog mProgressDialog;

    Spinner spinnerMonth;
    ArrayList<MonthNameDAO> monthlist;
    List<EmployeeAttendanceDAO> data;
    EmployeeAttendanceListAdpter employeeAttendanceListAdpter;
    private RecyclerView mstudentList, employeeLeaveList;
    Button leaveApply, leaveApplied, showAttendance;
    LinearLayout l2;
    String attendanceFlag = "";
    ArrayList<EmployeeLeaveDAO> leaveDAOArrayList;
    EmployeeLeaveListAdpter employeeLeaveListAdpter;
    int backCount = 0;
    TextView showAllLeave;
    String txt_leaves = "";

    String verifyMobileDeviceIdResponse = "";
    boolean statusv;
    String mobileDeviceId = "";
    private String latestVersion = "", smspassResponse = "", getRoleusersResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_attendance);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Intent intent = getIntent();
        //center_id = intent.getStringExtra("e_center_id");

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            verifyMobileDeviceId();
            new userAvailable().execute();
            new initMonthSpinner().execute();
        } else {

            Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        // mp = MediaPlayer.create(EmployeeAttendanceActivity.this, R.raw.sign);
        //mp.start();
        enteryAttendance = (Button) findViewById(R.id.enteryAttendance);
        existAttendance = (Button) findViewById(R.id.existAttendance);

        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        mstudentList = (RecyclerView) findViewById(R.id.employeeAttendanceList);
        employeeLeaveList = (RecyclerView) findViewById(R.id.employeeLeaveList);
        leaveApply = (Button) findViewById(R.id.leaveApply);
        leaveApplied = (Button) findViewById(R.id.leaveApplied);
        showAllLeave = (TextView) findViewById(R.id.showAllLeave);
        l2 = (LinearLayout) findViewById(R.id.l2);
        enteryAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    attendanceFlag = "1";
                    getCheckLocation();

                    // new userMorningAttendance().execute();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        existAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    attendanceFlag = "2";
                    getCheckLocation();
                    // new userCheckMorningAttendance().execute();
                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        leaveApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayEmployeeLeaveOptionsView displayEmployeeLeaveOptionsView = new DisplayEmployeeLeaveOptionsView();
                displayEmployeeLeaveOptionsView.show(getSupportFragmentManager(), "DisplayEmployeeLeaveOptionsView");
            }
        });

        ApplyEmployeeLeaveTodayView.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                new userAvailable().execute();
            }
        });
        EmployeeLeaveListAdpter.bindListener(new FeesListener() {
            @Override
            public void messageReceived(String messageText) {
                new getEmployeeLeaveList().execute();
            }
        });
        leaveApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    new getEmployeeLeaveList().execute();
                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    /**
     * Called when the activity is about to become visible.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            getUserRoles();


        }


    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(EmployeeAttendanceActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EmployeeAttendanceActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(EmployeeAttendanceActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(EmployeeAttendanceActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public void displayLocationSettingsRequest(final Context context) {
// final int REQUEST_CHECK_SETTINGS = 0x1;
        final String TAG = "EmployeeAttendanceActivity";
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
// Show the dialog by calling startResolutionForResult(), and check the result
// in onActivityResult().
                            status.startResolutionForResult((EmployeeAttendanceActivity) context, REQUEST_CHECK_SETTINGS);

// getActivity().startActivityForResult((MainActivity)getActivity(),REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void getCheckLocation() {

        // create class object
        for (int i = 0; i <= 30; i++) {
            gps = new GPSTracker(EmployeeAttendanceActivity.this);
        }

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.i("TrackResponse", "" + latitude);
            Log.i("TrackResponse", "" + longitude);
            if (latitude != 0.0 && longitude != 0.0) {
                new rangeAvailable().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_LONG).show();
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //


            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Do something for lollipop and above versions
                displayLocationSettingsRequest(EmployeeAttendanceActivity.this);
            } else {
                // do something for phones running an SDK before lollipop
                gps.showSettingsAlert();

            }
        }

    }

    private class rangeAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {

                        put("lat", latitude);
                        put("long", longitude);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            trainerResponse = serviceAccess.SendHttpPost(Config.URL_AVAILABLERANGE, jsonObj);
            Log.i("resp", "centerListResponse" + trainerResponse);


            if (trainerResponse.compareTo("") != 0) {
                if (isJSONValid(trainerResponse)) {


                    try {

                        jsonObject = new JSONObject(trainerResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        center_id = jsonObject.getString("center_id");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                if (attendanceFlag.equals("1")) {
                    new userMorningAttendance().execute();
                }
                if (attendanceFlag.equals("2")) {
                    new userCheckMorningAttendance().execute();
                }
                // Close the progressdialog
                //  mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                //  mProgressDialog.dismiss();
                CreateAlertDialogWithRadioButtonGroup();

            }
        }
    }

    public void CreateAlertDialogWithRadioButtonGroup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeAttendanceActivity.this);

        builder.setTitle("Select Your Location");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        Toast.makeText(EmployeeAttendanceActivity.this, "Pune Camp", Toast.LENGTH_LONG).show();
                        center_id = "1";
                        if (attendanceFlag.equals("1")) {
                            new userMorningAttendance().execute();
                        }
                        if (attendanceFlag.equals("2")) {
                            new userCheckMorningAttendance().execute();
                        }
                        new upDateLocation().execute();
                        break;
                    case 1:
                        center_id = "1";
                        if (attendanceFlag.equals("1")) {
                            new userMorningAttendance().execute();
                        }
                        if (attendanceFlag.equals("2")) {
                            new userCheckMorningAttendance().execute();
                        }
                        new upDateLocation().execute();
                        break;

                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

    private class upDateLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(TrainerVerfiyActivity.this);
            // Set progressdialog title
            // mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            // mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("latitude", latitude);
                        put("longitude", longitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            temLocationResponse = serviceAccess.SendHttpPost(Config.URL_ADDTEMPLOCATIONDATA, jsonObj);
            Log.i("resp", "temLocationResponse" + temLocationResponse);


            if (temLocationResponse.compareTo("") != 0) {
                if (isJSONValid(temLocationResponse)) {


                    try {

                        jsonObject = new JSONObject(temLocationResponse);
                        msg = jsonObject.getString("message");
                        status = jsonObject.getBoolean("status");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {


            // Close the progressdialog
            //  mProgressDialog.dismiss();

        }
    }

    private void getLocation() {

        // create class object
        gps = new GPSTracker(EmployeeAttendanceActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.i("TrackResponse", "" + latitude);
            Log.i("TrackResponse", "" + longitude);
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(18.522512);//18.522512
            locationB.setLongitude(73.8762828);//73.8762828
            float distance = locationA.distanceTo(locationB);
            // Double d=  distance(latitude,longitude,18.6188416,73.8060648);
            float distance1 = distFrom(latitude, longitude, 18.522512, 73.8762828);
            //  Toast.makeText(getApplicationContext(), "distance"+distance1, Toast.LENGTH_LONG).show();
            Log.i("address", "" + distance1);
            Log.i("address", "" + distance);
            // Toast.makeText(getApplicationContext(), "distance"+distance, Toast.LENGTH_LONG).show();

            Geocoder geocoder;
            final List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                Log.i("address", address);
                //    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
            Calendar cal = Calendar.getInstance();
            final String date = format.format(cal.getTime());

            final JSONObject jsonReqObj = new JSONObject() {
                {
                    try {

                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("login_location", center_id);
                        put("logout_location", center_id);
                        put("id", "");
                        put("status", "1");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            Thread objectThread1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    WebClient serviceAccess = new WebClient();
                    Log.i("json", "json" + jsonReqObj);
                    TrackResponse = serviceAccess.SendHttpPost(Config.URL_ADDEMPLOYEEATTENDACE, jsonReqObj);
                    Log.i("TrackResponse", TrackResponse);
                    if (isJSONValid(TrackResponse)) {


                        try {

                            jsonObject = new JSONObject(TrackResponse);
                            status = jsonObject.getBoolean("status");
                            msg = jsonObject.getString("message");

                            if (status) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Attendance Mark Successfully!", Toast.LENGTH_LONG).show();
                                        // mp = MediaPlayer.create(EmployeeAttendanceActivity.this, R.raw.sign);
                                        //mp.start();
                                        new getEmployeeAttendanceList().execute();
                                    }
                                });


                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }
            });

            objectThread1.start();


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //


            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Do something for lollipop and above versions
                displayLocationSettingsRequest(EmployeeAttendanceActivity.this);
            } else {
                // do something for phones running an SDK before lollipop
                gps.showSettingsAlert();

            }
        }

    }

    private void getLocation1() {

        // create class object
        gps = new GPSTracker(EmployeeAttendanceActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.i("TrackResponse", "" + latitude);
            Log.i("TrackResponse", "" + longitude);
            Location locationA = new Location("point A");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(18.522512);//18.522512
            locationB.setLongitude(73.8762828);//73.8762828
            float distance = locationA.distanceTo(locationB);
            // Double d=  distance(latitude,longitude,18.6188416,73.8060648);
            float distance1 = distFrom(latitude, longitude, 18.522512, 73.8762828);
            //  Toast.makeText(getApplicationContext(), "distance"+distance1, Toast.LENGTH_LONG).show();
            Log.i("address", "" + distance1);
            Log.i("address", "" + distance);
            // Toast.makeText(getApplicationContext(), "distance"+distance, Toast.LENGTH_LONG).show();
            if (distance < 50.00) {
                Geocoder geocoder;
                final List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    Log.i("address", address);
                    //    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
                Calendar cal = Calendar.getInstance();
                final String date = format.format(cal.getTime());

                final JSONObject jsonReqObj = new JSONObject() {
                    {
                        try {

                            put("user_id", preferences.getString("attendance_user_id", ""));
                            put("login_location", center_id);
                            put("logout_location", center_id);
                            put("id", id);
                            put("status", "2");

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("json exception", "json exception" + e);
                        }
                    }
                };

                Thread objectThread1 = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        WebClient serviceAccess = new WebClient();
                        Log.i("json", "json" + jsonReqObj);
                        String TrackResponse = serviceAccess.SendHttpPost(Config.URL_ADDEMPLOYEEATTENDACE, jsonReqObj);
                        Log.i("TrackResponse", TrackResponse);
                        if (isJSONValid(TrackResponse)) {


                            try {

                                jsonObject = new JSONObject(TrackResponse);
                                status = jsonObject.getBoolean("status");
                                msg = jsonObject.getString("message");
                                if (status) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Attendance Mark Successfully!", Toast.LENGTH_LONG).show();
                                            //  mp = MediaPlayer.create(EmployeeAttendanceActivity.this, R.raw.sign);
                                            // mp.start();
                                            new getEmployeeAttendanceList().execute();
                                        }
                                    });


                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                    }
                });

                objectThread1.start();
            } else {
                Toast.makeText(getApplicationContext(), "You are not in location zone! Please try again.", Toast.LENGTH_LONG).show();
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //


            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Do something for lollipop and above versions
                displayLocationSettingsRequest(EmployeeAttendanceActivity.this);
            } else {
                // do something for phones running an SDK before lollipop
                gps.showSettingsAlert();

            }
        }

    }

    private class userAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //   mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
            Calendar cal = Calendar.getInstance();
            final String date = format.format(cal.getTime());
            jsonObj = new JSONObject() {
                {
                    try {

                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("c_date", date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            leaveAvalResponse = serviceAccess.SendHttpPost(Config.URL_CHECKAVAILABLELEAVE, jsonObj);
            Log.i("resp", "centerListResponse" + leaveAvalResponse);


            if (leaveAvalResponse.compareTo("") != 0) {
                if (isJSONValid(leaveAvalResponse)) {


                    try {

                        jsonObject = new JSONObject(leaveAvalResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {

                l2.setVisibility(View.GONE);

                // Close the progressdialog
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                l2.setVisibility(View.VISIBLE);
                //  mProgressDialog.dismiss();

            }
        }
    }

    private class userMorningAttendance extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
            Calendar cal = Calendar.getInstance();
            final String date = format.format(cal.getTime());
            jsonObj = new JSONObject() {
                {
                    try {

                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("current_date", date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            checkAttendaneResponse = serviceAccess.SendHttpPost(Config.URL_CHECKEMPLOYEELOGINATTENDANCE, jsonObj);
            Log.i("resp", "centerListResponse" + checkAttendaneResponse);


            if (checkAttendaneResponse.compareTo("") != 0) {
                if (isJSONValid(checkAttendaneResponse)) {


                    try {

                        jsonObject = new JSONObject(checkAttendaneResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getApplicationContext(), "Already marked attendance!", Toast.LENGTH_LONG).show();
                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeAttendanceActivity.this);
                builder.setMessage("Do you want to mark Morning Attendance ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getLocation();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Attendance Marking");
                alert.show();

                mProgressDialog.dismiss();

            }
        }
    }

    private class userCheckMorningAttendance extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
            Calendar cal = Calendar.getInstance();
            final String date = format.format(cal.getTime());
            jsonObj = new JSONObject() {
                {
                    try {

                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("current_date", date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            checkAttendaneResponse = serviceAccess.SendHttpPost(Config.URL_CHECKEMPLOYEELOGINATTENDANCE, jsonObj);
            Log.i("resp", "centerListResponse" + checkAttendaneResponse);


            if (checkAttendaneResponse.compareTo("") != 0) {
                if (isJSONValid(checkAttendaneResponse)) {


                    try {

                        jsonObject = new JSONObject(checkAttendaneResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        id = jsonObject.getString("id");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {

                new userCheckEvenggAttendance().execute();

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {

                Toast.makeText(getApplicationContext(), "You are not login today so please try next day!", Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();

            }
        }
    }

    private class userCheckEvenggAttendance extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //  mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd hh:mm:ss
            Calendar cal = Calendar.getInstance();
            final String date = format.format(cal.getTime());
            jsonObj = new JSONObject() {
                {
                    try {

                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("current_date", date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            checkEvengAttendaneResponse = serviceAccess.SendHttpPost(Config.URL_CHECKEMPLOYEELOGOUTATTENDANCE, jsonObj);
            Log.i("resp", "centerListResponse" + checkAttendaneResponse);


            if (checkEvengAttendaneResponse.compareTo("") != 0) {
                if (isJSONValid(checkEvengAttendaneResponse)) {


                    try {

                        jsonObject = new JSONObject(checkEvengAttendaneResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getApplicationContext(), "Already marked attendance!", Toast.LENGTH_LONG).show();
                // Close the progressdialog
                //  mProgressDialog.dismiss();
            } else {

                //    mProgressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeAttendanceActivity.this);
                builder.setMessage("Do you want to mark Evening Attendance ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                getLocation1();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Attendance Marking");
                alert.show();


            }
        }
    }

    private class initMonthSpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //   mProgressDialog = new ProgressDialog(FixedAssetsActivity.this);
            // Set progressdialog title
            //   mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            // mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            jsonLeadObjLeave = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("attendance_user_id", ""));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            //  String baseURL = "http://192.168.1.13:8088/lms/api/lead/showlead";
            Log.i("json", "json" + jsonLeadObj);
            monthResponse = serviceAccess.SendHttpPost(Config.URL_GETALLEMPLOYEEAMONTH, jsonLeadObj);
            Log.i("resp", "leadListResponse" + monthResponse);

            Log.i("json", "json" + jsonLeadObj);
            totalLeavesResponse = serviceAccess.SendHttpPost(Config.GETEMPLOYEELEAVESBYID, jsonLeadObjLeave);
            Log.i("resp", "totalLeavesResponse" + totalLeavesResponse);

            if (monthResponse.compareTo("") != 0) {
                if (isJSONValid(monthResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                monthlist = new ArrayList<>();
                                //   monthlist.add(new MonthNameDAO("0", "Select Month"));
                                JSONArray LeadSourceJsonObj = new JSONArray(monthResponse);
                                for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                    JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                    monthlist.add(new MonthNameDAO("" + i + 1, json_data.getString("MMYY")));

                                }

                                jsonArray = new JSONArray(monthResponse);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }

                if (isJSONValid(totalLeavesResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                JSONObject jsonObject = new JSONObject(totalLeavesResponse);
                                txt_leaves = jsonObject.getString("totaLeave");

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (monthResponse.compareTo("") != 0) {
                showAllLeave.setVisibility(View.VISIBLE);
                showAllLeave.setText(txt_leaves);
                // Spinner spinnerCustom = (Spinner) findViewById(R.id.spinnerBranch);
                ArrayAdapter<MonthNameDAO> adapter = new ArrayAdapter<MonthNameDAO>(EmployeeAttendanceActivity.this, android.R.layout.simple_spinner_dropdown_item, monthlist);
                // MyAdapter adapter = new MyAdapter(StudentsListActivity.this,R.layout.spinner_item,locationlist);
                spinnerMonth.setAdapter(adapter);
                spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        MonthNameDAO LeadSource = (MonthNameDAO) parent.getSelectedItem();
                        // Toast.makeText(getApplicationContext(), "Source ID: " + LeadSource.getId() + ",  Source Name : " + LeadSource.getLocation_name(), Toast.LENGTH_SHORT).show();
                        monthId = LeadSource.getMonth();
                        new getEmployeeAttendanceList().execute();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });
                //  mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                // mProgressDialog.dismiss();
            }
        }
    }

    //
    private class getEmployeeLeaveList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EmployeeAttendanceActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("attendance_user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            //  String baseURL = "http://192.168.1.13:8088/srujanlms_new/api/Leadraw/showleadraw";
            Log.i("json", "json" + jsonLeadObj);
            employeeLeaveRespone = serviceAccess.SendHttpPost(Config.URL_GETALLEMPLOYEELEAVEBYUSERID, jsonLeadObj);
            Log.i("resp", "employeeLeaveRespone" + employeeLeaveRespone);
            if (employeeLeaveRespone.compareTo("") != 0) {
                if (isJSONValid(employeeLeaveRespone)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                leaveDAOArrayList = new ArrayList<>();
                                JsonHelper jsonHelper = new JsonHelper();
                                leaveDAOArrayList = jsonHelper.parseShowEmployeeLeaveList(employeeLeaveRespone);
                                jsonArray = new JSONArray(employeeLeaveRespone);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (employeeLeaveRespone.compareTo("") != 0) {
                mstudentList.setVisibility(View.GONE);
                employeeLeaveList.setVisibility(View.VISIBLE);
                employeeLeaveListAdpter = new EmployeeLeaveListAdpter(EmployeeAttendanceActivity.this, leaveDAOArrayList);
                employeeLeaveList.setAdapter(employeeLeaveListAdpter);
                employeeLeaveList.setLayoutManager(new LinearLayoutManager(EmployeeAttendanceActivity.this));
                employeeLeaveListAdpter.notifyDataSetChanged();

                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                mProgressDialog.dismiss();
            }
        }
    }

    //
    private class getEmployeeAttendanceList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
          /*  mProgressDialog = new ProgressDialog(ShowEmployeeAttendanceActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("attendance_user_id", ""));
                        put("monthId", monthId);
                        put("login_datetime", "All");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            //  String baseURL = "http://192.168.1.13:8088/srujanlms_new/api/Leadraw/showleadraw";
            Log.i("json", "json" + jsonLeadObj);
            employeeAttendanceRespone = serviceAccess.SendHttpPost(Config.URL_GETALLEMPLOYEEATTENDANCEBYUSERID, jsonLeadObj);
            Log.i("resp", "batchesListResponse" + employeeAttendanceRespone);
            if (employeeAttendanceRespone.compareTo("") != 0) {
                if (isJSONValid(employeeAttendanceRespone)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                data = new ArrayList<>();
                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseShowEmployeeAttendanceList(employeeAttendanceRespone);
                                jsonArray = new JSONArray(employeeAttendanceRespone);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (employeeAttendanceRespone.compareTo("") != 0) {
                employeeAttendanceListAdpter = new EmployeeAttendanceListAdpter(EmployeeAttendanceActivity.this, data);
                mstudentList.setAdapter(employeeAttendanceListAdpter);
                mstudentList.setLayoutManager(new LinearLayoutManager(EmployeeAttendanceActivity.this));
                employeeAttendanceListAdpter.notifyDataSetChanged();

                //  mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                //mProgressDialog.dismiss();
            }
        }
    }

    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }



    public void verifyMobileDeviceId() {


        jsonObj = new JSONObject() {
            {
                try {
                    put("pDeviceID", preferences.getString("attendance_mobile_deviceid", ""));
                    put("role_id", "2");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread objectThread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                verifyMobileDeviceIdResponse = serviceAccess.SendHttpPost(Config.URL_GETAVAILABLEMOBILEDEVICES, jsonObj);
                Log.i("loginResponse", "verifyMobileDeviceIdResponse" + verifyMobileDeviceIdResponse);
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() { // This thread runs in the UI
                            @Override
                            public void run() {
                                if (verifyMobileDeviceIdResponse.compareTo("") == 0) {

                                } else {

                                    try {
                                        JSONObject jObject = new JSONObject(verifyMobileDeviceIdResponse);
                                        statusv = jObject.getBoolean("status");

                                        if (statusv) {


                                        }
                                        else {
                                            finish();
                                            prefEditor.putString("attendance_user_id", "");
                                            prefEditor.commit();
                                            Intent i = new Intent(EmployeeAttendanceActivity.this, SplashScreenActivity.class);
                                            startActivity(i);
                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                };

                new Thread(runnable).start();
            }
        });
        objectThread.start();
    }

    public void getUserRoles() {


        jsonObj1 = new JSONObject() {
            {
                try {
                    put("role_id", "2");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread objectThread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                getRoleusersResponse = serviceAccess.SendHttpPost(Config.URL_GETAVAILABLEROLES, jsonObj1);
                Log.i("getRoleusersResponse", getRoleusersResponse);
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() { // This thread runs in the UI
                            @Override
                            public void run() {
                                if (getRoleusersResponse.compareTo("") == 0) {

                                } else {

                                    try {
                                        JSONObject jObject = new JSONObject(getRoleusersResponse);
                                        status = jObject.getBoolean("status");

                                        if (status) {
                                            forceUpdate();
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                };

                new Thread(runnable).start();
            }
        });
        objectThread.start();
    }

    public void forceUpdate() {
        //  int playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString("android_latest_version_code");
        VersionChecker versionChecker = new VersionChecker();
        try {
            latestVersion = versionChecker.execute().get();
            /*if (latestVersion.length() > 0) {
                latestVersion = latestVersion.substring(50, 58);
                latestVersion = latestVersion.trim();
            }*/


            Log.d("versoncode", "" + latestVersion);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //  String currentVersion = packageInfo.versionName;
        String currentVersion = packageInfo.versionName;

        new ForceUpdateAsync(currentVersion, EmployeeAttendanceActivity.this).execute();

    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {


        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {


            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!latestVersion.equals("")) {
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();

                        if (!((Activity) context).isFinishing()) {
                            showForceUpdateDialog();
                        }


                    }
                } else {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                        // AppUpdater appUpdater = new AppUpdater((Activity) context);
                        //  appUpdater.start();
                    } else {

                        Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }

                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));

            alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + context.getString(R.string.youAreNotUpdatedMessage1));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

}

