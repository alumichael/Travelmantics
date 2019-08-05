package com.mike4christ.travelmantics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int RC_SIGN_IN = 100;

    //binding widget to view
    @BindView(R.id.user_layout)
    LinearLayout user_layout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_item)
    RecyclerView recycler_item;
    FirebaseAuth firebaseAuth;
    Adapter mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth==null) {
            createSignInIntent();
        }
        init();


    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
               );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
              // init();


            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showMessage("Signing Cancelled");
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showMessage("No Internet Connection");
                    return;
                }


            }
        }
    }


    private void  init(){
        FirebaseDatabase.getInstance().getReference().child("Users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<Model> datas = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    Model data = dataSnapshotChild.getValue(Model.class);
                    datas.add(data);
                }
                getAllPost(datas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Error: "+databaseError.getMessage());

            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

  
    public void getAllPost(List<Model> datas) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        if (datas.size() == 0) {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            Snackbar.make(user_layout, "Empty Post", Snackbar.LENGTH_SHORT).show();
            return;
        }
        mAdapter = new Adapter(this,datas);
        recycler_item.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void showMessage(String s) {
        Snackbar.make(user_layout, s, Snackbar.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_admin) {

                startActivity(new Intent(UserActivity.this,AdminActivity.class));
            return true;
        } else if (itemId == R.id.action_log_out) {

            logout();


        } else if(itemId==R.id.action_share){

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "STI Agent Mobile");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Reaching to customers is our duty!");

            startActivity(Intent.createChooser(sharingIntent, "Share via"));



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
            init();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().signOut();
                            UserActivity.this.finish();
                            return;
                        }
                        Toast.makeText(UserActivity.this, "No user logged in yet!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
