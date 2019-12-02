package com.jv.productbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        viewHolderOne.tvName.setText(product.getProductname());
        viewHolderOne.tvUser.setText(product.getUser());
        viewHolderOne.tvDate.setText(product.getDate());

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

                Toast.makeText(context, items.get(pos - 1).getProductname(), Toast.LENGTH_SHORT).show();
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
