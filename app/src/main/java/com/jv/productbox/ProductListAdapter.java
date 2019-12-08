package com.jv.productbox;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jv.productbox.model.callback.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> items;

    public ProductListAdapter(Context context, List<Product> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_product, parent, false);
        viewHolder = new ViewHolderOne(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = items.get(position);
        ViewHolderOne viewHolderOne = (ViewHolderOne) holder;
        viewHolderOne.tvName.setText(product.getName());
        viewHolderOne.tvUser.setText(product.getUserid());

        viewHolderOne.tvDate.setText(product.getCreatedate());

//                if (newsBean.getImg_list() != null && newsBean.getImg_list().size() > 0) {
//                    Glide.with(context)
//                            .load(newsBean.getImg_list().get(0))
//                            .error(R.mipmap.ic_article_delete)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .into(ViewHolderOne.ivImage);
//                }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();

                Product product1 = items.get(pos - 1);

                Intent intent = new Intent(context, ProductInfoActivity.class);
                intent.putExtra(ProductInfoActivity.TAG_PRODUCT, product1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolderOne extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_product_name)
        TextView tvName;
        @BindView(R.id.tv_user)
        TextView tvUser;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public ViewHolderOne(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
