package com.example.kakacommunitymanager.community;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kakacommunitymanager.constant.HomeArticle;
import com.example.kakacommunitymanager.constant.HttpUtil;
import com.example.kakacommunitymanager.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.kakacommunitymanager.constant.Constant.BASE_ADDRESS;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<HomeArticle> communityArticleList;

    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView fresh;
        TextView author;
        TextView time;
        TextView title;
        TextView content;
        TextView tag;
        Button update;
        Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.community_item_layout);
            fresh = (TextView) itemView.findViewById(R.id.community_item_fresh);
            author = (TextView) itemView.findViewById(R.id.community_item_author);
            time = (TextView) itemView.findViewById(R.id.community_item_time);
            title = (TextView) itemView.findViewById(R.id.community_item_title);
            content = (TextView) itemView.findViewById(R.id.community_item_content);
            tag = (TextView) itemView.findViewById(R.id.community_item_tag);
            update = (Button)itemView.findViewById(R.id.community_item_update);
            delete = (Button)itemView.findViewById(R.id.community_item_delete);
        }
    }

    public CommunityAdapter(List<HomeArticle> communityArticleList) {
        this.communityArticleList = communityArticleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final HomeArticle homeArticle = communityArticleList.get(position);
        holder.author.setText(homeArticle.getAuthor());
        holder.time.setText(homeArticle.getNiceDate());
        holder.title.setText(String.valueOf(Html.fromHtml(homeArticle.getTitle())));
        if (homeArticle.getContent() != null) {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(homeArticle.getContent());
        } else {
            holder.content.setVisibility(View.GONE);
            holder.content.setText(homeArticle.getContent());
        }
        boolean fresh = homeArticle.isFresh();
        if (fresh) {
            holder.fresh.setVisibility(View.VISIBLE);
        } else {
            holder.fresh.setVisibility(View.GONE);
        }
        String tag = homeArticle.getTag();
        if (!(tag == null || tag.length() == 0)) {
            holder.tag.setText(tag);
            holder.tag.setVisibility(View.VISIBLE);
        } else {
            holder.tag.setVisibility(View.GONE);
        }
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onUpdateClick(position);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDeleteRequest(homeArticle.getDiscussPostId());
                onItemClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityArticleList.size();
    }

    public interface OnItemClickListener {
        void onUpdateClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemCLickListener(OnItemClickListener onItemCLickListener) {
        this.onItemClickListener = onItemCLickListener;
    }

    private void sendDeleteRequest(String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();
        HttpUtil.OkHttpPOST(BASE_ADDRESS + "/discuss" + "/delete", requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if(responseData.contains("0")) {
                    //Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
