package android.plumberhub.com.plumberhubapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Trips extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button btnSave;
    private Button btnClear;
    private Button btnPick;
    DatabaseReference mTrsDatabase;
    private EditText edtNewCustName;
    private EditText edtNewServices;
    private EditText edtNewTotalCost;
    private Calendar cal = Calendar.getInstance();
    private ListView lvTrips;
    private int day = 0, month = 0, year = 2017, hour = 0, minute = 0,
            dayFinal = 0, monthFinal = 0, yearFinal = 2017, hourFinal = 0, minuteFinal = 0;

    private void pushNewTrip(){
        String customerName = edtNewCustName.getText().toString();
        List<String> services = new ArrayList<>();
        for (String s: edtNewServices.getText().toString().split(",")){
            services.add(s);
        }
        double totalCost = Double.parseDouble(edtNewTotalCost.getText().toString());
        Date date = cal.getTime();
        Trip trip = new Trip(customerName, date, services, totalCost);

        mTrsDatabase.push().setValue(trip);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        mTrsDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://plumber-hub.firebaseio.com/trips");

        lvTrips = (ListView) findViewById(R.id.lvTrips);
        btnSave = (Button) findViewById(R.id.btnSaveTrip);
        btnClear = (Button) findViewById(R.id.btnClearTrip);
        btnPick = (Button) findViewById(R.id.btnPickDateTime);
        edtNewCustName = (EditText) findViewById(R.id.edtNewCustName);
        edtNewTotalCost = (EditText) findViewById(R.id.edtNewTotalCost);
        edtNewServices = (EditText) findViewById(R.id.edtNewServices);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Trips.this, Trips.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtNewCustName.setText("");
                edtNewTotalCost.setText("");
                edtNewServices.setText("");
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewTrip();
            }
        });

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month + 1;
        dayFinal = day;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Trips.this, Trips.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        cal.set(Calendar.YEAR, yearFinal);
        cal.set(Calendar.MONTH, monthFinal);
        cal.set(Calendar.DAY_OF_MONTH, dayFinal);
        cal.set(Calendar.HOUR_OF_DAY, hourFinal);
        cal.set(Calendar.MINUTE, minuteFinal);
    }
}
