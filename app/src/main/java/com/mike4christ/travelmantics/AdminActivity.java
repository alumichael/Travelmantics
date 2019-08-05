package com.mike4christ.travelmantics;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    /** ButterKnife Code **/
    @BindView(R.id.inputLayoutTitle)
    TextInputLayout inputLayoutTitle;
    @BindView(R.id.title_editxt)
    EditText titleEditxt;
    @BindView(R.id.admin_layout)
    LinearLayout admin_layout;
    @BindView(R.id.inputLayoutAmount)
    TextInputLayout inputLayoutAmount;
    @BindView(R.id.amount_editxt)
    EditText amountEditxt;
    @BindView(R.id.inputLayoutDesc)
    TextInputLayout inputLayoutDesc;
    @BindView(R.id.desc_editxt)
    EditText descEditxt;
    @BindView(R.id.select_img)
    Button selectImg;
    @BindView(R.id.image_to_upload)
    ImageView imageToUpload;
    @BindView(R.id.progress)
    AVLoadingIndicatorView progressBar;
    int PICK_IMAGE_REQUEST=1;
    Uri img_uri;
    StorageTask mUploadTask;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    StorageReference storageReference;
    NetworkConnection networkConnection=new NetworkConnection();

    String title,amount,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        setAction();

    }

    private void  setAction(){
        selectImg.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.select_img) {
            chooseImageFile();
        }
    }

    private void chooseImageFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0 || data == null || data.getData() == null) {
            showMessage("No image is selected, try again");
            return;
        }


        img_uri = data.getData();
        if(img_uri!=null) {


            try {

                imageToUpload.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), img_uri));
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("Please Re-pick image");
            }


        }


    }

    private void showMessage(String s) {
        Snackbar.make(admin_layout, s, Snackbar.LENGTH_SHORT).show();
    }

    //App bar Action

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {

            validateUserInputs();
            return true;
        } else if (itemId == R.id.action_log_out) {


            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void validateUserInputs() {

        if (networkConnection.isNetworkConnected(this)) {
            boolean isValid = true;
            if (titleEditxt.getText().toString().isEmpty()) {
                inputLayoutTitle.setError("Holiday title is required!");
                isValid = false;
            } else if (amountEditxt.getText().toString().isEmpty()) {
                inputLayoutAmount.setError("Amount is required");

                isValid = false;

            }else if(descEditxt.getText().toString().isEmpty()) {
                inputLayoutDesc.setError("Description is required!");
                isValid = false;

            } else {
                inputLayoutTitle.setErrorEnabled(false);
                inputLayoutAmount.setErrorEnabled(false);
                inputLayoutDesc.setErrorEnabled(false);
            }
            if(img_uri==null){
                showMessage("Please Pick image");
                isValid = false;
            }

            if (isValid) {
                UploadData();
            }
            return;
        }
        showMessage("No Internet connection discovered!");
    }

    private void UploadData(){

        selectImg.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        title = titleEditxt.getText().toString();
        amount = amountEditxt.getText().toString();
        description = descEditxt.getText().toString();
       // link = "Default";
        storageReference = FirebaseStorage.getInstance().getReference("images");

        if (img_uri != null) {
            StorageReference fileReference = storageReference;

            fileReference = fileReference.child(System.currentTimeMillis()+"."+getFileExtension(img_uri));

            mUploadTask = fileReference.putFile(img_uri);
        /*    // [START rtdb_write_new_user_task]
            mDatabase.child("users").child(userId).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                        }
                    });*/

            final StorageReference finalFileReference = fileReference;
            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        return finalFileReference.getDownloadUrl();
                    }
                    throw task.getException();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                                         @Override
                                         public void onComplete(@NonNull Task task) {
                                             if (task.isSuccessful()) {
                                                 String mUri = (task.getResult()).toString();
                                                  Model data = new Model(mUri, title, description, amount);
                                                 final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                 reference.child("Users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                         reference.child("Users").child(String.valueOf(data.holidy_title)).setValue(data)

                                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                     @Override
                                                                     public void onSuccess(Void aVoid) {
                                                                         showMessage("Uploaded Successfully");
                                                                         if (firebaseAuth.getCurrentUser() != null) {
                                                                             progressBar.setVisibility(View.GONE);
                                                                         }
                                                                         startActivity(new Intent(AdminActivity.this,UserActivity.class));
                                                                     }
                                                                 })
                                                                 .addOnFailureListener(new OnFailureListener() {
                                                                     @Override
                                                                     public void onFailure(@NonNull Exception e) {
                                                                         showMessage(e.getMessage());
                                                                         progressBar.setVisibility(View.GONE);
                                                                         selectImg.setVisibility(View.VISIBLE);
                                                                     }
                                                                 });

                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError databaseError) {
                                                         showMessage(databaseError.getMessage());
                                                         Log.i("Error",databaseError.getMessage());
                                                         progressBar.setVisibility(View.GONE);
                                                         selectImg.setVisibility(View.VISIBLE);
                                                     }
                                                 });


                                                 return;
                                             }
                                             showMessage("Upload Failed");
                                             progressBar.setVisibility(View.GONE);
                                             selectImg.setVisibility(View.VISIBLE);


                                         }
                                     }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    selectImg.setVisibility(View.VISIBLE);
                }
            });
            return;
        }


        showMessage("Select Image");
        progressBar.setVisibility(View.GONE);
        selectImg.setVisibility(View.VISIBLE);
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }


}


