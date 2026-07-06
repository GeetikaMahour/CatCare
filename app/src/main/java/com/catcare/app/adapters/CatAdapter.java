package com.catcare.app.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.catcare.app.R;
import com.catcare.app.models.Cat;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder> {

    public interface OnCatClickListener {
        void onCatClick(Cat cat);
    }

    private final List<Cat> cats;
    private final OnCatClickListener listener;

    public CatAdapter(List<Cat> cats, OnCatClickListener listener) {
        this.cats = cats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cat, parent, false);
        return new CatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Cat cat = cats.get(position);
        holder.bind(cat);
        holder.itemView.setOnClickListener(v -> listener.onCatClick(cat));
    }

    @Override
    public int getItemCount() { return cats.size(); }

    static class CatViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgCat;
        TextView tvName, tvBreed, tvAge, tvWeight;

        CatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCat   = itemView.findViewById(R.id.img_cat);
            tvName   = itemView.findViewById(R.id.tv_cat_name);
            tvBreed  = itemView.findViewById(R.id.tv_cat_breed);
            tvAge    = itemView.findViewById(R.id.tv_cat_age);
            tvWeight = itemView.findViewById(R.id.tv_cat_weight);
        }

        void bind(Cat cat) {
            tvName.setText(cat.getGenderEmoji() + " " + cat.getName());
            tvBreed.setText(cat.getBreed() == null || cat.getBreed().isEmpty()
                    ? "Mixed breed" : cat.getBreed());
            tvAge.setText(cat.getAge() + " yrs");
            tvWeight.setText(cat.getWeight() + " kg");

            if (cat.getPhotoUri() != null && !cat.getPhotoUri().isEmpty()) {
                imgCat.setImageURI(Uri.parse(cat.getPhotoUri()));
            } else {
                imgCat.setImageResource(R.drawable.ic_cat_placeholder);
            }
        }
    }
}