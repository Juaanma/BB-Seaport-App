package jms.puertobahiablanca;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by juan on 07/02/16.
 */

public class Helper {
    public static final String serverURL = "http://ingraphica.com/clientes/puertomovil/jsreport/",
            beaconsFileName = "balizamiento.csv", positionFileName = "posicion.csv",
            depthFileName = "profundidad.txt", newsServerURL = "http://puertobahiablanca.com/",
            newsFileName = "jsonnews.php";

    private static final char SEPARATOR = ';';
    public static List<String[]> positionCSV, beaconsCSV;
    public static List<String> depthTXT = new ArrayList<>();
    public static String newsJSON;

    // Load the CSV data into the positionCSV list.
    public static void getCSVFiles(Context context) {
        try {
            File positionFile = new File(context.getFilesDir() + "/" + positionFileName),
                    beaconsFile = new File(context.getFilesDir() + "/" + beaconsFileName),
                    depthFile = new File(context.getFilesDir() + "/" + depthFileName),
                    newsFile = new File(context.getFilesDir() + "/" + newsFileName);

            // Read the position CSV file.
            CSVReader csvReader = new CSVReader(new FileReader(positionFile), SEPARATOR);
            positionCSV = csvReader.readAll();
            csvReader.close();

            // Read the beacons CSV file.
            csvReader = new CSVReader(new FileReader(beaconsFile), SEPARATOR);
            beaconsCSV = csvReader.readAll();
            csvReader.close();

            // Read the depths CSV file.
            FileInputStream is;
            is = new FileInputStream(depthFile);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
            String line = bufReader.readLine();
            while (line != null) {
                depthTXT.add(line);
                line = bufReader.readLine();
            }
            bufReader.close();

            // Read the news file.
            is = new FileInputStream(newsFile);
            bufReader = new BufferedReader(new InputStreamReader(is));
            line = bufReader.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = bufReader.readLine();
            }
            newsJSON = sb.toString();
            bufReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fragmentTransaction(FragmentManager fragmentManager, Fragment nextFragment) {
        // The BackStack must be cleared, so that the user doesn't return to a Fragment from a
        // different section (menu item) when he presses the back button.
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Create a new fragment transaction.
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment.
        transaction.replace(R.id.fragment_container, nextFragment);
        // Add the transaction to the back stack so the user can navigate back
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    public static void updateCurrentSelectedItem(NavigationView navigationView,
                                                 MenuItem selectedMenuItem) {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.hasSubMenu()) {
                SubMenu subMenu = menuItem.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++)
                    subMenu.getItem(j).setChecked(false);
            }
            else
                menuItem.setChecked(false);
        }

        selectedMenuItem.setChecked(true);
    }
}

