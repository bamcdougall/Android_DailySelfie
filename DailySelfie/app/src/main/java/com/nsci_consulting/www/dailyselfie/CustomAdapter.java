package com.nsci_consulting.www.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
//import android.widget.BaseAdapter;

/**
 * Created by Brendan on 8/16/2015.
 */
public class CustomAdapter  extends CursorAdapter {
    private Context mContext;
    private String mBitmapStoragePath;

    private static LayoutInflater mLayoutInflater = null;
    private ArrayList<PictureRecord> mSelfieRecords = new ArrayList<PictureRecord>();

    private PictureDatabase mSelfiesDatabase;

    public CustomAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

        mSelfiesDatabase = new PictureDatabase(mContext);
        mSelfiesDatabase.open();

        mBitmapStoragePath = PictureHelper.getBitmapStoragePath(mContext);

        reloadData();
    }

    /**
     * Public Methods
     */

    public void addSelfie(PictureRecord newSelfie) {

        String filePath = mBitmapStoragePath + "/" + newSelfie.getName();

        if(PictureHelper.storeBitmapToFile(newSelfie.getPictureBitmap(), filePath)) {
            newSelfie.setPicturePath(filePath);
        }

        mSelfiesDatabase.insertSelfie(newSelfie.getName(), newSelfie.getPicturePath());

        reloadData();
    }

    public void deleteSelfie(PictureRecord selfie) {
        mSelfiesDatabase.deleteSelfie(selfie.getID());

        reloadData();
    }

    public void deleteAllSelfies() {
        //DELETE PICTURES FROM EXTERNAL CARD
        File bitmapStorageDir = new File(mBitmapStoragePath);
        deleteAllFilesRecursive(bitmapStorageDir);

        //DELETE ITEMS FROM DATABASE
        mSelfiesDatabase.deleteAllSelfies();

        reloadData();
    }

    public void freeResources() {
        mSelfiesDatabase.close();
    }

    /**
     * Private Helper Methods
     */

    public void reloadData() {
        this.swapCursor(mSelfiesDatabase.getAllSelfies());
    }

    // Returns a new SelfieRecord for the data at the cursor's current position
    private PictureRecord getSelfieRecordFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(PictureDatabase.KEY_ROWID));
        String name = cursor.getString(cursor.getColumnIndex(PictureDatabase.KEY_NAME));
        String picturePath = cursor.getString(cursor.getColumnIndex(PictureDatabase.KEY_PICTURE_PATH));

        PictureRecord selfie = new PictureRecord(name, picturePath);
        selfie.setID(id);

        return selfie;
    }


    private void deleteAllFilesRecursive(File fileOrDirectory) {
        if(fileOrDirectory.isDirectory()) {
            for(File child : fileOrDirectory.listFiles()) {
                deleteAllFilesRecursive(child);
            }
        }
        else {
            fileOrDirectory.delete();
        }
    }

    /**
     * View Holder Class Methods
     */

    static class ViewHolder {
        TextView name;
        ImageView picture;
    }

    /**
     * Overridden Methods
     */

    @Override
    public Cursor swapCursor(Cursor newCursor) {

        mSelfieRecords.clear();

        if(newCursor != null && newCursor.moveToFirst()) {
            do {
                mSelfieRecords.add(getSelfieRecordFromCursor(newCursor));
            } while (newCursor.moveToNext());
        }

        return super.swapCursor(newCursor);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(cursor.getColumnIndex(PictureDatabase.KEY_NAME)));

        int dimenPix = (int)mContext.getResources().getDimension(R.dimen.selfie_row_picture_width_height);

        holder.picture.setImageBitmap(PictureHelper.getScaledBitmap(cursor.getString(cursor.getColumnIndex(PictureDatabase.KEY_PICTURE_PATH)), dimenPix, dimenPix));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        View newView = mLayoutInflater.inflate(R.layout.selfie_row, parent, false);
        holder.name = (TextView)newView.findViewById(R.id.selfie_name_text_view);
        holder.picture = (ImageView)newView.findViewById(R.id.selfie_picture_image_view);

        newView.setTag(holder);

        return newView;
    }

    @Override
    public int getCount() {
        return mSelfieRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelfieRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
