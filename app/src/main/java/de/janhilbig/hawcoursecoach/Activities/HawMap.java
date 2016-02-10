package de.janhilbig.hawcoursecoach.Activities;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

// import svg-parser library
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import org.altbeacon.beacon.Region;

import java.util.List;

import de.janhilbig.hawcoursecoach.R;
import de.janhilbig.hawcoursecoach.database.Room;


public class HawMap extends SurfaceView implements Runnable {

    // objects for thread handling
    private Thread thread;
    private SurfaceHolder holder;
    private boolean threadIsRunning;
    // Room Object for passing x-y values
    private Room myRoom;
    // Room coordinates on map
    private long x, y;
    // picture object for the map
    private Picture picture;
    // objects needed for zoom and panning
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float focusX;
    private float focusY;

    private float lastFocusX = -1;
    private float lastFocusY = -1;

    // paint objects
    Paint myPaint;

    // blink
    private static final int BLINK_DURATION = 700;
    private boolean blink;
    private long blinkStart, lastUpdateTime;

    // Constructor
    public HawMap(Context context) {
        super(context);
        holder = getHolder();
        thread = null;
        threadIsRunning = false;
        // parse svg
        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.haw_map_neu_dunkel);
        // get picture
        picture = new Picture();
        picture = svg.getPicture();
        // scale gesture detector object for pinch zoom and panning
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        // init paint
        myPaint = new Paint();
    }

    public void switchRooms(long room_id) {
        int id = (int) room_id;
        List<Room> rooms = Room.getRooms();
        myRoom = rooms.get(id);
        x = myRoom.getMap_x();
        y = myRoom.getMap_y();
    }

    public void blinkDot(Canvas canvas) {
        if (blink) {
            canvas.drawCircle(x, y, 17, myPaint);
        }
        updateBlink();
    }

    public void setMyColor(int select) {
        switch (select) {
            case 1: myPaint.setColor(Color.argb(170, 227, 6, 19)); //rot
                break;
            case 2: myPaint.setColor(Color.argb(170, 26, 212, 116)); //grün
                break;
            case 3: myPaint.setColor(Color.argb(195, 154, 169, 194)); //blau
                break;
        }
    }

    public void updateBlink() {
        if (System.currentTimeMillis() - lastUpdateTime >= BLINK_DURATION
                && !blink) {
            blink = true;
            blinkStart = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - blinkStart >= 500 && blink) {
            blink = false;
            lastUpdateTime = System.currentTimeMillis();
        }

    }

    // run thread
    @Override
    public void run() {
        while (threadIsRunning) {
            // perform canvas drawing
            if (!holder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            // save canvas
            canvas.save();
            // scale canvas
            canvas.scale(mScaleFactor, mScaleFactor, focusX, focusY);
            // pan canvas
            canvas.translate(mPosX, mPosY);
            // draw background
            canvas.drawRGB(0, 24, 73);
            // draw map
            canvas.drawPicture(picture);
            // draw Circle
            blinkDot(canvas);
            // unlock and post
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            lastFocusX = -1;
            lastFocusY = -1;

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            focusX = detector.getFocusX();
            focusY = detector.getFocusY();

            if (lastFocusX == -1)
                lastFocusX = focusX;
            if (lastFocusY == -1)
                lastFocusY = focusY;

            mPosX += (focusX - lastFocusX);
            mPosY += (focusY - lastFocusY);
            Log.v("Hi Zoom", "Factor:" + mScaleFactor);
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 5.0f));

            lastFocusX = focusX;
            lastFocusY = focusY;

            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = ev.getX() / mScaleFactor;
                final float y = ev.getY() / mScaleFactor;
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex) / mScaleFactor;
                final float y = ev.getY(pointerIndex) / mScaleFactor;

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {

                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    mPosX += dx;
                    mPosY += dy;
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = (ev.getAction() &    MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex) / mScaleFactor;
                    mLastTouchY = ev.getY(newPointerIndex) / mScaleFactor;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    public void pause() {
        threadIsRunning = false;
        while (true) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    public void resume() {
        threadIsRunning = true;
        thread = new Thread(this);
        thread.start();
    }

}
