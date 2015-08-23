package com.nsci_consulting.www.dailyselfie;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.app.AlertDialog;
import android.content.DialogInterface;
/*
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
*/

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

    private MainActivityFragment mMainActivityFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mMainActivityFragment = new MainActivityFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.selfie_thumb_container, mMainActivityFragment).commit();
        }

//        lv = (ListView) findViewById(R.id.listselfies);
        // load pictures in ArrayList
        pictureDirectory = getApplicationContext().getExternalFilesDir(null);


        // set Alarm
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // build pending intent to start AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        startAlarm();

    }

    private void startAlarm(){

        // recurring every 2 Minutes
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (2 * 60 * 1000), 2 * 60 * 1000, alarmIntent);
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

        if (id == R.id.action_delete_all_selfies) {
            deleteAllSelfies();
        }

        if (id == R.id.action_delete_selfie) {
            deleteSelfie();
        }

        if (id == R.id.action_ok) {
            getFragmentManager().popBackStack();
            return true;
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
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(PictureHelper.getTempFile(this)));

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
/*
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
*/
//            refreshPictures();
//            galleryAddPic();

            String selfieName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            PictureRecord newSelfie = new PictureRecord(selfieName, null);
            newSelfie.setPictureBitmap(PictureHelper.getTempBitmap(this));
            mMainActivityFragment.addSelfie(newSelfie);

//            mMainActivityFragment.addSelfie(photoFile);


        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Log.v(TAG, "PictureWrite2Gallery");

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

/*
    private void refreshPictures() {

        mPictureItems = getPictureItemsFromPath(pictureDirectory);
        mAdapter.refresh(mPictureItems);
        mAdapter.notifyDataSetChanged();

    }
*/

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

    private void deleteAllSelfies() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_delete_all_selfies));
        builder.setPositiveButton(getString(R.string.dialog_yes), dialogDeleteAllSelfiesClickListener);
        builder.setNegativeButton(getString(R.string.dialog_no), dialogDeleteAllSelfiesClickListener).show();
    }

    private void deleteSelfie() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_delete_selfie));
        builder.setPositiveButton(getString(R.string.dialog_yes), dialogDeleteSelfieClickListener);
        builder.setNegativeButton(getString(R.string.dialog_no), dialogDeleteSelfieClickListener).show();
    }
    DialogInterface.OnClickListener dialogDeleteAllSelfiesClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    Toast.makeText(getApplicationContext(),getText(R.string.message_delete_all),Toast.LENGTH_SHORT).show();
                    mMainActivityFragment.deleteAllSelfies();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dialogDeleteSelfieClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    Toast.makeText(getApplicationContext(),getText(R.string.message_delete_current),Toast.LENGTH_SHORT).show();
                    mMainActivityFragment.deleteSelectedSelfie();

                    getFragmentManager().popBackStack();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

}
