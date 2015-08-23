package com.nsci_consulting.www.dailyselfie;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
//import android.support.v4.app.Fragment;

/**
 * Created by Brendan on 8/22/2015.
 */
public class BigPictureFragment extends Fragment {
    private PictureRecord mSelfie;

    public BigPictureFragment(PictureRecord selfie) {
        mSelfie = selfie;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Display options menu
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_bigpicture, container, false);

        ImageView imageView = (ImageView)view.findViewById(R.id.selfie_details_picture_image_view);
        imageView.setImageBitmap(PictureHelper.getScaledBitmap(mSelfie.getPicturePath(), 0, 0));

        // Set title bar
//        getActivity().getActionBar().setTitle(mSelfie.getName());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Remove items of the SelfiesFragment
        menu.clear();

        // Inflate selfie details mnu
        inflater.inflate(R.menu.menu_bigpicture, menu);
    }

}
