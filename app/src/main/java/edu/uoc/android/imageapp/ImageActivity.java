package edu.uoc.android.imageapp;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by nalcalag on 29/04/2017.
 */

public class ImageActivity {

    private View view;
    private static String directory = Environment.getExternalStorageDirectory()+"/UOCImageApp";
    private static String imgName = "imageApp.jpg";


    public ImageActivity(View view) {
        this.view = view;
    }

    public void saveImage(Bitmap bitmap) {

        File uocFile = new File(directory);
        if (!uocFile.exists()) {
            uocFile.mkdir();
        }

        // Check if file exists and delete it
        File file = new File(directory, imgName);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);

            // Save Bitmap in /UOCImageApp Directory
            if (fos!= null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.root),
                        R.string.save, Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        }
        catch (IOException ex) {
            Log.e("IOException", ex.toString());
            Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.root),
                    R.string.error_save, Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    public void deleteImage() {

        File file = new File(directory + "/" + imgName);

        if (file.exists()) {
            file.delete();
        }
        Snackbar mySnackbar = Snackbar.make(view.findViewById(R.id.root),
                R.string.delete, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public static String getDirectory() {
        return directory;
    }

    public static String getImgName() {
        return imgName;
    }
}
