package android.plumberhub.com.plumberhubapp;

import android.plumberhub.com.plumberhubapp.POJOs.Customer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;

public class DialogNewCustomer extends AppCompatActivity {

    private Button btnSave;
    private Button btnClear;
    DatabaseReference mCusDatabase;
    private EditText edtNewName;
    private EditText edtNewPhone;
    private EditText edtNewAddress;
    private EditText edtNewEmail;
    private Animation animRotate;
    private Animation animScale;

    private void pushNewCustomer(){
        String name = edtNewName.getText().toString();
        String address = edtNewAddress.getText().toString();
        String phone = edtNewPhone.getText().toString();
        String email = edtNewEmail.getText().toString();
        Customer customer = new Customer(name, address, phone, email);

        mCusDatabase.push().setValue(customer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_customer);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("customers");

        btnSave = (Button) findViewById(R.id.btnSaveCustomer);
        btnClear = (Button) findViewById(R.id.btnClearCustomer);
        edtNewName = (EditText) findViewById(R.id.edtNewName);
        edtNewPhone = (EditText) findViewById(R.id.edtNewPhone);
        edtNewAddress = (EditText) findViewById(R.id.edtNewAddress);
        edtNewEmail = (EditText) findViewById(R.id.edtNewEmail);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtNewName.setText("");
                edtNewAddress.setText("");
                edtNewEmail.setText("");
                edtNewPhone.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                pushNewCustomer();
            }
        });
    }
}
