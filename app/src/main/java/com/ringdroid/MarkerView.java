/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ringdroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Represents a draggable start or end marker.
 * <p>
 * Most events are passed back to the client class using a
 * listener interface.
 * <p>
 * This class directly keeps track of its own velocity, though,
 * accelerating as the user holds down the left or right arrows
 * while this control is focused.
 * 表示可拖动的开始或结束标记。
 * <p>
 * 大多数事件都使用传递给客户端类
 * 侦听器界面。
 * <p>
 * 虽然此类直接跟踪自己的速度，
 * 随着用户按住向左或向右箭头而加速
 * 当此控件为重点控件时。
 */
public class MarkerView extends ImageView {

    public interface MarkerListener {
        public void markerTouchStart(MarkerView marker, float pos);

        public void markerTouchMove(MarkerView marker, float pos);

        public void markerTouchEnd(MarkerView marker);

        public void markerFocus(MarkerView marker);

        public void markerLeft(MarkerView marker, int velocity);

        public void markerRight(MarkerView marker, int velocity);

        public void markerEnter(MarkerView marker);

        public void markerKeyUp();

        public void markerDraw();
    }

    ;

    private int mVelocity;
    private MarkerListener mListener;

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Make sure we get keys
        // 确保我们得到焦点
        setFocusable(true);

        mVelocity = 0;
        mListener = null;
    }

    public void setListener(MarkerListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                requestFocus();
                // We use raw x because this window itself is going to
                // move, which will screw up the "local" coordinates
                // 我们使用原始x，因为此窗口本身将要移动，这将弄乱“本地”坐标
                mListener.markerTouchStart(this, event.getRawX());
                break;
            case MotionEvent.ACTION_MOVE:
                // We use raw x because this window itself is going to
                // move, which will screw up the "local" coordinates
                // 我们使用原始x，因为此窗口本身将要移动，这将弄乱“本地”坐标
                mListener.markerTouchMove(this, event.getRawX());
                break;
            case MotionEvent.ACTION_UP:
                mListener.markerTouchEnd(this);
                break;
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        if (gainFocus && mListener != null)
            mListener.markerFocus(this);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mListener != null)
            mListener.markerDraw();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mVelocity++;
        int v = (int) Math.sqrt(1 + mVelocity / 2);
        if (mListener != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mListener.markerLeft(this, v);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mListener.markerRight(this, v);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                mListener.markerEnter(this);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mVelocity = 0;
        if (mListener != null)
            mListener.markerKeyUp();
        return super.onKeyDown(keyCode, event);
    }
}
