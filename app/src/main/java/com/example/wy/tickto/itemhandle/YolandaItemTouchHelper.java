package com.example.wy.tickto.itemhandle;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by 56989 on 2018/4/22.
 */

public class YolandaItemTouchHelper extends ItemTouchHelper {
    public ItemTouchHelper.Callback mCallback = null;
    public YolandaItemTouchHelper(ItemTouchHelper.Callback callback) {
        super(callback);
        mCallback = callback;
    }
    public Callback getCallback(){
        return mCallback;

    }

}
