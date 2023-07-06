package com.example.webtoondl;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{READ_MEDIA_IMAGES, WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);

        downloadImage("https://thomas-fairhurst.com/assets/images/pyton-logo.png");

        ImageView imageView = findViewById(R.id.imageView);

        // Retrieve the file path of the downloaded image
        String imagePath = getApplicationContext().getFilesDir() + "/image.jpg";

        // Load the image into the ImageView
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);
    }

    private void downloadImage(String imageUrl) {
        // Create a new thread to perform network operations
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a URL object from the image URL
                    URL url = new URL(imageUrl);

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    // Get the input stream from the connection
                    InputStream input = connection.getInputStream();

                    // Create an output stream to save the image internally
                    OutputStream output = new FileOutputStream(
                            new File(getApplicationContext().getFilesDir(), "image.jpg"));

                    // Read the data from the input stream and write it to the output stream
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    output.close();
                    input.close();

                    // Optionally, you can notify the UI thread that the image has been downloaded
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Image downloaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}