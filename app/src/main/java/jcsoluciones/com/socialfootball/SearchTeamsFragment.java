package jcsoluciones.com.socialfootball;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jcsoluciones.com.socialfootball.R;

/**
 * Created by Admin on 31/07/2016.
 */
public class SearchTeamsFragment extends Fragment {
    public SearchTeamsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_searchteams, container, false);
    }
}
