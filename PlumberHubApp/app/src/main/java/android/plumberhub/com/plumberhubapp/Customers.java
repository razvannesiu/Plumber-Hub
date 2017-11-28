package android.plumberhub.com.plumberhubapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Customers extends AppCompatActivity {

    private Button btnSave;
    DatabaseReference mCusDatabase;
    private EditText edtNewName;
    private EditText edtNewPhone;
    private EditText edtNewAddress;
    private EditText edtNewEmail;
    private ListView lvCustomers;

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
        setContentView(R.layout.activity_customers);

        mCusDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://plumber-hub.firebaseio.com/customers");

        lvCustomers = (ListView) findViewById(R.id.lvCust);
        btnSave = (Button) findViewById(R.id.btnSaveCustomer);
        edtNewName = (EditText) findViewById(R.id.edtNewName);
        edtNewPhone = (EditText) findViewById(R.id.edtNewPhone);
        edtNewAddress = (EditText) findViewById(R.id.edtNewAddress);
        edtNewEmail = (EditText) findViewById(R.id.edtNewEmail);

        final FirebaseListAdapter<Customer> firebaseCustListAdapter = new FirebaseListAdapter<Customer>(
                this,
                Customer.class,
                R.layout.widget_customers,
                mCusDatabase
        ) {
            @Override
            protected void populateView(View v, Customer model, int position) {
                TextView txtName = (TextView) v.findViewById(R.id.txtName);
                TextView txtAddress = (TextView) v.findViewById(R.id.txtAddress);
                TextView txtPhone = (TextView) v.findViewById(R.id.txtPhone);
                TextView txtEmail = (TextView) v.findViewById(R.id.txtEmail);

                txtName.setText(model.getName());
                txtAddress.setText(model.getAddress());
                txtPhone.setText(model.getPhone());
                txtEmail.setText(model.getEmail());
                v.setBackgroundColor(position % 2 == 0? Color.LTGRAY : Color.WHITE);
            }
        };

        lvCustomers.setAdapter(firebaseCustListAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewCustomer();
            }
        });

        lvCustomers.setLongClickable(true);
        lvCustomers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                firebaseCustListAdapter.getRef(pos).removeValue();
                return true;
            }
        });
    }
}
