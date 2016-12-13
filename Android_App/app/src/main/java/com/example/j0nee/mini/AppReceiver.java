package com.example.j0nee.mini;


import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.R.attr.name;


/**
 * Created by j0nee on 6/12/16.
 */

public class AppReceiver extends BroadcastReceiver {


    String packageName = null;
    String appName = null;
    String apkPath = null;
    String hashMd5 = null;
    String hashSha1 = null;
    String fileName;
    int alert = 0;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**********
     * File Path
     *************/

    int serverResponseCode = 0;


    private DatabaseReference mDatabase;







    @Override
    public void onReceive(Context context, Intent intent) {


        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        long prev = 0;

        for (ApplicationInfo packageInfo : packages) {
            String appFile = packageInfo.publicSourceDir;
            long last = new File(appFile).lastModified();

            if (last > prev) {
                //ResolveInfo info = (ResolveInfo) packageInfo;
                prev = last;
                packageName = packageInfo.packageName;
                appName = packageInfo.loadLabel(context.getPackageManager()).toString();
                apkPath = packageInfo.sourceDir;
                fileName = appFile;

            }


            new Thread(new Runnable() {
                public void run() {
                    uploadFile(fileName, appName);
                }
            }).start();


            File file = new File(apkPath);

            try {

                FileInputStream input = new FileInputStream(file);

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[8888];
                int l;


                while ((l = input.read(buffer)) > 0)
                    output.write(buffer, 0, l);

                input.close();
                output.close();

                byte[] data = output.toByteArray();

                MessageDigest digest = MessageDigest.getInstance("MD5");
                MessageDigest digestSha = MessageDigest.getInstance("SHA-1");

                byte[] bytes = data;
                byte[] bytesSha = data;

                digest.update(bytes, 0, bytes.length);
                bytes = digest.digest();


                digest.update(bytesSha, 0, bytes.length);
                bytesSha = digest.digest();


                StringBuilder sb = new StringBuilder();
                StringBuilder sbSha = new StringBuilder();

                for (byte b : bytes) {
                    sb.append(String.format("%02X", b));
                }

                hashMd5 = sb.toString();


                for (byte b : bytesSha) {
                    sbSha.append(String.format("%02X", b));
                }

                hashSha1 = sbSha.toString();


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        Toast.makeText(context, packageName + "\n" + appName + "\n" + apkPath + "\n" + hashMd5, Toast.LENGTH_LONG).show();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        int mal = 0;
        int padded = 1;
        AppData app = new AppData(packageName, appName);
        mDatabase.child("PackageName").setValue(packageName);
        mDatabase.child("MD5Hash").setValue(hashMd5);
        mDatabase.child("SHAHash").setValue(hashSha1);
        mDatabase.child("ApkPath").setValue(apkPath);
        mDatabase.child("Malicious").setValue(mal);
        mDatabase.child("padded").setValue(padded);



        mDatabase.child("Malicious").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                alert = dataSnapshot.getValue(int.class);



            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //null
            }
        });



    }
    public int uploadFile(String sourceFileUri, String name) {


        String fileName = name + ".apk";
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String SERVER_URL = "http://10.30.52.89/App/server.php";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(SERVER_URL);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                if (serverResponseCode == 200) {

                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return serverResponseCode;

        } // End else block

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



}


