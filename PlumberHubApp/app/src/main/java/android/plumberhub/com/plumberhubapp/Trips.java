package android.plumberhub.com.plumberhubapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.plumberhub.com.plumberhubapp.POJOs.Trip;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.plumberhub.com.plumberhubapp.Trips.MAX_RANDOM_ID;
import static android.plumberhub.com.plumberhubapp.Trips.arrayOfImages;
import static android.plumberhub.com.plumberhubapp.Trips.document;
import static android.plumberhub.com.plumberhubapp.Trips.pdfUri;

public class Trips extends AppCompatActivity implements TaskCompletionHandler {

    private Button btnAddTrip;
    DatabaseReference mTrsDatabase;
    Animation animScale;
    Animation animRotate;
    private FirebaseAuth firebaseAuth;
    private ListView lvTrips;
    private final int CAMERA_REQ_CODE = 4632;
    static final List<byte[]> listOfImages = new ArrayList<byte[]>();
    static final int MAX_RANDOM_ID = 10000;
    private ProgressDialog mProgress;
    static byte[][] arrayOfImages;
    static Document document;
    static Trip currentTrip;
    static Uri pdfUri;

    void uploadPdf(Uri uri){
        StorageReference pdfStorageRef = FirebaseStorage.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("invoices")
                .child(uri.getLastPathSegment());

        mProgress.show();

        pdfStorageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
            }
        });
    }

    void emailInvoice(Uri pdf, String email, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, pdf);
        startActivity(Intent.createChooser(intent, "Choose an email client:"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Uploading ...");
        animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        firebaseAuth = FirebaseAuth.getInstance();
        mTrsDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(firebaseAuth.getCurrentUser().getUid()).child("trips");

        lvTrips = (ListView) findViewById(R.id.lvTrips);
        btnAddTrip = (Button) findViewById(R.id.btnAddTrip);

        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(Trips.this, DialogNewTrip.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Trips.this, "Authentication required!", Toast.LENGTH_LONG).show();
                }
            }
        });

        final FirebaseListAdapter<Trip> firebaseTrsListAdapter = new FirebaseListAdapter<Trip>(
                this,
                Trip.class,
                R.layout.widget_trips,
                mTrsDatabase.orderByChild("time")
        ) {
            @Override
            protected void populateView(View v, Trip model, int position) {
                TextView txtCustName = (TextView) v.findViewById(R.id.txtCustName);
                TextView txtCustEmail = (TextView) v.findViewById(R.id.txtCustEmail);
                TextView txtServices = (TextView) v.findViewById(R.id.txtServices);
                TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
                TextView txtTotalCost = (TextView) v.findViewById(R.id.txtTotalCost);
                Button btnClearImages = (Button) v.findViewById(R.id.btnClearImages);
                Button btnTakePicture = (Button) v.findViewById(R.id.btnTakePic);
                Button btnEmailInvoice = (Button) v.findViewById(R.id.btnEmailInvoice);

                txtCustName.setText(model.getCustomerName());
                txtCustEmail.setText(model.getCustomerEmail());
                txtServices.setText(String.valueOf("Services: " + TextUtils.join(", ", model.getServices())));
                txtDate.setText( new SimpleDateFormat("EEE, d MMM yyyy HH:mm",
                        Locale.getDefault()).format(new Date(model.getTime())));
                txtTotalCost.setText(String.valueOf("$" + model.getTotalCost()));
                final Trip currModel = model;

                btnClearImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animRotate);
                        Toast.makeText(Trips.this, (listOfImages.size() == 1? "One image":
                                listOfImages.size() + " images")
                                + " cleared!", Toast.LENGTH_LONG).show();
                        listOfImages.clear();
                    }
                });

                btnTakePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        File imageFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
                        Uri mPhotoUri = FileProvider.getUriForFile(Trips.this,
                                Trips.this.getApplicationContext().getPackageName() +
                                        ".provider", imageFile);
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(intent, CAMERA_REQ_CODE);
                    }
                });

                btnEmailInvoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animScale);
                        currentTrip = currModel;
                        new BuildPdfTask().execute(Trips.this);
                    }
                });
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                return super.getView(getCount() - position - 1, view, viewGroup);
            }
        };

        lvTrips.setAdapter(firebaseTrsListAdapter);
        lvTrips.setLongClickable(true);
        lvTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                firebaseTrsListAdapter.getRef(firebaseTrsListAdapter.getCount() - pos - 1).removeValue();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQ_CODE && resultCode == RESULT_OK){
            File imageFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
            Uri mPhotoUri = FileProvider.getUriForFile(Trips.this,
                    Trips.this.getApplicationContext().getPackageName() +
                            ".provider", imageFile);
            InputStream inputStream = null;
            byte[] inputData = null;
            try {
                inputStream = getContentResolver().openInputStream(mPhotoUri);
                inputData = getBytes(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            listOfImages.add(inputData);
            arrayOfImages = listOfImages.toArray(new byte[listOfImages.size()][]);
            Toast.makeText(Trips.this, "There "
                    + (listOfImages.size() == 1? "is one image": "are " + listOfImages.size() + " images")
                    + " ready!", Toast.LENGTH_LONG).show();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onTaskComplete() {
        String name = currentTrip.getCustomerName().contains(" ")?
                currentTrip.getCustomerName().split(" ")[0] : currentTrip.getCustomerName();
        String formattedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm",
                Locale.getDefault()).format(new Date(currentTrip.getTime()));

        emailInvoice(pdfUri, currentTrip.getCustomerEmail(),"Invoice - " + formattedDate,
                "Hi " + name +
                        ",\n\nThis is your invoice for the " +
                        "trip that took place on the date of " + formattedDate +
                        ". Your total cost is $" + currentTrip.getTotalCost() +
                        " for the services: " + TextUtils.join(", ", currentTrip.getServices()) +
                        ". Please find the invoice attached below.\n\nThank you," +
                        "\nPlumber Hub");
        uploadPdf(pdfUri);
    }
}

class BuildPdfTask extends AsyncTask<TaskCompletionHandler, Image[], Image[]> {

    private TaskCompletionHandler taskCompletionHandler;

    @Override
    protected Image[] doInBackground(TaskCompletionHandler... taskCompletionHandlers) {
        Image[] images = new Image[arrayOfImages.length];
        try {
            for(int i = 0; i < arrayOfImages.length; i++){
                images[i] = Image.getInstance(arrayOfImages[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        this.taskCompletionHandler = taskCompletionHandlers[0];
        return images;
    }

    @Override
    protected void onPostExecute(Image... images) {
        document = new Document();
        String dirpath = android.os.Environment.getExternalStorageDirectory().toString();
        File pdf = new File(dirpath + "/invoice"+
                (new Random().nextInt(MAX_RANDOM_ID)) + ".pdf");
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.open();

        for(Image image: images) {
            Image img = Image.getInstance(image);

            img.setRotationDegrees(-90);
            float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float height = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            img.scaleToFit(width, height);

            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

            try {
                document.add(img);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

        document.close();
        pdfUri = Uri.fromFile(pdf);
        this.taskCompletionHandler.onTaskComplete();
    }

}
