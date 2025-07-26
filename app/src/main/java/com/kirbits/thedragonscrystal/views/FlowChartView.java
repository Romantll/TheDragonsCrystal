package com.kirbits.thedragonscrystal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.kirbits.thedragonscrystal.models.FlowNode;

import java.util.List;
import java.util.Objects;

public class FlowChartView extends View {
    private List<FlowNode> nodes;
    private Paint nodePaint, textPaint, linePaint;

    private float offsetX = 0, offsetY = 0;
    private float scale = 1.0f;
    private float lastTouchX, lastTouchY;
    private ScaleGestureDetector scaleDetector;

    private int nodeRadius = 80;
    private int horizontalSpacing = 400;
    private int verticalSpacing = 250;

    public FlowChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(6f);

        // Handle pinch zoom
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale *= detector.getScaleFactor();
                scale = Math.max(0.3f, Math.min(scale, 3.0f)); // clamp zoom
                invalidate();
                return true;
            }
        });
    }

    public void setNodes(List<FlowNode> nodes) {
        this.nodes = Objects.requireNonNullElse(nodes, java.util.Collections.emptyList());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (nodes == null || nodes.isEmpty()) return;

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        // Draw edges first
        for (FlowNode node : nodes) {
            if (node.getChildren() == null) continue;
            for (int childId : node.getChildren()) {
                FlowNode child = findNodeById(childId);
                if (child != null) {
                    float startX = node.getX() * horizontalSpacing;
                    float startY = node.getY() * verticalSpacing;
                    float endX = child.getX() * horizontalSpacing;
                    float endY = child.getY() * verticalSpacing;
                    canvas.drawLine(startX, startY, endX, endY, linePaint);
                }
            }
        }

        // Draw nodes
        for (FlowNode node : nodes) {
            float cx = node.getX() * horizontalSpacing;
            float cy = node.getY() * verticalSpacing;

            // Color based on state
            if (node.isEnding()) {
                nodePaint.setColor(Color.parseColor("#FFD700")); // gold for endings
            } else if (node.isVisited()) {
                nodePaint.setColor(Color.parseColor("#8A2BE2")); // purple for visited
            } else {
                nodePaint.setColor(Color.GRAY);
            }

            canvas.drawCircle(cx, cy, nodeRadius, nodePaint);
            canvas.drawText(node.getLabel(), cx, cy + 12, textPaint);
        }

        canvas.restore();
    }

    private FlowNode findNodeById(int id) {
        for (FlowNode node : nodes) {
            if (node.getId() == id) return node;
        }
        return null;
    }

    // Handle panning & zooming
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress()) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    offsetX += dx;
                    offsetY += dy;
                    invalidate();
                }
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                return true;
        }
        return true;
    }
}
