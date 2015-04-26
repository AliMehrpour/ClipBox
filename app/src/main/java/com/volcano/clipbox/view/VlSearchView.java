package com.volcano.clipbox.view;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * Extension of {@link SearchView} with hooks to notify when the
 * action view is expanded or collapsed.
 */
public final class VlSearchView extends SearchView {
    private SearchViewListener mExpandCollapseListener;

    /**
     * Interface for being notified when the SearchView expands or collapses.
     */
    public interface SearchViewListener {
        /**
         * The SearchView has expanded
         */
        public void onExpandSearch();

        /**
         * The SearchView has collapsed
         */
        public void onCollapseSearch();
    }

    public VlSearchView(Context context) {
        this(context, null);
    }

    public VlSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        if (mExpandCollapseListener != null) {
            mExpandCollapseListener.onCollapseSearch();
        }
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        if (mExpandCollapseListener != null) {
            mExpandCollapseListener.onExpandSearch();
        }
    }

    /**
     * @param listener
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mExpandCollapseListener = listener;
    }
}
