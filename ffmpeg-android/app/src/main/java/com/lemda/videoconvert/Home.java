package com.lemda.videoconvert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.services.SampleOverlayService;

public class Home extends Activity implements View.OnClickListener {

    private static final String TAG = Home.class.getSimpleName();

    @Inject
    FFmpeg ffmpeg;

    @InjectView(R.id.command)
    EditText commandEditText;

    @InjectView(R.id.command_output)
    LinearLayout outputLayout;

    @InjectView(R.id.run_command)
    Button runButton;

    private ProgressDialog progressDialog;



    @InjectView(R.id.tvDetail)
    TextView tvDetail;
    @InjectView(R.id.btnSelectAudio)
    Button btnSelectAudio;
    @InjectView(R.id.btnSelectVideo)
    Button btnSelectVideo;
    @InjectView(R.id.btnDone)
    Button  btnDone;
    @InjectView(R.id.btnRemoveAudio)
    Button  btnRemoveAudio;

    @InjectView(R.id.btnView)
    Button  btnView;



    String videoFile = "";
    String audioFile = "";
    String outputFile = "";
    String strDetails = "";
    int REQUEST_TAKE_GALLERY_VIDEO = 101;
    int REQUEST_TAKE_GALLERY_AUDIO = 201;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        ObjectGraph.create(new DaggerDependencyModule(this)).inject(this);




        try
        {
           /* tvDetail = (TextView)findViewById(R.id.tvDetail);
            btnSelectAudio = (Button)findViewById(R.id.btnSelectAudio);
            btnSelectVideo = (Button)findViewById(R.id.btnSelectVideo);
            btnDone = (Button)findViewById(R.id.btnDone);
*/
            //SampleOverlayService sampleOverlayService = new SampleOverlayService();
            startService(new Intent(this, SampleOverlayService.class));

            //SampleOverlayService.stop();


            btnSelectVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("video/mp4");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

                }
            });


            btnSelectAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Audio"),REQUEST_TAKE_GALLERY_AUDIO);

                }
            });

            btnView = (Button) findViewById(R.id.btnView);
            btnView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Log.i("==","--view--");
                    Intent intent = new Intent(Home.this, ActTabMain.class);//CameraRecorder
                    startActivity(intent);
                    finish();
                }
            });

            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAddAudioDone();
                }
            });


            btnRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRemoveAudioDone();
                }
            });

        }
        catch (Exception e)
        {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        loadFFMpegBinary();
        initUI();
    }



    private void setAddAudioDone()
    {
        try {
            int lastpos = videoFile.length();

            String st = new String("abcde");
            outputFile = new StringBuffer(videoFile).insert(lastpos-4, "_222").toString();
            String
                    //right command to remove audio  1.
                    // cmd_add_audio="-i!@#"+videoFilePath+"!@#-c!@#copy!@#-an!@#"+outputFilePath;

                    cmd_add_audio="-i!@#"+videoFile+"!@#-i!@#"+audioFile+"!@#-codec!@#copy!@#-shortest!@#"+outputFile;

            // ffmpeg -i video.avi -i audio.mp3 -codec copy -shortest output.avi


            //ffmpeg -i example.mkv -c copy -an example-nosound.mkv


            commandEditText.setText(cmd_add_audio);

            strDetails = strDetails + "\n  AddAudioCommand path= "+cmd_add_audio;
            tvDetail.setText(strDetails);
            // ffmpeg.execute(cmd, mergeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cmd = commandEditText.getText().toString();
        String[] command = cmd.split("!@#");
        if (command.length != 0) {
            execFFmpegBinary(command);
        } else {
            Toast.makeText(Home.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void setRemoveAudioDone()
    {
        try {

            int lastpos = videoFile.length();

            String st = new String("abcde");
            outputFile = new StringBuffer(videoFile).insert(lastpos-4, "_222").toString();

            String
                    //right command to remove audio  1.



                     cmd_add_audio="-i!@#"+videoFile+"!@#-c!@#copy!@#-an!@#"+outputFile;

                    //cmd_add_audio="-i!@#"+videoFilePath+"!@#-i!@#"+audioFilePath+"!@#-codec!@#copy!@#-shortest!@#"+outputFilePath;

            // ffmpeg -i video.avi -i audio.mp3 -codec copy -shortest output.avi


            //ffmpeg -i example.mkv -c copy -an example-nosound.mkv


            commandEditText.setText(cmd_add_audio);

            strDetails = strDetails + "\n  RemoveCommand path= "+cmd_add_audio;
            tvDetail.setText(strDetails);
            // ffmpeg.execute(cmd, mergeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cmd = commandEditText.getText().toString();
        String[] command = cmd.split("!@#");
        if (command.length != 0) {
            execFFmpegBinary(command);
        } else {
            Toast.makeText(Home.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
        }
    }



    private void initUI() {
        runButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    addTextViewToLayout("FAILED with output : "+s);
                }

                @Override
                public void onSuccess(String s) {
                    addTextViewToLayout("SUCCESS with output : "+s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg "+command);
                    addTextViewToLayout("progress : "+s);
                    progressDialog.setMessage("Processing\n"+s);
                }

                @Override
                public void onStart() {
                    outputLayout.removeAllViews();

                    Log.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void addTextViewToLayout(String text) {
        TextView textView = new TextView(Home.this);
        textView.setText(text);
        outputLayout.addView(textView);
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(Home.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Home.this.finish();
                    }
                })
                .create()
                .show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_command:

                try {
                   // FFmpeg ffmpeg = FFmpeg.getInstance(this);
                    String videoFilePath ="/storage/emulated/0/Download/swim.mp4";
                    String audioFilePath ="/storage/emulated/0/Download/test.mp3"; ///storage/sdcard1/MyGeet/swim.mp3";
                    String outputFilePath ="/storage/emulated/0/Download/new3_swim.mp4";
                    String cmd_add_audio2 = "-i!@#" + videoFilePath + "!@#-i!@#" + audioFilePath + "!@#-shortest!@#-threads!@#0!@#-preset!@#ultrafast!@#-strict!@#-2!@#" + outputFilePath;

                    String
                            //right command to remove audio  1.
                     // cmd_add_audio="-i!@#"+videoFilePath+"!@#-c!@#copy!@#-an!@#"+outputFilePath;

                            cmd_add_audio="-i!@#"+videoFilePath+"!@#-i!@#"+audioFilePath+"!@#-codec!@#copy!@#-shortest!@#"+outputFilePath;

                   // ffmpeg -i video.avi -i audio.mp3 -codec copy -shortest output.avi


                    //ffmpeg -i example.mkv -c copy -an example-nosound.mkv


                    commandEditText.setText(cmd_add_audio);
                   // ffmpeg.execute(cmd, mergeListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String cmd = commandEditText.getText().toString();
                String[] command = cmd.split("!@#");
                if (command.length != 0) {
                    execFFmpegBinary(command);
                } else {
                    Toast.makeText(Home.this, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }















    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String   filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {

                    Log.i("=--Video path-=","--path---"+selectedImagePath);
                    videoFile = selectedImagePath;

                    strDetails = strDetails + "\n  Video path= "+videoFile;
                    tvDetail.setText(strDetails);

                    /*Intent intent = new Intent(HomeActivity.this,VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
                }
            }
            if (requestCode == REQUEST_TAKE_GALLERY_AUDIO) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String   filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {

                    Log.i("=--audio path-=","--path---"+selectedImagePath);
                    audioFile = selectedImagePath;

                    strDetails = strDetails + "\n  Audio path= "+audioFile;
                    tvDetail.setText(strDetails);

                    /*Intent intent = new Intent(HomeActivity.this,VideoplayAvtivity.class);
                    intent.putExtra("path", selectedImagePath);
                    startActivity(intent);*/
                }
            }
        }
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}




// for the trim audio/video
//ffmpeg -ss 10 -to 16 -i input.mp3 output.mp3