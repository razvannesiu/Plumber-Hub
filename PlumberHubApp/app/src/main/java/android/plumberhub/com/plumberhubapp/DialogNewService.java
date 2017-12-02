package android.plumberhub.com.plumberhubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.plumberhub.com.plumberhubapp.POJOs.Service;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DialogNewService extends AppCompatActivity {

    Animation animRotate;
    Animation animScale;
    StorageReference imageReference;
    StorageReference fileRef;
    ProgressDialog progressDialog;
    DatabaseReference mDataReference;
    EditText edtNewTitle;
    EditText edtNewDescription;
    EditText edtNewTools;
    EditText edtNewPrice;
    Button btnSelectImage;
    Button btnSave;
    Button btnClear;
    private static final int MAX_RANDOM_ID = 10000;
    Uri fileUri;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST);
    }

    private void writeNewServiceToDB(String url) {
        String title = edtNewTitle.getText().toString();
        String description = edtNewDescription.getText().toString();
        List<String> tools = new ArrayList<String>();
        for (String s : edtNewTools.getText().toString().split(",")) {
            tools.add(s);
        }
        double price = Double.parseDouble(edtNewPrice.getText().toString());
        Service service = new Service(title, url, description, tools, price);

        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(service);
    }

    private void saveNewService(){
        if (fileUri != null) {
            progressDialog.show();
            fileRef = imageReference.child("service" + (new Random().nextInt(MAX_RANDOM_ID)) + ".jpg");

            fileRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();

                            // use Firebase Realtime Database to store the Service
                            writeNewServiceToDB(url);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // ...
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            // percentage in progress dialog
                            progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            // ...
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_service);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        edtNewTitle = (EditText) findViewById(R.id.edtNewTitle);
        edtNewDescription = (EditText) findViewById(R.id.edtNewDescription);
        edtNewTools = (EditText) findViewById(R.id.edtNewTools);
        edtNewPrice = (EditText) findViewById(R.id.edtNewPrice);
        btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
        btnSave = (Button) findViewById(R.id.btnSaveService);
        btnClear = (Button) findViewById(R.id.btnClearService);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtNewTitle.setText("");
                edtNewDescription.setText("");
                edtNewTools.setText("");
                edtNewPrice.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                saveNewService();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                selectImage();
            }
        });

        mDataReference = FirebaseDatabase.getInstance().getReference("services");
        imageReference = FirebaseStorage.getInstance().getReference().child("images");
        fileRef = null;
        progressDialog = new ProgressDialog(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
    }
}
