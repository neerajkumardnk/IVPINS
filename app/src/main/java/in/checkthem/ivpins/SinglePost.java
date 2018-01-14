package in.checkthem.ivpins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePost extends AppCompatActivity implements OnMapReadyCallback {
    public String post_key=null;
    private TextView title_tv,message_tv,status_tv,time_tv,lat_tv,lng_tv;
    private DatabaseReference databaseReference;
    GoogleMap mgoogleMap;
    public  String lat,lng;
    public double lati,lngi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_post);
        initMap();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("complaints");
        post_key = getIntent().getExtras().getString("blog_Id");
        title_tv = (TextView) findViewById(R.id.title_textView);
        message_tv = (TextView) findViewById(R.id.message_textView);
        status_tv = (TextView) findViewById(R.id.status_textView);
        time_tv = (TextView) findViewById(R.id.time_textView);
        //lat_tv=(TextView)findViewById(R.id.lat_textView);
        //lng_tv=(TextView)findViewById(R.id.lng_textView);
    }

    private void initMap() {
        MapFragment mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap=googleMap;
        setOnMap();


    }
    public void setOnMap() {
        if (post_key != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("complaints");
            post_key = getIntent().getExtras().getString("blog_Id");
            databaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String title = (String) dataSnapshot.child("title").getValue();
                    String message = (String) dataSnapshot.child("message").getValue();
                    String status = (String) dataSnapshot.child("status").getValue();
                    String time = (String) dataSnapshot.child("time").getValue();
                    lat = dataSnapshot.child("latitude").getValue().toString().trim();
                    lng = dataSnapshot.child("longitude").getValue().toString().trim();

                    title_tv.setText(title);
                    message_tv.setText(message);
                    status_tv.setText(status);
                    time_tv.setText(time);
                    lat = dataSnapshot.child("latitude").getValue().toString().trim();
                    lng = dataSnapshot.child("longitude").getValue().toString().trim();
                    lati = Double.parseDouble(lat);
                    lngi = Double.parseDouble(lng);
                    LatLng latLng = new LatLng(lati, lngi);
                    mgoogleMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));
                    mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


}
