package com.kirbits.thedragonscrystal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.kirbits.thedragonscrystal.models.FlowNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlowChartView extends View {
    private List<FlowNode> nodes;
    private Paint nodePaint, textPaint, linePaint;
    private float offsetX = 0, offsetY = 0;
    private float scale = 1.0f;
    private float lastTouchX, lastTouchY;
    private boolean firstLayout = true;

    public FlowChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5f);
    }

    public void setNodes(List<FlowNode> nodes) {
        this.nodes = Objects.requireNonNullElseGet(nodes, ArrayList::new);
        firstLayout = true;  // trigger auto-center on next draw
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (nodes == null || nodes.isEmpty()) return;

        if (firstLayout) {
            autoFitAndCenter();
            firstLayout = false;
        }

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        int radius = 80;
        int spacingX = 200;
        int spacingY = 200;

        // Draw connections
        for (FlowNode node : nodes) {
            List<Integer> children = node.getChildren();
            if (children == null) continue;
            for (int childId : children) {
                FlowNode child = findNodeById(childId);
                if (child != null) {
                    float startX = node.getX() * spacingX + radius;
                    float startY = node.getY() * spacingY + radius;
                    float endX = child.getX() * spacingX + radius;
                    float endY = child.getY() * spacingY + radius;
                    canvas.drawLine(startX, startY, endX, endY, linePaint);
                }
            }
        }

        // Draw nodes
        for (FlowNode node : nodes) {
            int cx = node.getX() * spacingX;
            int cy = node.getY() * spacingY;

            if (node.isEnding()) {
                nodePaint.setColor(Color.parseColor("#FFD700")); // gold
            } else if (node.isVisited()) {
                nodePaint.setColor(Color.parseColor("#8A2BE2")); // purple
            } else {
                nodePaint.setColor(Color.GRAY);
            }

            canvas.drawCircle(cx, cy, radius, nodePaint);
            canvas.drawText(node.isVisited() ? String.valueOf(node.getId()) : "?", cx - 20, cy + 10, textPaint);
        }
        canvas.restore();
    }

    private FlowNode findNodeById(int id) {
        for (FlowNode node : nodes) {
            if (node.getId() == id) return node;
        }
        return null;
    }

    /** Auto-fit the flowchart inside the screen and center it */
    private void autoFitAndCenter() {
        if (nodes == null || nodes.isEmpty()) return;

        int spacingX = 200;
        int spacingY = 200;
        int radius = 80;

        // Find bounds of all nodes
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (FlowNode node : nodes) {
            int x = node.getX() * spacingX;
            int y = node.getY() * spacingY;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int chartWidth = (maxX - minX) + radius * 2;
        int chartHeight = (maxY - minY) + radius * 2;

        // Calculate scale to fit screen (with padding)
        float scaleX = (float) getWidth() / (chartWidth * 1.2f);
        float scaleY = (float) getHeight() / (chartHeight * 1.2f);
        scale = Math.min(scaleX, scaleY);

        // Center the chart
        offsetX = getWidth() / 2f - ((minX + maxX) / 2f) * scale;
        offsetY = getHeight() / 2f - ((minY + maxY) / 2f) * scale;
    }

    // Dragging
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
