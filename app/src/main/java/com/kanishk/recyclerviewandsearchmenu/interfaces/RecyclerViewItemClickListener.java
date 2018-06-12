package com.kanishk.recyclerviewandsearchmenu.interfaces;

import android.view.View;

public interface RecyclerViewItemClickListener {
    void onItemClick(int position, View v);
    void onItemLongClick(int position, View v);
}
