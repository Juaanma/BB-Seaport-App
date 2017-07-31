package jms.puertobahiablanca;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    OnChangeFragmentButtonPressed,
                    FragmentManager.OnBackStackChangedListener{

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Create a Snackbar of indefinite length.
        TextView lastUpdateTv = (TextView) findViewById(R.id.last_update_tv);
        String last_update_date = Helper.positionCSV.get(1)[1];
        lastUpdateTv.setText("Última actualización " + last_update_date + ".");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initially, the home menu item is checked.
        Helper.updateCurrentSelectedItem(navigationView, navigationView.getMenu().getItem(0));

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            Helper.depthTXT = savedInstanceState.getStringArrayList("depthTXT");
            Helper.newsJSON = savedInstanceState.getString("newsJSON");
            Helper.positionCSV = ((SerializableList) savedInstanceState
                    .getSerializable("positionCSV")).getList();
            Helper.beaconsCSV = ((SerializableList) savedInstanceState
                    .getSerializable("positionCSV")).getList();
            return;
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        // The Main Fragment is instantiated first.
        MainFragment mainFragment = new MainFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, mainFragment);
        ft.commit();
    }

    public void modifyHomeButton(final Boolean upButtonEnabled) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (upButtonEnabled)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(upButtonEnabled);

        // If we're not using the up button, create the hamburger button
        // and link it with the navigation drawer.
        if (!upButtonEnabled) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, ((Toolbar) findViewById(R.id.toolbar)), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }
        // If not, configure the home button to call onBackPressed when pressed.
        else {
            ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    // Opens next fragment and modifies the navigation drawer.
    // It's called from MainFragment when one of the main navigation
    // buttons are pressed.
    public void OnChangeFragmentButtonPressed(String nextFragment) {
        MenuItem menuItem = null;

        switch (nextFragment) {
            case "beacons":
                menuItem = navigationView.getMenu().getItem(2).getSubMenu().getItem(2);
                break;
            case "shipspos":
                menuItem = navigationView.getMenu().getItem(1).getSubMenu().getItem(0);
                break;
            case "depth":
                menuItem = navigationView.getMenu().getItem(2).getSubMenu().getItem(1);
                break;
            case "vts":
                menuItem = navigationView.getMenu().getItem(2).getSubMenu().getItem(0);
                break;
        }
        onNavigationItemSelected(menuItem);
    }

    @Override
     public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the downloaded data.
        savedInstanceState.putStringArrayList("depthTXT", (ArrayList<String>) Helper.depthTXT);
        savedInstanceState.putString("newsJSON", Helper.newsJSON);
        savedInstanceState.putSerializable("positionCSV", new SerializableList(Helper.positionCSV));
        savedInstanceState.putSerializable("beaconsCSV", new SerializableList(Helper.beaconsCSV));

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fm = getSupportFragmentManager();
        MenuItem homeMenuItem = navigationView.getMenu().getItem(0);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() > 0)
            fm.popBackStack();
        else if (!homeMenuItem.isChecked()) {
            MainFragment mainFragment = new MainFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, mainFragment);
            ft.commit();

            Helper.updateCurrentSelectedItem(navigationView, homeMenuItem);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        // Enable the up button only when the back stack isn't empty (it has at least one fragment
        // which must go back to the previous fragment).
        Boolean upButtonEnabled = getSupportFragmentManager().getBackStackEntryCount() > 0;
        modifyHomeButton(upButtonEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            case R.id.menu_refresh:
                Intent splashScreenIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
                startActivity(splashScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        Fragment fragment = null;

        switch (id) {
            case R.id.nav_home:
                fragment = new MainFragment();
                break;
            case R.id.nav_vts:
                fragment = new VTSFragment();
                break;
            case R.id.nav_depth:
                fragment = new DepthFragment();
                break;
            case R.id.nav_beacons:
                fragment = new BeaconsFragment();
                break;
            case R.id.nav_ships_pos:
                fragment = new ShipsPositionsFragment();
                break;
            case R.id.nav_announcements:
                fragment = new AnnouncementsFragment();
                break;
            case R.id.nav_movements:
                fragment = new ExpectedMovementsFragment();
                break;
            case R.id.nav_anchorage:
                fragment = new ShipsInAnchorageFragment();
                break;
        }

        Helper.fragmentTransaction(getSupportFragmentManager(), fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Helper.updateCurrentSelectedItem(navigationView, menuItem);

        return true;
    }


}
