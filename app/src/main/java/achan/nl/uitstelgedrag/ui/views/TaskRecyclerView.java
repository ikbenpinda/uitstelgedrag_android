package achan.nl.uitstelgedrag.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Etienne on 05/02/17.
 */

public class TaskRecyclerView extends RecyclerView { // todo extend viewgroup, abstractions.

    private View emptyView;

    final private RecyclerView.AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    public TaskRecyclerView(Context context) {
        super(context);
    }

    public TaskRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void checkIfEmpty(){
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            Log.i("TaskRecyclerView", "Empty view visibility: " + emptyViewVisible);
            emptyView.setVisibility(emptyViewVisible? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    public void setEmptyView(View emptyView){
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
            oldAdapter.unregisterAdapterDataObserver(observer);
        super.setAdapter(adapter);
        if (adapter != null)
            adapter.registerAdapterDataObserver(observer);

        checkIfEmpty();
    }
}
