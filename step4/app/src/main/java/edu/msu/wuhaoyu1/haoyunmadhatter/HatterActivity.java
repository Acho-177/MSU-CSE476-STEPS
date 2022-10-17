package edu.msu.wuhaoyu1.haoyunmadhatter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class HatterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hatter);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hats_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        getSpinner().setAdapter(adapter);

        getSpinner().setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                getHatterView().setHat(pos);

                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });

        /*
         * Restore any state
         */
        if (savedInstanceState != null){
            getHatterView().getFromBundle(PARAMETERS, savedInstanceState);
            getSpinner().setSelection(getHatterView().getHat());
        }

        updateUI();
    }


    /**
     * Handle a Picture button press
     * @param view
     */
    public void onPicture(View view) {
        // Get a picture from the gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        pickImageResultLauncher.launch(intent);
    }

    /**
     * Handle a feather checkbox press
     * @param view
     */
    public void onFeather(View view) {
        getHatterView().setDrawthefeather(!getHatterView().GetDrawthefeather());
        updateUI();
    }

    /**
     * Handle a Color button press
     * @param view
     */
    public void onColor(View view) {
        // Get a new color for the hat from the color select activity
        Intent intent = new Intent(this, ColorSelectActivity.class);
        pickColorResultLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    ActivityResultLauncher pickImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                /*
                 * Method called when we get a result from some external
                 * activity called with the launcher
                 * @param result a result of from the activity
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //
                        //	Do get the image and do something with it.
                        // Response from the picture selection activity
                        Intent data = result.getData();
                        Uri imageUri = data.getData();

                        // We have to query the database to determine the document ID for the image
                        Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
                        cursor.moveToFirst();
                        String document_id = cursor.getString(0);
                        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
                        cursor.close();

                        // Next, we query the content provider to find the path for this
                        // document id.
                        cursor = getContentResolver().query(
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                        cursor.moveToFirst();
                        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        cursor.close();

                        if(path != null) {
                            Log.i("Path", path);
                            getHatterView().setImagePath(path);
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> pickColorResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
                        getHatterView().setColor(color);

                        updateUI();
                    }
                }
            });

    private static final String PARAMETERS = "parameters";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getHatterView().putToBundle(PARAMETERS, outState);
    }

    /**
     * The hatter view object
     */
    private HatterView getHatterView() {
        return (HatterView) findViewById(R.id.hatterView);
    }

    /**
     * The color select button
     */
    private Button getColorButton() {
        return (Button)findViewById(R.id.buttonColor);
    }

    /**
     * The feather checkbox
     */
    private CheckBox getFeatherCheck() {
        return (CheckBox)findViewById(R.id.checkFeather);
    }

    /**
     * The hat choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.spinnerHat);
    }
    /**
     * Ensure the user interface components match the current state
     */

    private void updateUI() {
        getColorButton().setEnabled(getHatterView().getHat() == 2);
        getFeatherCheck().setChecked(getHatterView().GetDrawthefeather());
        getSpinner().setSelection(getHatterView().getHat());
    }



}

