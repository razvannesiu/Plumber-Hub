package android.plumberhub.com.plumberhubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.plumberhub.com.plumberhubapp.POJOs.Service;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Services extends AppCompatActivity {

    Animation animScale;
    Animation animTranslateRight;
    Animation animTranslateLeft;
    DatabaseReference mDataReference;
    Button btnAddService;
    private FirebaseRecyclerAdapter<Service, ServiceViewHolder> mAdapter;
    private static final int MAX_CARDS = 25;
    private RecyclerView rcvListImg;
    private FirebaseAuth firebaseAuth;
    private static boolean SLIDE_TO_RIGHT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        animTranslateRight = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        animTranslateLeft = AnimationUtils.loadAnimation(this, R.anim.translate_left);

        firebaseAuth = FirebaseAuth.getInstance();
        btnAddService = (Button) findViewById(R.id.btnAddService);

        btnAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Services.this, DialogNewService.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Services.this, "Authentication required!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDataReference = FirebaseDatabase.getInstance().getReference("services");
        Query query = mDataReference.limitToLast(MAX_CARDS);

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
                if(SLIDE_TO_RIGHT){
                    view.startAnimation(animTranslateRight);
                }
                else{
                    view.startAnimation(animTranslateLeft);
                }
                SLIDE_TO_RIGHT = ! SLIDE_TO_RIGHT;
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
