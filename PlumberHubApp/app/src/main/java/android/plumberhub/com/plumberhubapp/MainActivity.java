package android.plumberhub.com.plumberhubapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth;
    private Animation animAlpha;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "User " + firebaseAuth.getCurrentUser().getEmail() + " is authenticated!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Authentication failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);

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
        view.startAnimation(animAlpha);
        Intent intent = null;

        String tag = view.getTag().toString();
        boolean shouldNavigate = true;

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
            case "Log Out":
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "User logged out!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                shouldNavigate = false;
                break;
            case "Log In":
                if(firebaseAuth.getCurrentUser() != null) {
                    //user logged in
                    Toast.makeText(this, "User " + firebaseAuth.getCurrentUser().getEmail() + " is authenticated!", Toast.LENGTH_LONG).show();
                }
                else{
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                            .setProviders(AuthUI.GOOGLE_PROVIDER, AuthUI.EMAIL_PROVIDER).build(), RC_SIGN_IN);
                }
                shouldNavigate = false;
                break;
        }

        if(shouldNavigate) {
            if(firebaseAuth.getCurrentUser() != null){
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Authentication required!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
