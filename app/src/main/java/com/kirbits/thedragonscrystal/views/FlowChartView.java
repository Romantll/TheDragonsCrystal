package com.kirbits.thedragonscrystal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.kirbits.thedragonscrystal.models.FlowNode;

import java.util.List;

public class FlowChartView extends View {
    private List<FlowNode> nodes;
    private Paint nodePaint, textPaint, linePaint;
    private float offsetX = 0, offsetY = 0;
    private float scale = 1.0f;
    private float lastTouchX, lastTouchY;
    private boolean isInitialized = false;

    public FlowChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(5f);
    }

    public void setNodes(List<FlowNode> nodes) {
        this.nodes = nodes;
        isInitialized = false; // Reset centering
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (nodes == null || nodes.isEmpty()) return;

        // Initialize centering only once
        if (!isInitialized) {
            autoCenter();
            isInitialized = true;
        }

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        int radius = 60;

        // Draw lines first
        for (FlowNode node : nodes) {
            if (node.getChildren() == null) continue;
            for (int childId : node.getChildren()) {
                FlowNode child = findNodeById(childId);
                if (child != null) {
                    canvas.drawLine(node.getX(), node.getY(), child.getX(), child.getY(), linePaint);
                }
            }
        }

        // Draw nodes
        for (FlowNode node : nodes) {
            if (node.isEnding()) {
                nodePaint.setColor(Color.parseColor("#FFD700")); // Gold for endings
            } else if (node.isDeath()) {
                nodePaint.setColor(Color.RED); // Red for death nodes
            } else if (node.isVisited()) {
                nodePaint.setColor(Color.parseColor("#8A2BE2")); // Purple for visited
            } else {
                nodePaint.setColor(Color.LTGRAY); // Light gray for unlocked but not visited
            }

            canvas.drawCircle(node.getX(), node.getY(), radius, nodePaint);
            canvas.drawText(String.valueOf(node.getId()), node.getX() - 20, node.getY() + 10, textPaint);
        }
        canvas.restore();
    }

    private FlowNode findNodeById(int id) {
        for (FlowNode node : nodes) {
            if (node.getId() == id) return node;
        }
        return null;
    }

    /** Automatically center and scale nodes on screen */
    private void autoCenter() {
        if (nodes == null || nodes.isEmpty()) return;

        // Find bounds
        float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
        for (FlowNode node : nodes) {
            if (node.getX() < minX) minX = node.getX();
            if (node.getX() > maxX) maxX = node.getX();
            if (node.getY() < minY) minY = node.getY();
            if (node.getY() > maxY) maxY = node.getY();
        }

        float chartWidth = maxX - minX + 200;
        float chartHeight = maxY - minY + 200;

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        if (viewWidth == 0 || viewHeight == 0) return;

        // Scale to fit
        scale = Math.min(viewWidth / chartWidth, viewHeight / chartHeight);

        // Center
        offsetX = (viewWidth - chartWidth * scale) / 2 - minX * scale;
        offsetY = (viewHeight - chartHeight * scale) / 2 - minY * scale;
    }

    // Simple drag for scrolling
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastTouchX;
                float dy = event.getY() - lastTouchY;
                offsetX += dx;
                offsetY += dy;
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
