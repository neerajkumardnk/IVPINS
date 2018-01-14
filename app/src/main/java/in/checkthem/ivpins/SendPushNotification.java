package in.checkthem.ivpins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendPushNotification extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth.AuthStateListener authListener;
    private Button buttonSendPush, getNotifButton, getLocation,myProfile;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    public EditText editTextTitle, editTextMessage, editTextImage,editTextVehicle;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;
    public TextView longitudeTextView,latitudeTextView,placeTextView,addressTextView,MyToken;
    public String placename,latitude, longitude, address,la,lg,myToken ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_push_notification);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SendPushNotification.this,LoginActivity.class));
                    finish();
                }
            }
        };
        MyToken=(TextView)findViewById(R.id.myToken);
        longitudeTextView=(TextView)findViewById(R.id.longitudetv);
        latitudeTextView=(TextView)findViewById(R.id.latitudetv);
        placeTextView=(TextView)findViewById(R.id.placetv);
        addressTextView=(TextView)findViewById(R.id.addresstv);
        buttonSendPush = (Button) findViewById(R.id.buttonSendPush);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        editTextImage = (EditText) findViewById(R.id.editTextImageUrl);
        editTextVehicle=(EditText)findViewById(R.id.editTextvehicle);
        editTextVehicle.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        myProfile=(Button)findViewById(R.id.myProfile);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SendPushNotification.this, MainActivity.class);
                startActivity(intent);
            }
        });
        getNotifButton=(Button)findViewById(R.id.getNotifButton);
        getNotifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SendPushNotification.this, in.checkthem.ivpins.ShowNotifFromFirebase.class);
                startActivity(intent);
            }
        });
        getLocation=(Button)findViewById(R.id.getLocationPickerButton);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SendPushNotification.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonSendPush.setOnClickListener(this);

        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
//        final String v = in.checkthem.ivpins.SharedPrefManager.getInstance(this).getVehicleNumber();
//        DatabaseReference Root=FirebaseDatabase.getInstance().getReference();
//        DatabaseReference databaseReference=Root.child(v).child("token");
//        String MyToken=SharedPrefManager.getInstance(this).getDeviceToken();
//        databaseReference.setValue(MyToken);
//        myToken.setText(MyToken);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        mClient.connect();

    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                placename = String.format("%s", place.getName());
                latitude = String.valueOf(place.getLatLng());
                longitude = String.valueOf(place.getLatLng());
                address = String.format("%s", place.getAddress());
                LatLng query=place.getLatLng();
               la=String.valueOf(query.latitude);
                lg=String.valueOf(query.longitude);
                placeTextView.setText(placename);
                longitudeTextView.setText(lg);
                latitudeTextView.setText(la);
                addressTextView.setText(address);
            }
        }
    }

    private void sendSinglePush() {

        final String title = editTextTitle.getText().toString().trim();
        final String message = editTextMessage.getText().toString().trim();
        final String image = editTextImage.getText().toString().trim();
        final String vehicle=editTextVehicle.getText().toString().trim();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Tokens").child(vehicle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               myToken=dataSnapshot.getValue(String.class);

                MyToken.setText(myToken);
                final String token= MyToken.getText().toString().trim();
                progressDialog = new ProgressDialog(SendPushNotification.this);
                progressDialog.setMessage("Sending Push");

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "Enter Title", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Enter Message", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(vehicle)) {
                    Toast.makeText(getApplicationContext(), "Enter Vehicle Number", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(la)) {
                    Toast.makeText(getApplicationContext(), "Select Location", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(lg)) {
                    Toast.makeText(getApplicationContext(), "Select Location", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_NEW_PUSH,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();

                                Toast.makeText(SendPushNotification.this, response, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("title", title);
                        params.put("message", message);
                        params.put("token", token);
                        if (!TextUtils.isEmpty(image))
                            params.put("image", image);
                        return params;
                    }
                };
                MyVolley.getInstance(SendPushNotification.this).addToRequestQueue(stringRequest);
                sendToFirebaseDb();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //final String email = spinner.getSelectedItem().toString();

    }
    private void sendToFirebaseDb(){
        //data
        final String title = editTextTitle.getText().toString().trim();
        final String message = editTextMessage.getText().toString().trim();
        final String vehicle=editTextVehicle.getText().toString().trim();
        final String placename=placeTextView.getText().toString().trim();
        final String latitude=latitudeTextView.getText().toString().trim();
        final String longitude=longitudeTextView.getText().toString().trim();
        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        final String v = in.checkthem.ivpins.SharedPrefManager.getInstance(this).getVehicleNumber();
        final String status="un resolved";
        final String phone = in.checkthem.ivpins.SharedPrefManager.getInstance(this).getPhoneNumber();
        FirebaseDatabase firebaseDatabase2;
        DatabaseReference databaseReference2;
        //Sending Data to Fdb
        firebaseDatabase2=FirebaseDatabase.getInstance();
        databaseReference2=firebaseDatabase2.getReference().child("complaints").push();
        databaseReference2.child("title").setValue(title);
        databaseReference2.child("message").setValue(message);
        databaseReference2.child("status").setValue(status);
        databaseReference2.child("to").setValue(vehicle);
        databaseReference2.child("from").setValue(v);
        databaseReference2.child("time").setValue(currentDateTimeString);
        databaseReference2.child("latitude").setValue(latitude);
        databaseReference2.child("longitude").setValue(longitude);
        databaseReference2.child("place").setValue(placename);
        //sendSms();

    }
    @Override
    public void onClick(View view) {
        //calling the method send push on button click
        sendSinglePush();
    }

//        public String sendSms() {
//            try {
//                // Construct data
//                String user = "username=" + "neerajdama@gmail.com";
//                String hash = "&hash=" + "a9e92850f43726e101916b17adacf958c8bc8ed80ace42407898bf77b35e2cf3";
//                String message = "&message=" + "This is your message L";
//                String sender = "&sender=" + "TXTLCL";
//                String numbers = "&numbers=" + "918885271982";
//
//                // Send data
//                HttpURLConnection conn = (HttpURLConnection) new URL("http://api.textlocal.in/send/?").openConnection();
//                String data = user + hash + numbers + message + sender;
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//                conn.getOutputStream().write(data.getBytes("UTF-8"));
//                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                final StringBuffer stringBuffer = new StringBuffer();
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    stringBuffer.append(line);
//                }
//                rd.close();
//
//                return stringBuffer.toString();
//            } catch (Exception e) {
//                System.out.println("Error SMS "+e);
//                return "Error "+e;
//            }
//        }

}
