package com.nsci_consulting.www.dailyselfie;

//import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "ExternalWriteActivity";
    String mCurrentPhotoPath;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private ListView lv;
    private CustomAdapter mAdapter;
    public ArrayList<PictureItem> mPictureItems = new ArrayList<PictureItem>();
    private AlarmManager mAlarmManager;
    private PendingIntent alarmIntent;
    private File pictureDirectory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listselfies);
        // load pictures in ArrayList
        pictureDirectory = getApplicationContext().getExternalFilesDir(null);

        if(pictureDirectory == null){
            Toast toast = Toast.makeText(this,getText(R.string.message_no_external_storage), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        mPictureItems = getPictureItemsFromPath(pictureDirectory);

        mAdapter = new CustomAdapter(mPictureItems,getApplicationContext());

        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PictureItem item = (PictureItem) adapterView.getItemAtPosition(i);
                Uri mUri = Uri.parse(pictureDirectory + "/" + item.getName());

                Intent mIntent = new Intent(getApplicationContext(), BigPictureActivity.class);
                mIntent.putExtra("uri", mUri);
                startActivity(mIntent);

            }
        });

        // set Alarm
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // build pending intent to start AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        startAlarm();

    }

    private void startAlarm(){

        // recurring every 2 Minutes
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (2*60*1000), 2*60*1000, alarmIntent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_stop_reminder) {

            // If the alarm has been set, cancel it.
            if (mAlarmManager!= null) {
                mAlarmManager.cancel(alarmIntent);

                Toast.makeText(getApplicationContext(),getText(R.string.message_reminder_stopped),Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if (id == R.id.action_start_reminder) {

            startAlarm();
            Toast.makeText(getApplicationContext(),getText(R.string.message_reminder_started),Toast.LENGTH_SHORT).show();

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.cam_selfie) {
            dispatchTakePictureIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "PictureStorageProblem");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
/*            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);*/
            refreshPictures();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private Bitmap setPic(File mCurrentPhotoPath) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / 200, photoH / 200);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath.getPath(), bmOptions);
    }

    private void refreshPictures() {

        mPictureItems = getPictureItemsFromPath(pictureDirectory);
        mAdapter.refresh(mPictureItems);
        mAdapter.notifyDataSetChanged();

    }

    private ArrayList<PictureItem> getPictureItemsFromPath(File path){

        ArrayList<PictureItem> items = new ArrayList<PictureItem>();
        File files[] = new File(path.getPath()).listFiles();

        for (File f: files){

            if(f.isFile()) {

                PictureItem pictureItem = new PictureItem();
                pictureItem.setName(f.getName());
                //pictureItem.setImage(BitmapFactory.decodeFile(f.getPath()));
                pictureItem.setImage(setPic(f));
                items.add(pictureItem);
            }

        }

        return items;
    }

}
