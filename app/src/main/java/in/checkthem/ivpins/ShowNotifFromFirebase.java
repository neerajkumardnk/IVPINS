package in.checkthem.ivpins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowNotifFromFirebase extends AppCompatActivity {


    public RecyclerView recyclerView;
    public   DatabaseReference databaseReference;
    public   DatabaseReference mCurrentUser;
    public Query mQueryCurrentUser;
    private  String complaints="complaints";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notif_from_firebase);
        String v = SharedPrefManager.getInstance(ShowNotifFromFirebase.this).getVehicleNumber();

        // Initialize references to views
        progressDialog=new ProgressDialog(ShowNotifFromFirebase.this);
        progressDialog.setMessage("Loading");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("complaints");
        mCurrentUser=FirebaseDatabase.getInstance().getReference().child("complaints");
        mQueryCurrentUser=mCurrentUser.orderByChild("to").equalTo(v);
        recyclerView=(RecyclerView)findViewById(R.id.list);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        progressDialog.dismiss();


        FirebaseRecyclerAdapter<SG, CViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter <SG, CViewHolder>(
                SG.class,
                R.layout.list_row,
                CViewHolder.class,
                mQueryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(CViewHolder viewHolder, SG model, int position) {

                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setStatus(model.getStatus());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(ShowNotifFromFirebase.this, in.checkthem.ivpins.SinglePost.class);
                        intent.putExtra("blog_Id",post_key);
                        startActivity(intent);

                    }
                });
                
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }


    public  static  class CViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public  void setTitle(String title){
            TextView title_textView=(TextView)mView.findViewById(R.id.title_textView);
            title_textView.setText(title);
        }
        public  void setMessage(String msg){
            TextView message_textView=(TextView)mView.findViewById(R.id.message_textView);
            message_textView.setText(msg);
        }
        public void setStatus(String status){
            TextView status_textView=(TextView)mView.findViewById(R.id.status_textView);
            status_textView.setText(status);
        }
    }

}
