package com.example.max_8.mediawithdb.Activitys;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.max_8.mediawithdb.AdapterClasses.SongsAdapter;
import com.example.max_8.mediawithdb.Model.UploadSongs;
import com.example.max_8.mediawithdb.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ShowListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    java.util.List<UploadSongs> List;
    SongsAdapter adapter;
    DatabaseReference mDR;
    public static boolean Ischeack = false;

    ValueEventListener valueEventListener;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    String uploadID;

    //--- here dialog
    AppCompatEditText editText;
    AppCompatEditText editText2;
    TextView textView;
    Uri audioUri;
    Uri mImageUri;

    Button buttonUpSongFromGall;
    Button buttonUp;
    StorageReference mStorage,mStorage2;
    StorageTask mUploadTask;
    StorageTask mUploadTask2;
    private DatabaseReference referenceSong;

    private ProgressDialog mProgress;
    private View view;
    private ImageButton imageSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        progressBar = (ProgressBar)findViewById(R.id.progressBarAbdullah);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List = new ArrayList<>();

        adapter = new SongsAdapter(ShowListActivity.this,List);
        recyclerView.setAdapter(adapter);

        mDR = FirebaseDatabase.getInstance().getReference("songs");


        valueEventListener = mDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List.clear();
                for(DataSnapshot dss : dataSnapshot.getChildren())
                {
                    UploadSongs uploadSongs = dss.getValue(UploadSongs.class);
                    uploadSongs.setMkey(dss.getKey());
                    List.add(uploadSongs);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(),databaseError.getMessage()+"",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPopupDialog();

            }
        });
    }


    public void createPopupDialog(){

        dialogBuilder = new AlertDialog.Builder(this);

        
        view = getLayoutInflater().inflate(R.layout.popup,null);

        setUpDialog();

        dialogBuilder.setView(view);//connect with builder
        dialog = dialogBuilder.create();//create the with alert

        dialog.show();

        buttonUpSongFromGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFile();
            }
        });

        imageSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageFile();
            }
        });


        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Wait for Uploading  ..");
                mProgress.setCancelable(false);
                mProgress.show();

                uploadAudioToFirebase();


            }
        });
    }


    //open gallery
    private void openImageFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,12323);
    }//-end

    //open gallery
    private void openAudioFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,101);
    }//-end
    //receive audio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12323 && resultCode == RESULT_OK && data.getData() != null){

            mImageUri = data.getData();
            Log.d("sdsdk",mImageUri.toString());
            imageSong.setImageURI(mImageUri);
            imageSong.setBackgroundColor(Color.green(0));

        }

        if(requestCode == 101 && resultCode == RESULT_OK && data.getData() != null){

            audioUri = data.getData();

            String fileName = getFileName(audioUri);
            textView.setText(fileName);


        }



    }//--end


    //file name more readable
    private String getFileName(Uri audioUri) {

        String result = null;


        //content://media/external/audio/media/710
        if (audioUri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(audioUri,null,null,null,null);

            try{
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

//                Log.d("audio:",
//                        cursor.getColumnName(0)+" "+  //document_id
//                        cursor.getColumnName(1)+" "+      //mime_type
//                        cursor.getColumnName(2)+" "+     //_display_name
//                        cursor.getColumnName(3)+" "+    //summary
//                        cursor.getColumnName(4)+" "+   //last_modified
//                        cursor.getColumnName(5)+" "+  //flags
//                        cursor.getColumnName(6)+" ");//_size

            }//end try
            finally { cursor.close();}
        }//end if

        //if null
        if (result == null){
            result = audioUri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut!=-1){
                result = result.substring(cut + 1);
            }//end if
        }//end if

        return result;

    }//---end


    //upload
    private void uploadAudioToFirebase(){


        if(textView.getText().toString().equals("No file selected")  ) {
            Toast.makeText(getApplicationContext(), "Please select an audio .. ", Toast.LENGTH_LONG).show();
            mProgress.dismiss();

        }else {

            if (mUploadTask!=null && mUploadTask.isInProgress())
                Toast.makeText(getApplicationContext(),"Song upload .. ",Toast.LENGTH_SHORT).show();
            else
                uploadFile();
        }
    }//----END


    //here upload to storage in fire base
    private void uploadFile() {

        if(audioUri != null && mImageUri != null && !editText.getText().toString().equals("") && !editText2.getText().toString().equals("")){

            String durationTxt;


            int durationInMillis = findSongDuration(audioUri);


            final StorageReference storageReference = mStorage.child(System.currentTimeMillis() + "." +getFileExtension(audioUri));
            final StorageReference storageReference1 = mStorage2.child(System.currentTimeMillis()+ "."+"IMAGE");

            if(durationInMillis == 0)
            {
                durationTxt = "NA";
            }
            durationTxt = getDurationFromMilli(durationInMillis);
            final String finalDurationTxt = durationTxt;

            //image URI
            mUploadTask2 = storageReference1.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                           storageReference1.getDownloadUrl().
                                   addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(final Uri IMAGEuri) {

                            //audio URI
                            mUploadTask = storageReference.putFile(audioUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                                @Override
                                                public void onSuccess(final Uri audioURII) {

                                                        //UploadSongs uploadSongs = new UploadSongs(editText.getText().toString(),finalDurationTxt,audioUri.toString());
                                                        //change to this becuase file link to storage
                                                        UploadSongs uploadSongs = new UploadSongs(editText.getText().toString(), finalDurationTxt, audioURII.toString(), editText2.getText().toString(), IMAGEuri.toString());

                                                        //add songs to firebase
                                                        uploadID = referenceSong.push().getKey();//unike id

                                                        referenceSong.child(uploadID).setValue(uploadSongs);//بحيث انه يتغير كل شويه وووواو



                                                }
                                            }) ;
                                                }
                                            });

                                        }
                                    });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                            //Woooooooooooooooow
                            mProgress.setProgress((int)progress);

                            //if done
                            if(progress == 100.0){
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(),"file uploaded ...",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });


        }else{Toast.makeText(getApplicationContext(),"Some fields empty ...",Toast.LENGTH_SHORT).show();
        mProgress.dismiss();
        }


    }

    //change format
    private String getDurationFromMilli(int durationInMillis) {

        Date date = new Date(durationInMillis);
        SimpleDateFormat simple = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String myTime = simple.format(date);

        return myTime;
    }

    //duration songs
    private int findSongDuration(Uri audioUri) {

        int timeInMillisec = 0;
        try{
            //retrive metaData
            MediaMetadataRetriever re = new MediaMetadataRetriever();
            re.setDataSource(this,audioUri);//path
            String time = re.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//في انواع كثيره وووووواو

            re.release();
            timeInMillisec = Integer.parseInt(time);
            return timeInMillisec;
        }catch (Exception e) { //here if error
            e.printStackTrace();
            return 0;
        }

    }

    //type audio
    private String getFileExtension(Uri audioUri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }


    private void setUpDialog(){

        editText = (AppCompatEditText)view.findViewById(R.id.songTitle);
        editText2 = (AppCompatEditText)view.findViewById(R.id.artist_Name);
        textView = (TextView)view.findViewById(R.id.txtViewSongFileSelected);


        mProgress = new ProgressDialog(ShowListActivity.this);


        mStorage = FirebaseStorage.getInstance().getReference().child("DBSong");
        mStorage2 = FirebaseStorage.getInstance().getReference().child("DBSongImage");
        referenceSong = FirebaseDatabase.getInstance().getReference().child("songs");

        buttonUpSongFromGall = (Button)view.findViewById(R.id.uploadSongGallory);
        buttonUp = (Button)view.findViewById(R.id.uploadSong);
        imageSong = (ImageButton)view.findViewById(R.id.imageSong);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDR.removeEventListener(valueEventListener);
    }


    //Make sure if he comes back here
    @Override
    protected void onStart() {
        super.onStart();
        Ischeack = true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        Ischeack = false;
    }






}
