package jms.puertobahiablanca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {
    private String viewTitle;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(viewTitle);
    }

    public static DetailsFragment newInstance(String[] columnNames, String[] row, String viewTitle) {
        DetailsFragment myFragment = new DetailsFragment();

        myFragment.viewTitle = viewTitle;

        int length = columnNames.length;

        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (!row[i].equals("")) {
                // This is a special case that should be considered to correctly parse the data file.
                // If a cell that corresponds to a column header spans over two columns, the cells in
                // the second column will not have a column name associated with them. To fix this, both
                // columns are shown under the same column header.
                if (columnNames[i].equals("CARGA-DESC. TONS") || columnNames[i].equals("CARGA-DESC TONS")) {
                    data.add("<font color=#657FC4><b>" + columnNames[i] + "</b></font>" + ": " + row[i]
                            + row[i+1]);
                    i++;
                }
                else if (!columnNames[i].equals("") && !row[i].equals(""))
                    data.add("<font color=#657FC4><b>" + columnNames[i] + "</b></font>" + ": " + row[i]);
            }
        }

        Bundle args = new Bundle();
        args.putStringArrayList("data", data);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        // needed to indicate that the fragment would
        // like to add items to the Options Menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_details, container, false);

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
        });
        //todo
        setHasOptionsMenu(true);

        //ListView detailsLv = (ListView) layout.findViewById(R.id.details_lv);
        TextView tv1 = (TextView) layout.findViewById(R.id.textView1),
                tv2 = (TextView) layout.findViewById(R.id.textView2),
                tv3 = (TextView) layout.findViewById(R.id.textView3);
        List<String> data = getArguments().getStringArrayList("data");

        tv1.setText(Html.fromHtml(data.get(0)));
        tv2.setText(Html.fromHtml(data.get(1)));
        String dataInUnorderedList = "";
        for (int i = 2; i < data.size(); i++) {
            dataInUnorderedList += "&#8226; " + data.get(i) + "<br/>";
        }
        tv3.setText(Html.fromHtml(dataInUnorderedList));

        /*
        ArrayAdapter<String> adapter = new SimpleTextViewAdapter(getContext(), data.subList(2, data.size()));
        detailsLv.setAdapter(adapter);
        */

        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItem", "selected");
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
