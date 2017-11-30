package android.plumberhub.com.plumberhubapp;

import android.view.View;

/**
 * Created by razva on 2017-11-29.
 */

public interface ClickListener {
    public void onClick(View view, int position);
    public void onLongClick(View view, int position);
}
