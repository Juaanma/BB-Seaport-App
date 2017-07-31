package jms.puertobahiablanca;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Avoid download the files two times if there's a saved instance of the Activity.
        if (savedInstanceState != null)
            return;

        DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(Helper.server2URL + Helper.positionFileName, Helper.server1URL +
                        Helper.beaconsFileName, Helper.server1URL + Helper.depthFileName,
                        Helper.newsServerURL + Helper.newsFileName);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... URLs) {
            // Check if the phone has an Internet connection.
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnected();
            if (!isConnected)
                return "Debes estar conectado a Internet para utilizar esta aplicación.";

            // Download the files.
            for (String url : URLs) {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL realURL = new URL(url);
                    String fileName = url.substring(url.lastIndexOf('/') + 1);
                    connection = (HttpURLConnection) realURL.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        // return connection.getResponseCode() + " " + connection.getResponseMessage();
                        return "Hubo un problema con el servidor. Por favor intenta más tarde.";
                    }

                    // download the file
                    input = connection.getInputStream();
                    // Open a new file output with the name of the file that was downloaded.
                    // We get the name from the URL.
                    output = openFileOutput(fileName, Context.MODE_PRIVATE);

                    byte data[] = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }

                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return "Ocurrió un problema durante la descarga. Asegúrate " +
                            " de estar conectado a Internet e intenta nuevamente.";
                    // return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            mWakeLock.release();
            // /*
            if (errorMessage != null) {
                new AlertDialog.Builder(context)
                        .setTitle("Esta aplicación necesita descargar archivos para funcionar.")
                        .setMessage(errorMessage)
                        .setPositiveButton("Volver a intentar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DownloadTask downloadTask = new DownloadTask(context);
                                downloadTask.execute(Helper.positionFileName, Helper.beaconsFileName,
                                        Helper.depthFileName);
                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
            else {
                // Load the data from the CSV into a List from the Helper class.
                Helper.getCSVFiles(context);

                // Start the main activity
                Intent mainActivityIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);

                finish();
            }
            // */
        }
    }
}
