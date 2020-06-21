package app.thaqib.org;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.widget.PullRefreshLayout;

public class News extends Fragment {
    ListView list;
    PullRefreshLayout Refresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);
        Refresh = view.findViewById(R.id.news_pull_to_refresh);
        list = view.findViewById(R.id.news_list);

        Refresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                String[] values_refresh = {"Android","IPhone","WindowsMobile","Blackberry",
                        "WebOS","Ubuntu","Windows7","Max OS X"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, values_refresh);
                list.setAdapter(adapter);
                Refresh.setRefreshing(false);
            }
        });
        return view;
    }
}

