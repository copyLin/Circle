package com.example.linxl.circle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.linxl.circle.gson.IdleItem;
import com.example.linxl.circle.gson.LostItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class LostFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private List<LostItem> items;
    private List<LostItem> allItems;
    private StaggeredGridLayoutManager layoutManager;
    private LostAdapter adapter;
    private String currentId = "0";
    private boolean hasMore = true;
    private int lastVisibleItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_lost, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        allItems = new ArrayList<>();
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new LostAdapter(allItems);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        requestForLost();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && hasMore) {
                    requestForLost();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisibleItems = new int[layoutManager.getSpanCount()];
                lastVisibleItems = layoutManager.findLastVisibleItemPositions(lastVisibleItems);
                lastVisibleItem = findMax(lastVisibleItems);
            }

        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorTheme);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentId = "0";
                allItems.clear();
                hasMore = true;
                adapter.changeState(0);
                requestForLost();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void requestForLost() {
        String address = getString(R.string.server_ip) + "lostServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("currentId", currentId)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();

                    if (responseData.equals("NoMoreData")){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hasMore = false;
                                adapter.changeState(1);
                            }
                        });

                    }else {
                        Gson gson = new Gson();
                        items = gson.fromJson(responseData,
                                new TypeToken<List<LostItem>>(){}.getType());
                        LostItem item = items.get(items.size() - 1);
                        currentId = item.getLostId();
                        allItems.addAll(items);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();

                            }
                        });
                    }

                }
            }
        });
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
