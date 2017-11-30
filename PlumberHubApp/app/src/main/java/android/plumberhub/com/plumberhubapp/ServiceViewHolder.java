package android.plumberhub.com.plumberhubapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by razva on 2017-11-28.
 */

public class ServiceViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtTools;
        public TextView txtPrice;
        public ImageView imageView;

        public ServiceViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.txtServiceTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtTools = (TextView) itemView.findViewById(R.id.txtServiceTools);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            imageView = (ImageView) itemView.findViewById(R.id.imgService);
        }
}
