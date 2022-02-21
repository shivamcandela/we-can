package com.candela.wecan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.candela.wecan.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.onFragmentBtnSelected {

    Toolbar toolbar;
    private FragmentManager fragmentmanager;
    private FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawer;
    TextView nav_user;
    TextView nav_server;
    TextView nav_resource_realm;
    public static String username = "";
    public static Context context;
    public static boolean active=false;
    public static String getUserName() {
        return username;
    }

    @SuppressLint({"RestrictedApi", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();


        SharedPreferences sharedPreferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();
        username = (String) keys.get("user_name");

        String current_ip = (String) keys.get("current-ip");
        String current_resource = (String) keys.get("current-resource");
        String current_realm = (String) keys.get("current-realm");
        Log.e("check_box", username);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        nav_user = headerView.findViewById(R.id.user);
        nav_server = headerView.findViewById(R.id.server);
        nav_resource_realm = headerView.findViewById(R.id.resource_realm);

        nav_user.setText("User: " + username);
        nav_server.setText("Server: "+ current_ip);
        nav_resource_realm.setText("Realm: "+ current_realm + "\nResource: " + current_resource);

        drawer = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar, R.string.open,R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentmanager = getSupportFragmentManager();
        fragmentTransaction = fragmentmanager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment_content_navigation, new HomeFragment());
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Disconnect from Session")
                .setMessage("Disconnect from Server?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomeFragment.handler_link.removeCallbacks(HomeFragment.runnable_link);
                        HomeFragment.handler_speedometer_thread.removeCallbacks(HomeFragment.runnable_speedometer);
                        HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
                        StartupActivity.lf_resource.lfresource.destroy();
//                        Intent myIntent = new Intent(getApplicationContext(), StartupActivity.class);
//                        startActivity(myIntent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.home){
            fragmentmanager = getSupportFragmentManager();
            fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment_content_navigation, new HomeFragment());
            fragmentTransaction.commit();
        }
        return true;
    }

    @Override
    public void onButtonSelected() {

    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_navigation);
    }
}