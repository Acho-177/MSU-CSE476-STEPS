package edu.msu.wuhaoyu1.puzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view class for our Puzzle.
 */
public class PuzzleView extends View {

    /**
     * Paint object we will use to draw a line
     */
    private Paint linePaint;

    /**
     * The actual puzzle
     */
    private Puzzle puzzle;

    public PuzzleView(Context context) {
        super(context);
        init(null, 0);
    }

    public PuzzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PuzzleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        puzzle = new Puzzle(getContext());

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xff008000);
        linePaint.setStrokeWidth(3);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        puzzle.draw(canvas);
        //canvas.drawLine(0,  0, 400, 100, linePaint);
    }


}