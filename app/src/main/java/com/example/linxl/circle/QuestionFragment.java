package com.example.linxl.circle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.linxl.circle.gson.QuestionItem;
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
public class QuestionFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private List<QuestionItem> items;
    private List<QuestionItem> allItems;
    private LinearLayoutManager layoutManager;
    private QuestionAdapter adapter;
    private String currentId = "0";
    private boolean hasMore = true;
    private int lastVisibleItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_question, container, false);

        Log.d("————QuesFragment————", "onCreate");

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        allItems = new ArrayList<>();
        layoutManager = new LinearLayoutManager(MyApplication.getContext());
        adapter = new QuestionAdapter(allItems);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        requestForQuestion();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && hasMore) {
                    requestForQuestion();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
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
                requestForQuestion();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void requestForQuestion() {
        String address = getString(R.string.server_ip) + "questionServlet";
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
                                new TypeToken<List<QuestionItem>>(){}.getType());
                        QuestionItem item = items.get(items.size() - 1);
                        currentId = item.getQuestionId();
                        allItems.addAll(items);

                        Log.d("————QuesFragment————", "currentId: " + currentId);
                        Log.d("————QuesFragment————", "items: " + allItems);

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

}
