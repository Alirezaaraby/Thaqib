package app.thaqib.org;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStructure;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.thaqib.org.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    String versionName;
    private static String url = "";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ProgressDialog pDialog;
    String Version = "";
    String Tittle_txt = "";
    String Description_txt = "";
    String Button_txt = "";
    public boolean Internet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        versionName = BuildConfig.VERSION_NAME;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.instagram.com/thethaqib/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        viewPager.setCurrentItem(1, true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("About Creator:")
                    .setMessage("Codded By AlirezaAraby For More Information And Questions Email To:"+'\n'+"alirezaaraby5@gmail.com")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();

        }
        if (id == R.id.action_checkupdate) {
            new GetVersion().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetVersion extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("درحال دریافت اطلاعات...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(final Void... arg0) {
            JSONObject JsonMain = null;
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JsonMain = jsonObj.getJSONObject("Header");
                    Tittle_txt = JsonMain.getString("Version_Tittle");
                    Description_txt = JsonMain.getString("Version_Description");
                    Version = JsonMain.getString("Version");
                    Button_txt = JsonMain.getString("Version_Button_Text");
                    Internet = true;

                }

                catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View parentLayout = findViewById(R.id.constraintLayout);
                            Snackbar.make(parentLayout,"Json parsing error", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Internet = false;
                        }
                    });
                }
            }

            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View parentLayout = findViewById(R.id.constraintLayout);
                        Snackbar.make(parentLayout,"No Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Internet = false;
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            if (Internet){
                pDialog.dismiss();
                if (!Version.equals(versionName)) {
                    showDialog(MainActivity.this,"");
                }
                else{
                    View parentLayout = findViewById(R.id.constraintLayout);
                    Snackbar.make(parentLayout,"Your Version Is Up To Date", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            else {
                pDialog.dismiss();
            }

        }
    }

    public void showDialog(Activity activity, String msg){
        if (Internet) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.versiondialog);

            TextView Tittle = dialog.findViewById(R.id.Tittle);
            TextView Des = dialog.findViewById(R.id.Des);
            Tittle.setText(Tittle_txt);
            Des.setText(Description_txt);

            Des.setMovementMethod(LinkMovementMethod.getInstance());

            Linkify.addLinks(Des, Linkify.WEB_URLS);

            Button dialogButton = dialog.findViewById(R.id.buttonOk);
            dialogButton.setText(Button_txt);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
}
