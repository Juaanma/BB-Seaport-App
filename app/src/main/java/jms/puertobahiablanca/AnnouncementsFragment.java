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

public class AnnouncementsFragment extends Fragment {


    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Anuncios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

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

        // Get the data needed to fill the list corresponding to this fragment.
        List<String[]> announcementsData = new ArrayList<>();
        List<Boolean> isHeaderPosition = new ArrayList<>();
        String[] columnNames = null;

        for (int i = 0; i < Helper.positionCSV.size(); i++) {
            String[] row = Helper.positionCSV.get(i);
            if (row[0].equals("ANUNCIOS")) {
                i++;

                columnNames = Helper.positionCSV.get(i);

                for (i = i+1; i < Helper.positionCSV.size(); i++) {
                    row = Helper.positionCSV.get(i);

                    // If a blank space is found, this chart ended in the previous row.
                    if (row[0].equals(""))
                        break;
                    // If this subsection is empty, the header from the previous row must be deleted.
                    if (row[0].equals("No hay.")) {
                        announcementsData.remove(announcementsData.size()-1);
                        isHeaderPosition.remove(isHeaderPosition.size()-1);
                        continue;
                    }

                    announcementsData.add(row);
                    // If the first column of this row has one of these names, a header item must
                    // be created.
                    isHeaderPosition.add(row[0].equals("GRANOS") || row[0].equals("SUBPRODUCTOS")
                            || row[0].equals("CARGA GENERAL") || row[0].equals("CONTENEDORES")
                            || row[0].equals("FERTILIZANTES") || row[0].equals("METANEROS")
                            || row[0].equals("INFLAMABLES y QUIMICOS") || row[0].equals("MONOBOYAS")
                            || row[0].equals("VARIOS"));

                }

                break;
            }
        }

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.announcements_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new GeneralRecyclerViewAdapter(getActivity(), announcementsData,
                isHeaderPosition, columnNames, "Anuncio"));

        return view;
    }

}
