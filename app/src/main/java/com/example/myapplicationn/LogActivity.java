package com.example.myapplicationn;

import static java.util.logging.Logger.global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class LogActivity extends AppCompatActivity {
    private ListView listView;
    List fileList = new ArrayList<>();
    String uurl;
    String filename;

    ArrayAdapter adapter;
    static boolean calledAlready = false;

    private ProgressDialog progressBar;

    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
    private File outputFile; //??????????????? ????????? ??????
    private File path;//??????????????????

    private boolean hasPermissions(String[] permissions) {
        int res = 0;
        //????????? ????????? ?????? ??????????????? ?????? ?????? ?????? ??????
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                //????????? ?????? ?????? ??????
                return false;
            }

        }
        //???????????? ????????? ??????
        return true;
    }


    private void requestNecessaryPermissions(String[] permissions) {
        //????????????( API 23 )???????????? ????????? ?????????(Runtime Permission) ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        if (!hasPermissions(PERMISSIONS)) { //????????? ????????? ???????????? ????????? ??????
            requestNecessaryPermissions(PERMISSIONS);//????????? ??????????????? ????????? ??????????????? ??????
        } else {
            //?????? ??????????????? ????????? ????????? ??????.
        }

        if (!calledAlready)
        {
            //FirebaseDatabase.getInstance().setPersistenceEnabled(true); // ?????? ?????????????????? ?????? ??????????????? ??????.
            calledAlready = true;
        }

        listView= (ListView)  findViewById(R.id.lv_fileList);

        adapter = new ArrayAdapter<String>(this, R.layout.activity_listitem, fileList);
        listView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("video");
        databaseRef.child("makevideo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // ????????? ????????? ???????
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    //MyFiles filename = (MyFiles) fileSnapshot.getValue(MyFiles.class);
                    //??????????????? value??? ????????? ??????????????????
                    String str = fileSnapshot.child("filename").getValue(String.class);
                    Log.i("TAG: value is ", str);
                    fileList.add(str);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });




        progressBar=new ProgressDialog(LogActivity.this);
        progressBar.setMessage("???????????????");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(true);




        //Button button = (Button) findViewById(R.id.button);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { //1
                //?????????????????? ?????? ????????? ???????????? Alight.avi ????????? ???????????????.

/*
                storageRef.child("video/makevideo/2021-11-01 13:27:04.avi").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

*/
                Object vo = (Object)adapterView.getAdapter().getItem(position);
/*
                Toast toast = Toast.makeText(getApplicationContext(), "Toast test",Toast.LENGTH_LONG);
                toast.setText("Toast" + vo);
                toast.show();
*/
                //final String fileURL = "https://firebasestorage.googleapis.com/v0/b/android-2305a.appspot.com/o/video%2Fmakevideo%2F2021-11-01%2013%3A27%3A04.avi?alt=media&token=1ecaef5b-8d25-49d0-a02f-ab6d23518edf";
                filename=vo.toString();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                StorageReference pathReference = storageRef.child("video/makevideo/"+filename);
                String changegg="video/makevideo/"+filename+"?alt=media";
                changegg=changegg.replace("/","%2F");
                changegg=changegg.replace(" ","%20");
                changegg=changegg.replace(":","%3A");

                String Ffilepath="https://firebasestorage.googleapis.com/v0/b/android-2305a.appspot.com/o/"+changegg;

                /*
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        uurl=uri.toString();
                    }
                });*/

                final String fileURL=Ffilepath;



                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputFile= new File(path, "a"); //??????????????? ????????? ????????? File ?????? ??????//filename

                if (outputFile.exists()) { //?????? ???????????? ?????? ?????? ??????

                    AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
                    builder.setTitle("?????? ????????????");
                    builder.setMessage("?????? SD ????????? ???????????????. ?????? ???????????? ?????????????");
                    builder.setNegativeButton("?????????",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Toast.makeText(getApplicationContext(),"?????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
                                    playVideo(outputFile.getPath());
                                }
                            });
                    builder.setPositiveButton("???",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    outputFile.delete(); //?????? ??????

                                    final DownloadFilesTask downloadTask = new DownloadFilesTask(LogActivity.this);//filename
                                    downloadTask.execute(fileURL);

                                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            downloadTask.cancel(true);
                                        }
                                    });
                                }
                            });
                    builder.show();

                } else { //?????? ???????????? ?????? ??????
                    final DownloadFilesTask downloadTask = new DownloadFilesTask(LogActivity.this);//filename
                    downloadTask.execute(fileURL);

                    progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            downloadTask.cancel(true);

                        }
                    });
                }
            }
        });
    }



    public class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String st;

        public DownloadFilesTask(Context context) {
            this.context = context;
            //this.st=st;

        }


        //?????? ??????????????? ???????????? ?????? ????????????????????? ????????? ???????????????.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //???????????? ???????????? ??? ?????? ????????? ??????????????? CPU??? ????????? ????????? ??????
            //?????? ???????????? ????????? ????????? ??????????????? ???????????? ?????? ?????????.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();
        }


        //?????? ??????????????? ???????????????.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;


            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //?????? ????????? ?????????
                FileSize = connection.getContentLength();

                //URL ??????????????? ???????????????????????? ?????? input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String ccc=path.toString()+filename;

                outputFile= new File(path, "a"); //??????????????? ????????? ????????? File ?????? ??????

                // SD????????? ???????????? ?????? Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //???????????? BACK ?????? ????????? ????????????
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float)downloadedSize/FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //????????? ???????????? ???????????????.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                mWakeLock.release();

            }
            return FileSize;
        }


        //???????????? ??? ?????????????????? ????????????
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //?????? ???????????? ?????? ???
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();

            if ( size > 0) {
                Toast.makeText(getApplicationContext(), "???????????? ?????????????????????. ?????? ??????=" + size.toString(), Toast.LENGTH_LONG).show();

                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);

                playVideo(outputFile.getPath());

            }
            else
                Toast.makeText(getApplicationContext(), "???????????? ??????", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!readAccepted || !writeAccepted) {
                            showDialogforPermission("?????? ??????????????? ???????????? ????????????????????????.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {

        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  LogActivity.this);
        myDialog.setTitle("??????");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    private void playVideo(String path) {
        Uri videoUri = Uri.fromFile(new File(path));
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(videoUri, "video/*");
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(videoIntent, null));
        }
    }


}