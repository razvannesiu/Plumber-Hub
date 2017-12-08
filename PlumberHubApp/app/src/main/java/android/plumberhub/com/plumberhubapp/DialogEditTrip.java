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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.plumberhub.com.plumberhubapp.Trips.tripToEdit;

public class DialogEditTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button btnSave;
    private Button btnClear;
    private Button btnPick;
    Animation animScale;
    Animation animRotate;
    private EditText edtEditCustName;
    private EditText edtEditCustEmail;
    private EditText edtEditServices;
    private EditText edtEditTotalCost;
    private Calendar cal = Calendar.getInstance();
    private int day = 0, month = 0, year = 2017, hour = 0, minute = 0,
            dayFinal = 0, monthFinal = 0, yearFinal = 2017, hourFinal = 0, minuteFinal = 0;

    private void editTrip(){
        String customerName = edtEditCustName.getText().toString();
        String customerEmail = edtEditCustEmail.getText().toString();
        List<String> services = new ArrayList<>();
        for (String s: edtEditServices.getText().toString().split(",")){
            services.add(s);
        }
        double totalCost = Double.parseDouble(edtEditTotalCost.getText().toString());
        Date date = cal.getTime();

        Trip trip = new Trip(customerName, customerEmail, date.getTime(), services, totalCost);

        tripToEdit.setValue(trip);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_trip);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        btnSave = (Button) findViewById(R.id.btnSaveEditedTrip);
        btnClear = (Button) findViewById(R.id.btnClearEditedTrip);
        btnPick = (Button) findViewById(R.id.btnPickEditedDateTime);
        edtEditCustName = (EditText) findViewById(R.id.edtEditCustName);
        edtEditCustEmail = (EditText) findViewById(R.id.edtEditCustEmail);
        edtEditTotalCost = (EditText) findViewById(R.id.edtEditTotalCost);
        edtEditServices = (EditText) findViewById(R.id.edtEditServices);

        Trip trip = (Trip) getIntent().getSerializableExtra("trip");
        edtEditCustName.setText(trip.getCustomerName());
        edtEditCustEmail.setText(trip.getCustomerEmail());
        edtEditTotalCost.setText(String.valueOf(trip.getTotalCost()));
        cal.setTime(new Date(trip.getTime()));
        edtEditServices.setText(TextUtils.join(", ", trip.getServices()));

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DialogEditTrip.this,
                        R.style.picker, DialogEditTrip.this, year, month, day);
                datePickerDialog.show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtEditCustName.setText("");
                edtEditCustEmail.setText("");
                edtEditTotalCost.setText("");
                edtEditServices.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                editTrip();
                finish();
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(DialogEditTrip.this,
                R.style.picker, DialogEditTrip.this, hour, minute, true);
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
