package edu.uoc.android.imageapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Request code
    private final int REQUEST_PERMISSION_STORAGE_SAVE = 101;
    private final int REQUEST_PERMISSION_STORAGE_DELETE = 102;
    private final int REQUEST_IMAGE_CAPTURE = 103;
    // Views
    private View rootView;
    private Button buttonOpenImage;
    private ImageView mImageView;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set views
        rootView = findViewById(R.id.root);
        buttonOpenImage = (Button) findViewById(R.id.image_app_btn_capture);
        mImageView = (ImageView) findViewById(R.id.image_app_iv_picture);
        tvMessage = (TextView) findViewById(R.id.image_app_tv_message);

        // Set listeners
        buttonOpenImage.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            onDeleteMenuTap();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            onSaveMenuTap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteMenuTap() {
        // check permissions
        if (!hasPermissionsToWrite()) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                              REQUEST_PERMISSION_STORAGE_DELETE);
        }
        // TODO: show dialog if image file exists
    }

    private void onSaveMenuTap() {
        // check permissions
        if (!hasPermissionsToWrite()) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                              REQUEST_PERMISSION_STORAGE_SAVE);
        }
        // TODO save the image
    }

    private boolean hasPermissionsToWrite() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOpenImage) {
            // TODO: launching an intent to get an image from camera app

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            if (mImageView !=null) {
                tvMessage.setVisibility(View.INVISIBLE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE_DELETE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // show dialog if image file exists
                    // TODO: show dialog if image file exists
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO: show message
                }
            }
            case REQUEST_PERMISSION_STORAGE_SAVE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // save the image file
                    // TODO: save the image
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO: show message
                }
            }
        }
    }
}
