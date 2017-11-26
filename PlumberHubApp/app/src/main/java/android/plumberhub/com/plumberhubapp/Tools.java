package android.plumberhub.com.plumberhubapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Tools extends AppCompatActivity {

    DatabaseReference mDatabase;
    private Button btnSave;
    private EditText edtNewTool;
    private ListView lvTools;

    private void pushNewTool(){
        mDatabase.push().setValue(edtNewTool.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://plumber-hub.firebaseio.com/tools");

        btnSave = (Button) findViewById(R.id.btnSaveTool);
        edtNewTool = (EditText) findViewById(R.id.edtNewTool);
        lvTools = (ListView) findViewById(R.id.lvTools);

        final FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewTool();
            }
        });

//        SwipeDismissListViewTouchListener touchListener =
//                new SwipeDismissListViewTouchListener(
//                        lvTools,
//                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
//                            @Override
//                            public boolean canDismiss(int position) {
//                                return true;
//                            }
//
//                            @Override
//                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//                                for (int position : reverseSortedPositions) {
//                                    firebaseListAdapter.notifyDataSetChanged();
//                                }
//                            }
//                        });
//        lvTools.setOnTouchListener(touchListener);
    }
}
