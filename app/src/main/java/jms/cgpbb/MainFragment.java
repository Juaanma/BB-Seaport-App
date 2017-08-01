package jms.cgpbb;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

public class MainFragment extends Fragment {

    private Context context;
    private OnChangeFragmentButtonPressed onChangeFragmentButtonPressed;

    public MainFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        onChangeFragmentButtonPressed = (OnChangeFragmentButtonPressed) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle("Puerto de Bahía Blanca");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_main, container, false);

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
                Intent splashScreenIntent = new Intent(context, SplashScreenActivity.class);
                startActivity(splashScreenIntent);
                getActivity().finish();
            }
        });

        // Configure the main navigation buttons (that are actually Card Views).
        RelativeLayout shipsPosRl = (RelativeLayout) layout.findViewById(R.id.ships_pos_rl),
                beaconsRl = (RelativeLayout) layout.findViewById(R.id.beacons_rl),
                depthRl = (RelativeLayout) layout.findViewById(R.id.depth_rl),
                vtsRl = (RelativeLayout) layout.findViewById(R.id.vts_rl);
        shipsPosRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFragmentButtonPressed.OnChangeFragmentButtonPressed("shipspos");
            }
        });
        beaconsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFragmentButtonPressed.OnChangeFragmentButtonPressed("beacons");
            }
        });
        depthRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFragmentButtonPressed.OnChangeFragmentButtonPressed("depth");
            }
        });
        vtsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFragmentButtonPressed.OnChangeFragmentButtonPressed("vts");
            }
        });

        TextView high_tide_hour = (TextView) layout.findViewById(R.id.high_tide_hour),
                low_tide_hour = (TextView) layout.findViewById(R.id.low_tide_hour);

        // To get a cell use: csvBody.get(row)[column]. Row and column are 0-indexed.
        // Set the value for the high tide hour.
        String high_tide_start_hour = Helper.positionCSV.get(2)[3],
                high_tide_end_hour = Helper.positionCSV.get(3)[3];
        high_tide_hour.setText(high_tide_start_hour + " / " + high_tide_end_hour);
        // Set the value for the low tide hour.
        String low_tide_start_hour = Helper.positionCSV.get(2)[1],
                low_tide_end_hour = Helper.positionCSV.get(3)[1];
        low_tide_hour.setText(low_tide_start_hour + " / " + low_tide_end_hour);

        // Set the values of the news' card view.
        TextView[] news_title = {
                (TextView) layout.findViewById(R.id.news_title1),
                (TextView) layout.findViewById(R.id.news_title2),
                (TextView) layout.findViewById(R.id.news_title3)
        };
        ImageView[] news_image = {
                (ImageView) layout.findViewById(R.id.news_image1),
                (ImageView) layout.findViewById(R.id.news_image2),
                (ImageView) layout.findViewById(R.id.news_image3)
        };
        RelativeLayout[] newsRelLayout = {
                (RelativeLayout) layout.findViewById(R.id.news_rl1),
                (RelativeLayout) layout.findViewById(R.id.news_rl2),
                (RelativeLayout) layout.findViewById(R.id.news_rl3)
        };

        try {
            JSONObject jObject = new JSONObject(Helper.newsJSON);

            for (int i = 0; i < 3; i++) {
                JSONObject news = ((JSONObject) jObject.getJSONArray("news").get(i));
                final String title1 = news.getString("titulo"), url = news.getString("url");
                news_title[i].setText(title1);
                newsRelLayout[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("URL: ", url);
                        try {
                            URL tempUrl = new URL(URLDecoder.decode(url, "UTF-8"));
                            Log.d("real url ", tempUrl.toString());
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tempUrl.toString()));
                            startActivity(browserIntent);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                });

                new ImageLoadTask(news.getString("imagen"), news_image[i]).execute();
            }
        }
        catch (JSONException err) {
            err.printStackTrace();
        }

        // Inflate the layout for this fragment
        return layout;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                // A trick to decode an encoded URL.
                URL tempUrl = new URL(URLDecoder.decode(url, "UTF-8"));
                URI uri = new URI(tempUrl.getProtocol(), tempUrl.getUserInfo(), tempUrl.getHost(),
                        tempUrl.getPort(), tempUrl.getPath(), tempUrl.getQuery(), tempUrl.getRef());
                URL urlConnection = uri.toURL();

                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
