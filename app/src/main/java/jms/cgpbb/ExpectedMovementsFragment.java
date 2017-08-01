package jms.cgpbb;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ExpectedMovementsFragment extends Fragment {


    public ExpectedMovementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Movimientos previstos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_expected_movements, container, false);

        // Set the Action Bar color corresponding to this fragment.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2F4682));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Set the on refresh listener.
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)
                layout.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent splashScreenIntent = new Intent(getActivity(), SplashScreenActivity.class);
                startActivity(splashScreenIntent);
                getActivity().finish();
            }
        });;

        ListView detailsLv = (ListView) layout.findViewById(R.id.expected_movements_lv);

        ArrayList<String> data = new ArrayList<>();

        for (int i = 0; i < Helper.positionCSV.size(); i++) {
            String[] row = Helper.positionCSV.get(i);
            if (row[0].equals("MOVIMIENTOS PREVISTOS")) {
                i++;
                while (i < Helper.positionCSV.size()) {
                    row = Helper.positionCSV.get(i);

                    // If we found a blank space, the expected movements section ended.
                    if (row[0].equals(""))
                        break;

                    data.add(row[0]);

                    i++;
                }
                break;
            }
        }

        ArrayAdapter<String> adapter = new SimpleTextViewAdapter(getContext(), data);
        detailsLv.setAdapter(adapter);

        // Inflate the layout for this fragment
        return layout;
    }

}
