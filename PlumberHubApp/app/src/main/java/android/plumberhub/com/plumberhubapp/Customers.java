package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.plumberhub.com.plumberhubapp.POJOs.Customer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
    private FirebaseListAdapter<Customer> firebaseCustListAdapter;
    static DatabaseReference customerToEdit;

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
        startActivity(Intent.createChooser(intent, "Choose an email client:"));
    }

    private void sendText(String number, String text){
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }

    private void callNumber(String number){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
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

        firebaseCustListAdapter = new FirebaseListAdapter<Customer>(
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
            }
        };

        lvCustomers.setAdapter(firebaseCustListAdapter);
        registerForContextMenu(lvCustomers);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvCust) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Customer customer = (Customer) lvCustomers.getItemAtPosition(info.position);
            menu.setHeaderTitle(customer.getName());
            String[] menuItems = getResources().getStringArray(R.array.cus_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.cus_menu);
        String menuItemName = menuItems[menuItemIndex];
        Customer customer = (Customer) lvCustomers.getItemAtPosition(info.position);

        switch (menuItemName){
            case "Edit":
                Intent intent = new Intent(Customers.this, DialogEditCustomer.class);
                customerToEdit = firebaseCustListAdapter.getRef(info.position);
                intent.putExtra("customer", customer);
                startActivity(intent);
                break;
            case "Delete":
                firebaseCustListAdapter.getRef(info.position).removeValue();
                break;
            case "Call":
                callNumber(customer.getPhone());
                break;
            case "Send Email":
                sendEmail(customer.getEmail(), "Plumber Hub",
                        "Hi! I just wanted to inform you that ... ");
                break;
            case "Send Text":
                sendText(customer.getPhone(), "Hi! I'm texting you because ... ");
                break;
            case "Save Contact":
                saveContact(customer);
                break;
        }
        return true;
    }
}
