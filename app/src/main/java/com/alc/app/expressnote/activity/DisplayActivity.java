package com.alc.app.expressnote.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alc.app.expressnote.model.Entry;
import com.alc.app.expressnote.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView mEntryList;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Query mQueryCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mAuth = FirebaseAuth.getInstance();

        String currentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Entries");
        mDatabase.keepSynced(true);

        mQueryCurrentUser = mDatabase.orderByChild("uid").equalTo(currentUserId);


        mEntryList = (RecyclerView) findViewById(R.id.entry_list);
        mEntryList.setHasFixedSize(true);
        mEntryList.setLayoutManager(new LinearLayoutManager(this));


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(DisplayActivity.this, MainActivity.class));
                }
            }
        };

        if (isNetworkConnected() || isWifiConnected()) {
            Toast.makeText(this, "Network Is Active", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setMessage("It looks like your internet connection is off. Please turn it " +
                            "on or Some features might not work")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(R.drawable.warning).show();
        }

    }

    private boolean isWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Entry, EntryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Entry, EntryViewHolder>(
                Entry.class,
                R.layout.entry_card,
                EntryViewHolder.class,
                mQueryCurrentUser

        ) {
            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, Entry model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setDate(model.getDate());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Not Working
                        Intent nextactivity = new Intent(v.getContext(), EntryActivity.class);

                        v.getContext().startActivity(nextactivity);

                    }
                });
            }

        };

        mEntryList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EntryViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title){

            TextView e_title = (TextView) mView.findViewById(R.id.entry_title);
            e_title.setText(title);

        }

        public void setContent(String content){

            TextView e_content = (TextView) mView.findViewById(R.id.entry_content);
            e_content.setText(content);
        }

        public void setDate(String date){

            TextView e_date = (TextView) mView.findViewById(R.id.entry_date);
            e_date.setText(date);
        }

        public void setImage(Context ctx,String image){

            ImageView e_image = (ImageView) mView.findViewById(R.id.entry_image);
            Picasso.with(ctx).load(image).into(e_image);
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){

            startActivity(new Intent(DisplayActivity.this, EntryActivity.class));

        }
        if (item.getItemId() == R.id.action_logout){
            signout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void signout() {
        mAuth.signOut();
    }
}
