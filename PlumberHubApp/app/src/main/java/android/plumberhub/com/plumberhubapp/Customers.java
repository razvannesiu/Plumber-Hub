package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.graphics.Color;
import android.plumberhub.com.plumberhubapp.POJOs.Customer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Customers extends AppCompatActivity {

    private Button btnAddCust;
    DatabaseReference mCusDatabase;
    private ListView lvCustomers;
    private FirebaseAuth firebaseAuth;
    private Animation animScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        firebaseAuth = FirebaseAuth.getInstance();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(firebaseAuth.getCurrentUser().getUid()).child("customers");
        lvCustomers = (ListView) findViewById(R.id.lvCust);
        btnAddCust = (Button) findViewById(R.id.btnAddCustomer);

        btnAddCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Customers.this, DialogNewCustomer.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Customers.this, "Authentication required!", Toast.LENGTH_LONG).show();
                }
            }
        });

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
                v.setBackgroundColor(position % 2 != 0? Color.LTGRAY : Color.WHITE);
            }
        };

        lvCustomers.setAdapter(firebaseCustListAdapter);
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
