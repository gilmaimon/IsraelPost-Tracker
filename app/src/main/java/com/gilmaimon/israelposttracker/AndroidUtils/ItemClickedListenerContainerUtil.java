package com.gilmaimon.israelposttracker.AndroidUtils;

public class ItemClickedListenerContainerUtil<Ty> implements ItemClickedListenerContainer<Ty> {
    private ItemClickedListener<Ty> listener = null;

    public void setItemClickedListener(ItemClickedListener<Ty> listener) {
        this.listener = listener;
    }
    public void removeItemClickedListener() {
        this.listener = null;
    }
    public void dispatch(Ty item, int position) {
        if(listener == null) return;
        listener.itemClicked(item, position);
    }
}
