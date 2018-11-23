package com.gilmaimon.israelposttracker.AndroidUtils;

public interface ItemClickedListenerContainer<Ty> {
    void setItemClickedListener(ItemClickedListener<Ty> listener);
    void removeItemClickedListener();
}
