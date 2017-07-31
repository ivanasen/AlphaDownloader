package app.ivanasen.com.alphadownloader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.ivanasen.com.alphadownloader.R;


public class InternetSpeedTestFragment extends Fragment {

    public static InternetSpeedTestFragment newInstance() {
        return new InternetSpeedTestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internet_speed_test, container, false);
    }

}
