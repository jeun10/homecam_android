package com.example.myapplicationn;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SettingsActivity extends Activity {

    String plzurl;
    EditText user_name;
    Button btn_save;
    RadioGroup rd_select;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyMMdd");

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터
    private DatabaseReference mDatabase;
    private DatabaseReference firebaseDatabase;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Button btn_getImage = findViewById(R.id.btn_getImage);
        btn_getImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        //new part^----^


        user_name = findViewById(R.id.user_name);
        btn_save = findViewById(R.id.btn_save);
        rd_select=(RadioGroup) findViewById(R.id.rdgroup);


        //firebase 정의

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        btn_save.setOnClickListener(new View.OnClickListener() {//저장누를때
            @Override

            public void onClick(View v) {
                RadioButton rd = (RadioButton) findViewById(rd_select.getCheckedRadioButtonId());

                String getRadioSelect = rd.getText().toString();
                String getUserName = user_name.getText().toString();

                //String RadioSelect = "1";
/*
                if (getRadioSelect=="등록")
                    RadioSelect="1";
                else if (getRadioSelect=="미등록")
                    RadioSelect ="0";
*/

                //hashmap 만들기
                HashMap result = new HashMap<>();
                result.put("name", getUserName);
                result.put("select", getRadioSelect);

                writeNewUser(getUserName, getRadioSelect);

                //--------------------------
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef;
                int a=1;
                for(Uri uri : uriList){
                    String filename;//파일이름
                    String urlname;//


                    Date now = new Date();
                    filename=formatter.format(now)+"_"+a+".png";
                    urlname=formatter.format(now)+"_"+a;
                    storageRef=storage.getReferenceFromUrl("gs://android-2305a.appspot.com").child("/photo/"+getUserName+"/"+filename);
                    //storageRef.putFile(uri);
                    StorageReference pathReference = storageRef.child("/photo/"+getUserName+"/"+filename);
                    storageRef.putFile(uri)
                            //성공시
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("★★★★★★★★11111", uri.toString());
                                            String imgurl=uri.toString();
                                            mDatabase.child("users/"+getUserName+"/").child("photolink/"+urlname).setValue(imgurl);
                                        }
                                    }).toString();
                                }
                            })
                            //실패시
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                }
                            });


                    //다운로드할 파일을 가르키는 참조 만들기
/*
                    //Url을 다운받기
                    storageRef.putFile(uri);


                    //Url을 다운받기
                    String changegg="photo/"+filename+"?alt=media";
                    changegg=changegg.replace("/","%2F");
                    changegg=changegg.replace(" ","%20");
                    changegg=changegg.replace(":","%3A");

                    String Ffilepath="https://firebasestorage.googleapis.com/v0/b/android-2305a.appspot.com/o/"+changegg;

                    String imgurl=uri.toString();
                    mDatabase.child("users").child(getUserName+"/phlink/"+urlname+"/").setValue(Ffilepath);

*/
                    a++;

                }
                //---------------------------

            }
        });
    }
    //new--^^----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        int pic_count = 0;
        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

                adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        pic_count+=1;
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                }
            }
        }


    }
    //^^____^^


    //------------
    //-------------

    private void writeNewUser(String name, String select) {
        User user = new User(name, select);

        mDatabase.child("users").child(name).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(SettingsActivity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(SettingsActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void readUser(){
        mDatabase.child("users").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue(User.class) != null){
                    User post = dataSnapshot.getValue(User.class);
                    Log.w("FireBaseData", "getData" + post.toString());
                } else {
                    Toast.makeText(SettingsActivity.this, "데이터 없음...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}