package android.plumberhub.com.plumberhubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.plumberhub.com.plumberhubapp.POJOs.Service;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

import static android.plumberhub.com.plumberhubapp.Services.serviceToEdit;

public class DialogEditService extends AppCompatActivity {

    Animation animRotate;
    Animation animScale;
    StorageReference imageReference;
    StorageReference fileRef;
    ProgressDialog progressDialog;
    DatabaseReference mDataReference;
    EditText edtEditTitle;
    EditText edtEditDescription;
    EditText edtEditTools;
    EditText edtEditPrice;
    Button btnSelectEditedImage;
    Button btnSave;
    Button btnClear;
    private static final int MAX_RANDOM_ID = 10000;
    Uri fileUri;
    private boolean didSelectImage;
    private Service currService;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST);
    }

    private void writeEditedServiceToDB(String url) {
        String title = edtEditTitle.getText().toString();
        String description = edtEditDescription.getText().toString();
        List<String> tools = new ArrayList<String>();
        for (String s : edtEditTools.getText().toString().split(",")) {
            tools.add(s.trim());
        }
        double price = Double.parseDouble(edtEditPrice.getText().toString());
        Service service = new Service(title, url, description, tools, price);

        serviceToEdit.setValue(service);
        finish();
    }

    private void saveEditedService(){
        if (fileUri != null && didSelectImage) {
            progressDialog.show();
            fileRef = imageReference.child("service" + (new Random().nextInt(MAX_RANDOM_ID)) + ".jpg");

            fileRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();

                            // use Firebase Realtime Database to store the Service
                            writeEditedServiceToDB(url);
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
        else{
            writeEditedServiceToDB(currService.getImageUrl());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            didSelectImage = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_service);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        edtEditTitle = (EditText) findViewById(R.id.edtEditTitle);
        edtEditDescription = (EditText) findViewById(R.id.edtEditDescription);
        edtEditTools = (EditText) findViewById(R.id.edtEditTools);
        edtEditPrice = (EditText) findViewById(R.id.edtEditPrice);
        btnSelectEditedImage = (Button) findViewById(R.id.btnSelectEditedImage);
        btnSave = (Button) findViewById(R.id.btnSaveEditedService);
        btnClear = (Button) findViewById(R.id.btnClearEditedService);

        currService = (Service) getIntent().getSerializableExtra("service");
        edtEditTitle.setText(currService.getTitle());
        edtEditDescription.setText(currService.getDescription());
        edtEditTools.setText(TextUtils.join(", ", currService.getTools()));
        edtEditPrice.setText(String.valueOf(currService.getPrice()));
        didSelectImage = false;

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtEditTitle.setText("");
                edtEditDescription.setText("");
                edtEditTools.setText("");
                edtEditPrice.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                saveEditedService();
            }
        });

        btnSelectEditedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                selectImage();
            }
        });

        mDataReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("services");

        imageReference = FirebaseStorage.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images");

        fileRef = null;
        progressDialog = new ProgressDialog(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
    }
}
