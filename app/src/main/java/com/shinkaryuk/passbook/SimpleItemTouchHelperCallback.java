package com.shinkaryuk.passbook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ArrayDataSourcePass passAdapter;
    private ArrayDataSourceNotes noteAdapter;
    private ArrayDataSourceImg imgAdapter;
    private Context mContext;
    private static int DEL_CANVAS_COLOR = 0;
    Drawable deleteMark;
    Drawable archiveMark;

    public SimpleItemTouchHelperCallback(ArrayDataSourcePass adapter, Context context) {
        passAdapter = adapter;
        mContext = context;
        init();
    }

    public SimpleItemTouchHelperCallback(ArrayDataSourceNotes adapter, Context context) {
        noteAdapter = adapter;
        mContext = context;
        init();
    }

    public SimpleItemTouchHelperCallback(ArrayDataSourceImg adapter, Context context) {
        imgAdapter = adapter;
        mContext = context;
        init();
    }

    private void init(){
        deleteMark = mContext.getDrawable(android.R.drawable.ic_menu_delete);
        archiveMark = mContext.getDrawable(android.R.drawable.ic_menu_upload);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;// это чтобы не дивгался верх и вниз, было true
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = 0;// - 0 - чтобы не перемещалось вверх и вниз ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (passAdapter != null) {
            passAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        } else if (noteAdapter != null) {
            noteAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        } else if (imgAdapter != null) {
            imgAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == 16) {
            if (passAdapter != null) {
                passAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            } else if (noteAdapter != null) {
                noteAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            } else if (imgAdapter != null) {
                imgAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        } else if (direction == 32) {
            if (passAdapter != null) {
                passAdapter.onItemDismissR(viewHolder.getAdapterPosition());
            } else if (noteAdapter != null) {
                noteAdapter.onItemDismissR(viewHolder.getAdapterPosition());
            } else if (imgAdapter != null) {
                imgAdapter.onItemDismissR(viewHolder.getAdapterPosition());
            }
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Rect mRect = new Rect();
        int marginMark = 20;
        mRect.left = 0;
        mRect.right = recyclerView.getWidth();
        mRect.top = viewHolder.itemView.getTop();
        mRect.bottom = viewHolder.itemView.getBottom();
        Paint mPaint = new Paint();

        int xMarkLeft = 0;
        int xMarkRight = 0;
        int xMarkTop = viewHolder.itemView.getTop();
        int xMarkBottom = viewHolder.itemView.getBottom();


        if (dX<=0){
            xMarkLeft = mRect.right - marginMark - viewHolder.itemView.getHeight();
            xMarkRight = mRect.right - marginMark;
            if (Math.abs(dX) <= recyclerView.getWidth() / 3) {
                mPaint.setColor(mContext.getResources().getColor(R.color.red_swipe_left_itemholder, null));//Color.RED);
            } else {
                mPaint.setColor(Color.RED);
            }

        } else {
            xMarkLeft = mRect.left;
            xMarkRight = mRect.left + marginMark + viewHolder.itemView.getHeight();

            if (Math.abs(dX) <= recyclerView.getWidth() / 3) {
                mPaint.setColor(mContext.getResources().getColor(R.color.red_swipe_right_itemholder, null));
            } else mPaint.setColor(Color.GREEN);
        }

        c.drawRect(mRect, mPaint);
        //if (Math.abs(dX) <= recyclerView.getWidth() / 3 * 2) {
            //viewHolder.itemView.setTranslationX(dX);
        //}


        if (dX <= 0) {
            // Определяет размеры квадратой области за сдвинутым элеметом, где будет нарисован xMark
            deleteMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            deleteMark.draw(c); // Рисует иконку удаления


            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(40F);//viewHolder.itemView.getHeight()/2);
            c.drawText(mContext.getResources().getString(R.string.action_swipe_delete), recyclerView.getWidth() - viewHolder.itemView.getHeight() - 220F,
                    viewHolder.itemView.getTop() + viewHolder.itemView.getHeight() - 30F, mPaint);
        } else {
            // Определяет размеры квадратой области за сдвинутым элеметом, где будет нарисован xMark
            archiveMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            archiveMark.draw(c); // Рисует иконку удаления

            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(40F);//viewHolder.itemView.getHeight()/2);
            c.drawText(mContext.getResources().getString(R.string.action_swipe_export), xMarkRight, viewHolder.itemView.getTop() + viewHolder.itemView.getHeight() - 30F, mPaint);
        }
        if (imgAdapter != null) {//чтобы двигался в обе стороны только элемент документов/фото
            /*if (dX <= 0)*/
            viewHolder.itemView.setTranslationX(dX);//если условие раскомментировать, то будет двигаться только влево
        } else if (passAdapter != null) {
            if (dX <= 0) viewHolder.itemView.setTranslationX(dX);
        } else if (noteAdapter != null) {
            if (dX <= 0) viewHolder.itemView.setTranslationX(dX);
        }
    }
}