package com.kanishk.recyclerviewandsearchmenu.articlesearch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kanishk.recyclerviewandsearchmenu.ApplicationContextProvider;
import com.kanishk.recyclerviewandsearchmenu.interfaces.OnBottomReachedListener;
import com.kanishk.recyclerviewandsearchmenu.R;
import com.kanishk.recyclerviewandsearchmenu.apiresponse.ResponseContent;
import com.kanishk.recyclerviewandsearchmenu.interfaces.RecyclerViewItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

    //region Member variables
    List<ResponseContent> responseContentList;
    OnBottomReachedListener onBottomReachedListener;
    RecyclerViewItemClickListener onItemClickListener;
    //endregion Member variables

    //region Constructor
    public ArticlesAdapter(List<ResponseContent> responseContentList) {
        this.responseContentList = responseContentList;
    }
    //endregion Constructor

    //region Getters and Setters
    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //endregion Getters and Setters

    //region Override methods for RecyclerView.Adapter

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        ArticleViewHolder viewHolder = new ArticleViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ResponseContent responseContent = this.responseContentList.get(position);
        holder.mHeadline.setText(responseContent.getHeadline().getMain());
        if (!responseContent.getThumbnailURL().toString().equals(""))
            Picasso.get().load(responseContent.getThumbnailURL().toString()).into(holder.mThumbnail);

        if (position == this.responseContentList.size() - 1 && onBottomReachedListener != null){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return this.responseContentList.size();
    }

    //endregion Override methods for RecyclerView.Adapter

    //region Private Helper Methods
    public void updateList(List<ResponseContent> newResponseContents) {
        int currentLength = this.responseContentList.size();
        this.responseContentList.addAll(newResponseContents);
        notifyItemRangeChanged(currentLength,newResponseContents.size());
    }
    //endregion Private Helper Methods

    //region ViewHolder class
    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView mHeadline;
        private ImageView mThumbnail;
        public ArticleViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            mHeadline = (TextView) view.findViewById(R.id.tv_headline);
            mThumbnail = (ImageView) view.findViewById(R.id.iv_article_thumbnail);
            mThumbnail.setImageDrawable(ApplicationContextProvider.getContext().getResources().getDrawable(R.drawable.article,null ));
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            onItemClickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }
    //endregion ViewHolder class

}
