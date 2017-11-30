package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //apply new font for title
        Typeface oleoScript = Typeface.createFromAsset(getAssets(), "Fonts/OleoScript-Bold.ttf");
        TextView tvTitle = (TextView) findViewById(R.id.main_menu);
        tvTitle.setTypeface(oleoScript);
    }

    /**
     * Method that handles the Offline MultiPlayer & Single Player buttons from the Start Menu.
     *
     * @param view View for this handler.
     */
    public void startMenuBtnHandler(View view) {
        Intent intent = null;

        String tag = view.getTag().toString();

        switch (tag) {
            case "Customers":
                intent = new Intent(MainActivity.this, Customers.class);
                break;
            case "Services":
                intent = new Intent(MainActivity.this, Services.class);
                break;
            case "Tools":
                intent = new Intent(MainActivity.this, Tools.class);
                break;
            case "Trips":
                intent = new Intent(MainActivity.this, Trips.class);
                break;
        }

        startActivity(intent);
    }
}
