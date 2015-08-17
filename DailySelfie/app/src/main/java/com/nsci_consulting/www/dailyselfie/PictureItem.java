package com.nsci_consulting.www.dailyselfie;

import android.graphics.Bitmap;

/**
 * Created by Brendan on 8/16/2015.
 */
public class PictureItem {
    private String name;
    private Bitmap image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
