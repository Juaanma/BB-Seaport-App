package jms.cgpbb;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GeneralRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final List<String[]> values;
    private final List<Boolean> isHeaderPosition;
    private final String[] columnNames;
    private final Context context;
    private final String detailedViewTitle;

    public GeneralRecyclerViewAdapter(Context context, List<String[]> values,
                                      List<Boolean> isHeaderPosition, String[] columnNames,
                                      String detailedViewTitle) {
        this.values = values;
        this.isHeaderPosition = isHeaderPosition;
        this.columnNames = columnNames;
        this.context = context;
        this.detailedViewTitle = detailedViewTitle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.general_list_item, parent, false);

            return new VHItem(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.general_list_header, parent, false);

            return new VHHeader(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType
                + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final String[] row = values.get(position);

        if (holder instanceof VHItem) {
            //cast holder to VHItem and set data
            VHItem item = ((VHItem) holder);

            //item.mItemIv.setImageResource(resourceId);

            item.mItemTv1.setText(Html.fromHtml("<font color=#657FC4><b>" + columnNames[0]
                    + "</b></font>" + ": " + row[0]));

            // The second row corresponds to the ship name's row.
            // If it's empty, then there's no boat in this row.
            if (row[1].equals("")) {
                item.mItemTv2.setText(Html.fromHtml("<font color=#657FC4><b>" + columnNames[1]
                                + "</b></font>" + ": "));

                item.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "No hay ningún buque en este sitio.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                item.mItemIv.setVisibility(View.INVISIBLE);
            }
            else {
                item.mItemTv2.setText(Html.fromHtml("<font color=#657FC4><b>" + columnNames[1]
                        + "</b></font>" + ": " + row[1]));

                item.mView.setOnClickListener(null);

                item.mItemIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a new fragment transaction.
                        FragmentTransaction transaction = ((MainActivity) context)
                                .getSupportFragmentManager().beginTransaction();
                        DetailsFragment detailsFragment = DetailsFragment.newInstance(columnNames, row, detailedViewTitle);
                        // Replace whatever is in the fragment_container view with this fragment.
                        transaction.replace(R.id.fragment_container, detailsFragment);
                        // Add the transaction to the back stack so the user can navigate back
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                });

                item.mItemIv.setVisibility(View.VISIBLE);
            }


        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header
            ((VHHeader) holder).mHeaderTv.setText(row[0]);
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition.get(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public class VHItem extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemTv1;
        public final TextView mItemTv2;
        public final ImageView mItemIv;

        public VHItem(View view) {
            super(view);
            mView = view;
            mItemTv1 = (TextView) view.findViewById(R.id.text_view_1);
            mItemTv2 = (TextView) view.findViewById(R.id.text_view_2);
            mItemIv = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public class VHHeader extends RecyclerView.ViewHolder {
        public final TextView mHeaderTv;

        public VHHeader(View view) {
            super(view);
            mHeaderTv = (TextView) view.findViewById(R.id.header_tv);
        }
    }
}
