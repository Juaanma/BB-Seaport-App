package jms.cgpbb;

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
 * A fragment representing a list of Items.
 */
public class ShipsPositionsFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShipsPositionsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Posición de buques");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ships_positions, container, false);

        // Set the Action Bar color corresponding to this fragment.
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF52BAD7));
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
        List<String[]> shipsData = new ArrayList<>();
        List<Boolean> isHeaderPosition = new ArrayList<>();
        String[] columnNames = null;

        for (int i = 0; i < Helper.positionCSV.size(); i++) {
            String[] row = Helper.positionCSV.get(i);
            if (row[0].equals("SITIO")) {
                columnNames = row;

                for (i = i+1; i < Helper.positionCSV.size(); i++) {
                    row = Helper.positionCSV.get(i);

                    // If a blank space is found, this chart ended in the previous row.
                    if (row[0].equals(""))
                        break;

                    shipsData.add(row);
                    // If the first column of this row has that name, a header item must be created.
                    isHeaderPosition.add(row[0].equals("PUERTO ROSALES (Monoboyas)"));
                }

                break;
            }
        }

        // Set the adapter
        Context context = getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ships_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new GeneralRecyclerViewAdapter(getActivity(), shipsData,
                isHeaderPosition, columnNames, "Posición de buque"));

        return view;
    }
}
