package app.thaqib.org;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Classes extends Fragment {

    PullRefreshLayout Refresh;
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "";

    ArrayList<HashMap<String, String>> contactList;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.classes_fragment, container, false);
        lv = view.findViewById(R.id.classes_list);

        Refresh = view.findViewById(R.id.classes_pull_to_refresh);

        contactList = new ArrayList<>();

        lv = view.findViewById(R.id.classes_list);

        new Classes.GetClasses().execute();

        Refresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactList.clear();
                new Classes.GetClasses().execute();
                Refresh.setRefreshing(false);
            }
        });

        return view;
    }
    private class GetClasses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("درحال دریافت اطلاعات...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray Classes = jsonObj.getJSONArray("Classes");

                    // looping through All Contacts
                    for (int i = 0; i < Classes.length(); i++) {
                        JSONObject c = Classes.getJSONObject(i);

                        String name = c.getString("Name");
                        String Description = c.getString("Des");
                        String Time = c.getString("Date_Time");
                        String During = c.getString("During");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("Desc", Description);
                        contact.put("during", During);
                        contact.put("D_T", Time);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "به اینترنت متصل نیستید! اینترنت خود را چک کنید و دوباره امتحان کنید.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), contactList,
                    R.layout.list_item_classes, new String[]{"name", "Desc",
                    "during","D_T"}, new int[]{R.id.name_class,
                    R.id.description_class, R.id.date_and_time_class,R.id.during_class});

            lv.setAdapter(adapter);
        }

    }
}

