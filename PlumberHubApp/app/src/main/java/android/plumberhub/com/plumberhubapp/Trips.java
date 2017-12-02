package android.plumberhub.com.plumberhubapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.plumberhub.com.plumberhubapp.POJOs.Trip;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Trips extends AppCompatActivity {

    private Button btnAddTrip;
    DatabaseReference mTrsDatabase;
    Animation animScale;
    private FirebaseAuth firebaseAuth;
    private ListView lvTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        firebaseAuth = FirebaseAuth.getInstance();
        mTrsDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://plumber-hub.firebaseio.com/trips");

        lvTrips = (ListView) findViewById(R.id.lvTrips);
        btnAddTrip = (Button) findViewById(R.id.btnAddTrip);

        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Trips.this, DialogNewTrip.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Trips.this, "Authentication required!", Toast.LENGTH_LONG).show();
                }
            }
        });

        final FirebaseListAdapter<Trip> firebaseTrsListAdapter = new FirebaseListAdapter<Trip>(
                this,
                Trip.class,
                R.layout.widget_trips,
                mTrsDatabase.orderByChild("totalCost")
        ) {
            @Override
            protected void populateView(View v, Trip model, int position) {
                TextView txtCustName = (TextView) v.findViewById(R.id.txtCustName);
                TextView txtServices = (TextView) v.findViewById(R.id.txtServices);
                TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
                TextView txtTotalCost = (TextView) v.findViewById(R.id.txtTotalCost);

                txtCustName.setText(model.getCustomerName());
                txtServices.setText(String.valueOf("Services: " + TextUtils.join(", ", model.getServices())));
                String formattedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()).format(model.getDate());
                txtDate.setText(formattedDate);
                txtTotalCost.setText(String.valueOf("$" + model.getTotalCost()));
            }
        };

        lvTrips.setAdapter(firebaseTrsListAdapter);
        lvTrips.setLongClickable(true);
        lvTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                firebaseTrsListAdapter.getRef(pos).removeValue();
                return true;
            }
        });
    }
}
