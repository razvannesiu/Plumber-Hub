package android.plumberhub.com.plumberhubapp;

import android.plumberhub.com.plumberhubapp.POJOs.Customer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import static android.plumberhub.com.plumberhubapp.Customers.customerToEdit;

public class DialogEditCustomer extends AppCompatActivity {
    private Button btnSave;
    private Button btnClear;
    private EditText edtEditName;
    private EditText edtEditPhone;
    private EditText edtEditAddress;
    private EditText edtEditEmail;
    private Animation animRotate;
    private Animation animScale;

    private void editCustomer(){
        String name = edtEditName.getText().toString();
        String address = edtEditAddress.getText().toString();
        String phone = edtEditPhone.getText().toString();
        String email = edtEditEmail.getText().toString();
        Customer customer = new Customer(name, address, phone, email);

        customerToEdit.setValue(customer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_customer);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        btnSave = (Button) findViewById(R.id.btnSaveEditedCustomer);
        btnClear = (Button) findViewById(R.id.btnClearEditedCustomer);
        edtEditName = (EditText) findViewById(R.id.edtEditName);
        edtEditPhone = (EditText) findViewById(R.id.edtEditPhone);
        edtEditAddress = (EditText) findViewById(R.id.edtEditAddress);
        edtEditEmail = (EditText) findViewById(R.id.edtEditEmail);

        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        edtEditName.setText(customer.getName());
        edtEditPhone.setText(customer.getPhone());
        edtEditAddress.setText(customer.getAddress());
        edtEditEmail.setText(customer.getEmail());

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animRotate);
                edtEditName.setText("");
                edtEditAddress.setText("");
                edtEditEmail.setText("");
                edtEditPhone.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                editCustomer();
                finish();
            }
        });
    }
}
