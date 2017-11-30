package android.plumberhub.com.plumberhubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Services extends AppCompatActivity {

    StorageReference imageReference;
    StorageReference fileRef;
    ProgressDialog progressDialog;
    DatabaseReference mDataReference;
    EditText edtNewTitle;
    EditText edtNewDescription;
    EditText edtNewTools;
    EditText edtNewPrice;
    Button btnUploadImage;
    Button btnSave;
    Button btnClear;
    private FirebaseRecyclerAdapter<Service, ServiceViewHolder> mAdapter;
    private static final int MAX_CARDS = 25;
    private static final int MAX_RANDOM_ID = 10000;
    private static int ID = 1;
    private RecyclerView rcvListImg;
    Uri fileUri;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;

    private void chooseFile(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST);
    }

    private void writeNewServiceToDB(String url) {
        String title = edtNewTitle.getText().toString();
        String description = edtNewDescription.getText().toString();
        List<String> tools = new ArrayList<String>();
        for (String s : edtNewTools.getText().toString().replaceAll("\\s+", "").split(",")) {
            tools.add(s);
        }
        double price = Double.parseDouble(edtNewPrice.getText().toString());
        Service service = new Service(title, url, description, tools, price);

        String key = mDataReference.push().getKey();
        mDataReference.child(key).setValue(service);
    }

    private void saveNewService(View v){
        if (fileUri != null) {
            progressDialog.setTitle("Uploading...");
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
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
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
        setContentView(R.layout.activity_services);

        edtNewTitle = (EditText) findViewById(R.id.edtNewTitle);
        edtNewDescription = (EditText) findViewById(R.id.edtNewDescription);
        edtNewTools = (EditText) findViewById(R.id.edtNewTools);
        edtNewPrice = (EditText) findViewById(R.id.edtNewPrice);
        btnUploadImage = (Button) findViewById(R.id.btnUploadImage);
        btnSave = (Button) findViewById(R.id.btnSaveService);
        btnClear = (Button) findViewById(R.id.btnClearService);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtNewTitle.setText("");
                edtNewDescription.setText("");
                edtNewTools.setText("");
                edtNewPrice.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewService(v);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile(v);
            }
        });

        mDataReference = FirebaseDatabase.getInstance().getReference("services");
        imageReference = FirebaseStorage.getInstance().getReference().child("images");
        Query query = mDataReference.limitToLast(MAX_CARDS);
        fileRef = null;
        progressDialog = new ProgressDialog(this);

        mAdapter = new FirebaseRecyclerAdapter<Service, ServiceViewHolder>(
                Service.class, R.layout.widget_services, ServiceViewHolder.class, query) {
            @Override
            protected void populateViewHolder(ServiceViewHolder viewHolder, Service model, int position) {
                viewHolder.txtTitle.setText(model.getTitle());
                viewHolder.txtDescription.setText(model.getDescription());
                viewHolder.txtTools.setText(String.valueOf("Tools: " + TextUtils.join(", ", model.getTools())));
                viewHolder.txtPrice.setText(String.valueOf("$" + model.getPrice()));

                Glide.with(Services.this)
                        .load(model.getImageUrl())
                        .error(android.R.drawable.stat_sys_warning)
                        .into(viewHolder.imageView);
            }
        };

        rcvListImg = (RecyclerView) findViewById(R.id.rcv_list_img);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        rcvListImg.setHasFixedSize(false);
        rcvListImg.setLayoutManager(layoutManager);

        rcvListImg.addOnItemTouchListener(new RecyclerTouchListener(this,
                rcvListImg, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //...
            }

            @Override
            public void onLongClick(View view, int position) {
                mAdapter.getRef(position).removeValue();
            }
        }));

        rcvListImg.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdapter.cleanup();
    }
}
