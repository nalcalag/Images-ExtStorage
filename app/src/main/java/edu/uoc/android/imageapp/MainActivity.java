package edu.uoc.android.imageapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Request code
    private final int REQUEST_PERMISSION_STORAGE_SAVE = 101;
    private final int REQUEST_PERMISSION_STORAGE_DELETE = 102;
    private final int REQUEST_TAKE_PHOTO = 103;
    // Views
    private View rootView;
    private Button buttonOpenImage;
    private ImageView mImageView;
    private TextView tvMessage;
    //
    String mCurrentPhotoPath;
    String uocPhotoPath;
    ImageActivity imageActivity;

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

        // Check if there is an image saved in the phone storage and print it
        uocPhotoPath = imageActivity.getDirectory() + "/" + imageActivity.getImgName();
        File file = new File(imageActivity.getDirectory() + "/" + imageActivity.getImgName());

        if (file.exists()) {

            mImageView.setImageBitmap(BitmapFactory.decodeFile(uocPhotoPath));
            tvMessage.setVisibility(View.INVISIBLE);
        }
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

        if (mImageView != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Do you want to delete this image?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            imageActivity = new ImageActivity(rootView);
                            imageActivity.deleteImage();
                            mImageView.setImageDrawable(null);
                            tvMessage.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void onSaveMenuTap() {
        // check permissions
        if (!hasPermissionsToWrite()) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                              REQUEST_PERMISSION_STORAGE_SAVE);
        }
        // TODO save the image
        imageActivity = new ImageActivity(rootView);
        //Check if there is an image
        if (mImageView.getDrawable() != null) {
            imageActivity.saveImage(((BitmapDrawable)mImageView.getDrawable()).getBitmap());
        } else {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermissionsToWrite() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOpenImage) {

            takePicture();
        }
    }

    //Method to take a picture from Camera App and save in a temp File
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile("temp");
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile(String directoryName) throws IOException {
        // Create an image file name
        String imageFileName = "imageApp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + directoryName);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            setPic();

            //Set TextView when there is an image
            if (mImageView !=null) {
                tvMessage.setVisibility(View.INVISIBLE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
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
