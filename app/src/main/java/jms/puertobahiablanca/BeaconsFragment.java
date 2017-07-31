package jms.puertobahiablanca;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BeaconsFragment extends Fragment {


    public BeaconsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Balizamiento");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);

        // Set the Action Bar color corresponding to this fragment.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF2F4682));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Set the on refresh listener.
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)
                view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent splashScreenIntent = new Intent(getActivity(), SplashScreenActivity.class);
                startActivity(splashScreenIntent);
                getActivity().finish();
            }
        });

        // Get the data needed to fill the ships list.
        List<String[]> beaconsData = new ArrayList<>();
        List<Boolean> isHeaderPosition = new ArrayList<>();
        String[] columnNames = null;

        beaconsData.add(Helper.beaconsCSV.get(0));
        isHeaderPosition.add(true);

        columnNames = Helper.beaconsCSV.get(1);

        for (int i = 2; i < Helper.beaconsCSV.size(); i++) {
            String[] row = Helper.beaconsCSV.get(i);

            // If a blank space is found, this chart ended in the previous row.
            if (row[0].equals("")) {
                // We can now start reading the footnote.
                String note = "";
                for (i = i+1; i < Helper.beaconsCSV.size(); i++)
                    note += Helper.beaconsCSV.get(i)[0];

                row = new String[]{note};

                beaconsData.add(row);
                isHeaderPosition.add(true);

                break;
            }

            beaconsData.add(row);
            // If the first column of this row has that name, a header item must be created.
            isHeaderPosition.add(false);
        }

        // Set the adapter
        Context context = getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.beacons_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new GeneralRecyclerViewAdapter(getActivity(), beaconsData,
                isHeaderPosition, columnNames, "Baliza"));

        return view;
    }

}
