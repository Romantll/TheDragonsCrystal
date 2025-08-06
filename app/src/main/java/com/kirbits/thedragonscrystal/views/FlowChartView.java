package com.kirbits.thedragonscrystal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kirbits.thedragonscrystal.models.FlowNode;

import java.util.List;

public class FlowChartView extends View {
    private List<FlowNode> nodes;
    private boolean debugMode = false;

    // paints
    private final Paint nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // pan & zoom state
    private float offsetX = 0, offsetY = 0, scale = 1f;
    private float lastTouchX, lastTouchY;
    private boolean initialized = false;
    private final ScaleGestureDetector scaleDetector;

    public FlowChartView(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);

        // text paint
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);

        // line paint
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(4f);

        // pinch‐zoom detector
        scaleDetector = new ScaleGestureDetector(ctx,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        scale *= detector.getScaleFactor();
                        scale = Math.max(0.3f, Math.min(scale, 3.0f));
                        invalidate();
                        return true;
                    }
                }
        );
    }

    /** Supply the nodes and reset centering */
    public void setNodes(List<FlowNode> nodes) {
        this.nodes = nodes;
        this.initialized = false;
        invalidate();
    }

    /** Flip between debug (always show IDs) and normal mode */
    public void toggleDebugMode() {
        this.debugMode = !this.debugMode;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // always give pinch events first
        scaleDetector.onTouchEvent(ev);
        if (scaleDetector.isInProgress()) return true;

        // otherwise handle pan
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = ev.getX();
                lastTouchY = ev.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - lastTouchX;
                float dy = ev.getY() - lastTouchY;
                offsetX += dx;
                offsetY += dy;
                lastTouchX = ev.getX();
                lastTouchY = ev.getY();
                invalidate();
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (nodes == null || nodes.isEmpty()) return;

        if (!initialized) {
            autoCenter();
            initialized = true;
        }

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        int radius = 60;

        // 1) draw edges
        for (FlowNode n : nodes) {
            if (n.getChildren() == null) continue;
            for (int cid : n.getChildren()) {
                FlowNode c = findNodeById(cid);
                if (c != null) {
                    canvas.drawLine(n.getX(), n.getY(), c.getX(), c.getY(), linePaint);
                }
            }
        }

        // 2) draw nodes
        for (FlowNode n : nodes) {
            // color based on state
            if (n.isVisited()) {
                if (n.isDeath())      nodePaint.setColor(Color.RED);
                else if (n.isEnding()) nodePaint.setColor(0xFFFFD700); // gold
                else                   nodePaint.setColor(0xFF8A2BE2); // purple
            } else {
                nodePaint.setColor(Color.DKGRAY);
            }


            canvas.drawCircle(n.getX(), n.getY(), radius, nodePaint);

            // label logic
            String label;
            if (debugMode) {
                label = String.valueOf(n.getId());
            } else if (!n.isVisited()) {
                label = "?";
            } else {
                label = String.valueOf(n.getId());
            }

            float tw = textPaint.measureText(label);
            canvas.drawText(
                    label,
                    n.getX() - tw/2,
                    n.getY() + textPaint.getTextSize()/2,
                    textPaint
            );
        }

        canvas.restore();
    }

    private FlowNode findNodeById(int id) {
        if (nodes == null) return null;
        for (FlowNode n : nodes) {
            if (n.getId() == id) return n;
        }
        return null;
    }

    /** Compute pan+zoom so the entire chart fits in view */
    private void autoCenter() {
        if (nodes == null || nodes.isEmpty()) return;

        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        for (FlowNode n : nodes) {
            minX = Math.min(minX, n.getX());
            maxX = Math.max(maxX, n.getX());
            minY = Math.min(minY, n.getY());
            maxY = Math.max(maxY, n.getY());
        }

        float chartW = (maxX - minX) + 2*radius;
        float chartH = (maxY - minY) + 2*radius;
        float vw = getWidth(), vh = getHeight();
        if (vw==0 || vh==0) return;

        scale   = Math.min(vw/chartW, vh/chartH);
        offsetX = (vw - chartW*scale)/2 - minX*scale + radius;
        offsetY = (vh - chartH*scale)/2 - minY*scale + radius;
    }

    // small constant for padding in autoCenter()
    private final int radius = 60;
}
