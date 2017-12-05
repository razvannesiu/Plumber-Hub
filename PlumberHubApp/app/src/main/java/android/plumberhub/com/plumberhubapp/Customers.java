package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.net.Uri;
import android.plumberhub.com.plumberhubapp.POJOs.Customer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static android.provider.ContactsContract.CommonDataKinds.Email.TYPE_WORK;
import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static android.provider.ContactsContract.Intents.Insert.EMAIL_TYPE;
import static android.provider.ContactsContract.Intents.Insert.NAME;
import static android.provider.ContactsContract.Intents.Insert.PHONE;
import static android.provider.ContactsContract.Intents.Insert.PHONE_TYPE;

public class Customers extends AppCompatActivity {

    private Button btnAddCust;
    DatabaseReference mCusDatabase;
    private ListView lvCustomers;
    private FirebaseAuth firebaseAuth;
    private Animation animScale;

    private void saveContact(Customer customer){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(EMAIL, customer.getEmail())
                .putExtra(EMAIL_TYPE, TYPE_WORK)
                .putExtra(PHONE, customer.getPhone())
                .putExtra(PHONE_TYPE, TYPE_WORK)
                .putExtra(NAME, customer.getName());

        startActivity(intent);
    }

    private void sendEmail(String email, String subject, String message){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Choose an Email client:"));
    }

    private void sendText(String number, String text){
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }

    private void callNumber(String number){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }

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
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Customers.this, DialogNewCustomer.class);
                    startActivity(intent);
                } else {
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
                Button btnCall = (Button) v.findViewById(R.id.btnCall);
                Button btnSaveContact = (Button) v.findViewById(R.id.btnSaveContact);
                Button btnSendText = (Button) v.findViewById(R.id.btnSendText);
                Button btnSendEmail = (Button) v.findViewById(R.id.btnSendEmail);
                final Customer customer = model;

                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        callNumber(customer.getPhone());
                    }
                });

                btnSendText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        sendText(customer.getPhone(), "Hi! I'm texting you because ... ");
                    }
                });

                btnSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        sendEmail(customer.getEmail(), "Plumber Hub",
                                "Hi! I just wanted to inform you that ... ");
                    }
                });

                btnSaveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        saveContact(customer);
                    }
                });

                txtName.setText(model.getName());
                txtAddress.setText(model.getAddress());
                txtPhone.setText(model.getPhone());
                txtEmail.setText(model.getEmail());
                //v.setBackgroundColor(position % 2 != 0? Color.LTGRAY : Color.WHITE);
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
