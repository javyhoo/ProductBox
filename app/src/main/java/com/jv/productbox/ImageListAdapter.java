package com.jv.productbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> items;

    public ImageListAdapter(Context context, List<String> items) {
        this.context = context;
//        this.items = items;

        this.items.add("https://pics4.baidu.com/feed/b64543a98226cffce2611ef12f269095f703eadc.jpeg?token=58bc1c7ff6e42c53a7912d22088f88a3&amp;s=3AA87C224B3F748AD6F0F4C8010060B1");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_image, parent, false);
        viewHolder = new ViewHolderOne(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String url = items.get(position);

        ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
        Glide.with(context).load(url).into(viewHolderOne.image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolderOne extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_product_info)
        ImageView image;

        public ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
