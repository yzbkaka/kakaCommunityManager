package com.example.kakacommunitymanager.search;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kakacommunitymanager.R;
import com.example.kakacommunitymanager.community.CommunityAdapter;
import com.example.kakacommunitymanager.constant.HomeArticle;
import com.example.kakacommunitymanager.constant.HttpUtil;
import com.example.kakacommunitymanager.constant.MyApplication;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.kakacommunitymanager.constant.Constant.BASE_ADDRESS;

public class ShowSearchActivity extends AppCompatActivity {

    private SmartRefreshLayout refreshLayout;

    private ImageView back;

    private TextView title;

    private String keyWord;

    private RecyclerView recyclerView;

    private List<HomeArticle> articleList = new ArrayList<>();

    private CommunityAdapter communityAdapter;

    private int curPage = 0;

    private ImageView errorImage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search);
        initView();
        showProgressDialog();
        getSearchJSON(0);
    }

    private void initView() {
        errorImage = (ImageView) findViewById(R.id.search_error);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.query_refresh_layout);
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        refreshLayout.setRefreshHeader(new ClassicsHeader(MyApplication.getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(MyApplication.getContext()));
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                articleList.clear();
                getSearchJSON(0);
                curPage = 0;
                refreshlayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                curPage++;
                getSearchJSON(curPage);
                refreshlayout.finishLoadMore();

            }
        });
        back = (ImageView) findViewById(R.id.query_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.query_title);
        recyclerView = (RecyclerView) findViewById(R.id.query_recycler_view);
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.getContext());
        communityAdapter = new CommunityAdapter(articleList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(communityAdapter);
    }

    private void getSearchJSON(int page) {
        Intent intent = getIntent();
        keyWord = intent.getStringExtra("keyword");
        title.setText(keyWord);
        HttpUtil.OkHttpGET(BASE_ADDRESS + "/search" + "/" + keyWord, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (!isDestroy(ShowSearchActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            errorImage.setVisibility(View.VISIBLE);
                            Toast.makeText(MyApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                parseSearchJSON(responseData);
                if (!isDestroy(ShowSearchActivity.this)) {
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

    private void parseSearchJSON(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                HomeArticle homeArticle = new HomeArticle();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject post = jsonObject.getJSONObject("post");
                homeArticle.setDiscussPostId(post.getString("id"));
                homeArticle.setTitle(post.getString("title"));
                homeArticle.setContent(post.getString("content"));
                homeArticle.setNiceDate(post.getString("createTime"));
                JSONObject user = jsonObject.getJSONObject("user");
                homeArticle.setAuthor(user.getString("username"));
                articleList.add(homeArticle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        communityAdapter.setOnItemCLickListener(new CommunityAdapter.OnItemClickListener() {

            @Override
            public void onUpdateClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                articleList.remove(position);
                communityAdapter.notifyDataSetChanged();
                Toast.makeText(ShowSearchActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });
        errorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchJSON(0);
            }
        });
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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
