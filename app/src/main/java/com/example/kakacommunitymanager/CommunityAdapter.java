package com.example.kakacommunitymanager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        HomeArticle homeArticle = communityArticleList.get(position);
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
                onItemClickListener.onItemClick(position);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return communityArticleList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemCollectClick(int position);
    }

    public void setOnItemCLickListener(OnItemClickListener onItemCLickListener) {
        this.onItemClickListener = onItemCLickListener;
    }

}
