package android.plumberhub.com.plumberhubapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import static android.plumberhub.com.plumberhubapp.Tools.toolToEdit;

public class DialogEditTool extends AppCompatActivity {

    private Animation animScale;
    private EditText edtEditTool;
    private Button btnSaveTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_tool);

        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        edtEditTool = (EditText) findViewById(R.id.edtEditTool);
        edtEditTool.setText((String) getIntent().getSerializableExtra("tool"));
        btnSaveTool = (Button) findViewById(R.id.btnSaveEditedTool);

        btnSaveTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                toolToEdit.setValue(edtEditTool.getText().toString());
                finish();
            }
        });
    }
}
