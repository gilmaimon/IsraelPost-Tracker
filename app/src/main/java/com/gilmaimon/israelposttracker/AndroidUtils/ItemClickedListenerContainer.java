package com.gilmaimon.israelposttracker.AndroidUtils;

interface ItemClickedListenerContainer<Ty> {
    void setItemClickedListener(ItemClickedListener<Ty> listener);
    void removeItemClickedListener();
}
