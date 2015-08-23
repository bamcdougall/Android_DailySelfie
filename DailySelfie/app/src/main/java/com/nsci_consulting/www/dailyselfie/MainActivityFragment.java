package com.nsci_consulting.www.dailyselfie;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
/*
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
*/

public class MainActivityFragment extends ListFragment {

    private PictureRecord selectedSelfie;
    private CustomAdapter mCustomAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setListAdapter(mCustomAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomAdapter = new CustomAdapter(getActivity(), null, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCustomAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy () {
        mCustomAdapter.freeResources();

        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //LAUNCH SELFIE DETAILS FRAGMENT
        selectedSelfie = (PictureRecord)mCustomAdapter.getItem(position);

        if(selectedSelfie != null) {
            BigPictureFragment detailsFragment = new BigPictureFragment(selectedSelfie);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.selfie_thumb_container, detailsFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    /**
     * Public Methods
     */

    public void addSelfie(PictureRecord newSelfie) {
        if(mCustomAdapter != null) {
            mCustomAdapter.addSelfie(newSelfie);
        }
    }

    public void deleteSelectedSelfie() {
        if(mCustomAdapter != null) {
            mCustomAdapter.deleteSelfie(selectedSelfie);
        }
    }

    public void deleteAllSelfies() {
        if(mCustomAdapter != null) {
            mCustomAdapter.deleteAllSelfies();
        }
    }
}
