package achan.nl.uitstelgedrag.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import achan.nl.uitstelgedrag.R;
import achan.nl.uitstelgedrag.models.Category;
import achan.nl.uitstelgedrag.ui.views.TaskView;

/**
 * Created by Etienne on 26-3-2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    CategoryViewHolder holder;
    List<Category> categories;

    /**
     *
     * @param categories
     */
    public CategoryAdapter(List<Category> categories){
        this.categories = categories;
    }

    /**
     *
     * @return
     */
    public List<Category> getTasks() {
        return categories;
    }

    /**
     *
     * @param position
     * @param category
     */
    public void addItem(int position, Category category){
        categories.add(position, category);
        notifyItemInserted(position);
    }

    /**
     *
     * @param position
     */
    public void removeItem(int position){
        categories.remove(position);
        notifyItemRemoved(position);
    }

    /**
     *
     */
    public void getChild(){
        // TODO: 28-3-2016 ?
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_category_view, parent, false);
        holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        // TODO: 28-3-2016 multiple TABLE_TASKS handling, dynamic list size.
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     *
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder{

        public TextView categoryLabel;
        public TaskView task;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            // FIXME: 28-3-2016 multiple TABLE_TASKS, single category.
            //categoryLabel = (TextView) itemView.findViewById(/* ? */);
            //task = (TaskView) itemView.findViewById(R.id.TaskView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("UISTELGEDRAG", "CategoryViewHolder.OnClick called!");
                }
            });
        }
    }
}
