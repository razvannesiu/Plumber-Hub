package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Tools extends AppCompatActivity {

    Animation animScale;
    DatabaseReference mDatabase;
    private Button btnSave;
    private EditText edtNewTool;
    private ListView lvTools;
    private FirebaseAuth firebaseAuth;
    private FirebaseListAdapter<String> firebaseListAdapter;
    static DatabaseReference toolToEdit;


    private void pushNewTool(){
        mDatabase.push().setValue(edtNewTool.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(firebaseAuth.getCurrentUser().getUid()).child("tools");

        btnSave = (Button) findViewById(R.id.btnSaveTool);
        edtNewTool = (EditText) findViewById(R.id.edtNewTool);
        lvTools = (ListView) findViewById(R.id.lvTools);

        firebaseListAdapter = new FirebaseListAdapter<String>(
                this,
                String.class,
                android.R.layout.simple_list_item_1,
                mDatabase
        ) {
            @Override
            protected void populateView(View v, String model, int position) {
                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                textView.setText(model);
            }
        };

        lvTools.setAdapter(firebaseListAdapter);
        registerForContextMenu(lvTools);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);

                if(firebaseAuth.getCurrentUser() != null) {
                    pushNewTool();
                }
                else{
                    Toast.makeText(Tools.this, "Authentication required!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvTools) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(String.valueOf(lvTools.getItemAtPosition(info.position)));
            String[] menuItems = getResources().getStringArray(R.array.tls_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.tls_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = (String) lvTools.getItemAtPosition(info.position);

        switch (menuItemName){
            case "Edit":
                Intent intent = new Intent(Tools.this, DialogEditTool.class);
                intent.putExtra("tool", listItemName);
                toolToEdit = firebaseListAdapter.getRef(info.position);
                startActivity(intent);
                break;
            case "Delete":
                firebaseListAdapter.getRef(info.position).removeValue();
                break;
        }
        return true;
    }

}
