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

public class DepthFragment extends Fragment {


    public DepthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Profundidad sitios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_depth, container, false);

        // Set the Action Bar color corresponding to this fragment.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFE7191B));
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

        List<String[]> depthData = new ArrayList<>();
        List<Boolean> isHeaderPosition = new ArrayList<>();
        String[] columnNames = new String[5];

        // Read title.
        String[] row = new String[] {Helper.depthTXT.get(0)};
        depthData.add(row);
        isHeaderPosition.add(true);

        // Read column names.
        for (int i = 1; i <= 5; i++)
            columnNames[i-1] = Helper.depthTXT.get(i);

        // Read each row of the first chart.
        int lastLineFirstChart = 0;
        for (int i = 6; i < Helper.depthTXT.size(); i+=5) {
            // The replace method is a fix for a strange character that can appear instead of
            // a whitespace.
            if (Helper.depthTXT.get(i).replaceAll("\u00A0", "").trim().equals("")) {
                lastLineFirstChart = i;
                break;
            }

            row = new String[5];
            // Read the columns of this row.
            for (int j = 0; j < 5; j++) {
                row[j] = Helper.depthTXT.get(i+j);
            }

            depthData.add(row);
            isHeaderPosition.add(false);
        }

        depthData.add(new String[]{Helper.depthTXT.get(lastLineFirstChart + 1)});
        isHeaderPosition.add(true);

        int startOfSecondChart = lastLineFirstChart+3;

        depthData.add(new String[]{Helper.depthTXT.get(startOfSecondChart)});
        isHeaderPosition.add(true);
        for (int i = startOfSecondChart+1; i + 1 < Helper.depthTXT.size(); i+=2) {
            depthData.add(new String[]{Helper.depthTXT.get(i) + Helper.depthTXT.get(i + 1)});
            isHeaderPosition.add(true);
        }

        // Set the adapter
        Context context = getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.beacons_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new GeneralRecyclerViewAdapter(getActivity(), depthData,
                isHeaderPosition, columnNames, "Profundidad del sitio"));

        return view;
    }
}
