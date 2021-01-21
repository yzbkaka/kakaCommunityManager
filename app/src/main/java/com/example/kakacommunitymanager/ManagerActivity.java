package com.example.kakacommunitymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.kakacommunitymanager.Constant.BASE_ADDRESS;

public class ManagerActivity extends AppCompatActivity {

    private RefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private CommunityAdapter communityAdapter;

    private List<HomeArticle> communityArticleList = new ArrayList<>();

    private ImageView errorImage;

    private ProgressDialog progressDialog;

    private int curPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        initView();
    }

    private void initView() {
        errorImage = (ImageView) findViewById(R.id.community_error);
        refreshLayout = (RefreshLayout) findViewById(R.id.community_swipe_refresh_layout);
        initRefreshView();
        recyclerView = (RecyclerView) findViewById(R.id.community_recycler_view);
        initRecyclerView();

        showProgressDialog();
        getCommunityJSON(1);
        communityAdapter.setOnItemCLickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onItemCollectClick(int position) {

            }
        });
        errorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommunityJSON(1);
            }
        });
    }

    private void initRefreshView() {
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        refreshLayout.setRefreshHeader(new ClassicsHeader(MyApplication.getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(MyApplication.getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                communityArticleList.clear();
                getCommunityJSON(1);
                curPage = 1;
                refreshlayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                curPage++;
                getCommunityJSON(curPage);
                refreshlayout.finishLoadMore();
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.getContext());
        communityAdapter = new CommunityAdapter(communityArticleList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(communityAdapter);
    }

    private void getCommunityJSON(int page) {
        HttpUtil.OkHttpGET(BASE_ADDRESS + "/index" + "/" + page, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        errorImage.setVisibility(View.VISIBLE);
                        Toast.makeText(MyApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseCommunityJSON(responseData);
                if (!isDestroy(ManagerActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            errorImage.setVisibility(View.GONE);
                            communityAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void parseCommunityJSON(String responseData) {
        try {
            JSONObject jsonData = new JSONObject(responseData);
            JSONArray discussPosts = jsonData.getJSONArray("discussPosts");
            for (int i = 0; i < discussPosts.length(); i++) {
                JSONObject jsonObject = discussPosts.getJSONObject(i);
                JSONObject discussPost = jsonObject.getJSONObject("discussPost");  //解析文章数据
                HomeArticle homeArticle = new HomeArticle();
                homeArticle.setDiscussPostId(discussPost.getString("id"));
                homeArticle.setTitle(discussPost.getString("title"));
                homeArticle.setContent(discussPost.getString("content"));
                homeArticle.setNiceDate(discussPost.getString("createTime"));
                JSONObject user = jsonObject.getJSONObject("user");
                homeArticle.setAuthor(user.getString("username"));
                communityArticleList.add(homeArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ManagerActivity.this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }
}
