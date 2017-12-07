package android.plumberhub.com.plumberhubapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DialogNewTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button btnSave;
    private Button btnClear;
    private Button btnPick;
    DatabaseReference mTrsDatabase;
    Animation animScale;
    Animation animRotate;
    private EditText edtNewCustName;
    private EditText edtNewCustEmail;
    private EditText edtNewServices;
    private EditText edtNewTotalCost;
    private Calendar cal = Calendar.getInstance();
    private int day = 0, month = 0, year = 2017, hour = 0, minute = 0,
            dayFinal = 0, monthFinal = 0, yearFinal = 2017, hourFinal = 0, minuteFinal = 0;

    private void pushNewTrip(){
        String customerName = edtNewCustName.getText().toString();
        String customerEmail = edtNewCustEmail.getText().toString();
        List<String> services = new ArrayList<>();
        for (String s: edtNewServices.getText().toString().split(",")){
            services.add(s);
        }
        double totalCost = Double.parseDouble(edtNewTotalCost.getText().toString());
        Date date = cal.getTime();

        Trip trip = new Trip(customerName, customerEmail, date.getTime(), services, totalCost);

        mTrsDatabase.push().setValue(trip);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_trip);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        mTrsDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("trips");

        btnSave = (Button) findViewById(R.id.btnSaveTrip);
        btnClear = (Button) findViewById(R.id.btnClearTrip);
        btnPick = (Button) findViewById(R.id.btnPickDateTime);
        edtNewCustName = (EditText) findViewById(R.id.edtNewCustName);
        edtNewCustEmail = (EditText) findViewById(R.id.edtNewCustEmail);
        edtNewTotalCost = (EditText) findViewById(R.id.edtNewTotalCost);
        edtNewServices = (EditText) findViewById(R.id.edtNewServices);

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DialogNewTrip.this,
                        R.style.picker, DialogNewTrip.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtNewCustName.setText("");
                edtNewCustEmail.setText("");
                edtNewTotalCost.setText("");
                edtNewServices.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                pushNewTrip();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(DialogNewTrip.this,
                R.style.picker, DialogNewTrip.this, hour, minute, true);
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