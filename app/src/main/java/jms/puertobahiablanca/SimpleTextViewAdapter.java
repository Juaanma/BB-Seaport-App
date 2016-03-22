package jms.puertobahiablanca;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juan on 11/02/16.
 */
public class SimpleTextViewAdapter extends ArrayAdapter<String> {
    private List<String> data;

    public SimpleTextViewAdapter(Context context, List<String> data) {
        super(context, 0, data);

        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_text_view_item, parent, false);
        // Lookup view for data population
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        // Populate the data into the template view using the data object
        textView.setText(Html.fromHtml(data.get(position)));
        // Return the completed view to render on screen
        return convertView;
    }
}
