package com.synchro.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.synchro.R;
import com.synchro.ble.CalculateMisses;
import com.synchro.model.DotsModel;

import java.util.ArrayList;

import static com.synchro.activity.GlobalApplication.isMisses;
import static com.synchro.utils.AppConstants.Canvas_pixels_X;
import static com.synchro.utils.AppConstants.Canvas_pixels_Y;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;
import static com.synchro.utils.AppConstants.offset_x;
import static com.synchro.utils.AppConstants.offset_y;
import static com.synchro.utils.AppConstants.real_life_X;
import static com.synchro.utils.AppConstants.real_life_Y;
import static com.synchro.utils.DrawableDotImageView.convertedX;
import static com.synchro.utils.DrawableDotImageView.convertedY;
import static com.synchro.utils.DrawableDotImageView.drawPentagon;
import static com.synchro.utils.DrawableDotImageView.drawStar;
import static com.synchro.utils.DrawableDotImageView.setCanvasWidthHeight;

public class DrawableDotImageViewZeroing extends androidx.appcompat.widget.AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener {
    private ArrayList<Dot> dots = new ArrayList<>();
    private Paint dotPaint;
    private Paint avgDotPaint;
    private Paint trianglePaint;
    private DotsModel averagePosition = null;
    private DotsModel trianglePosition = null;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private static final String TAG = "ZoomLayout";
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private Mode mode = Mode.NONE;
    private float scale = 1.0f;
    private float lastScaleFactor = 1.0f;

    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;
    private Context context;


    public DrawableDotImageViewZeroing(@NonNull Context context) {
        super(context);
        this.context = context;
        setup();
    }

    public DrawableDotImageViewZeroing(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    public DrawableDotImageViewZeroing(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setup();
    }

    public void addDots(float x, float y) {
        float radius= SharedPref.getValue(context, SharedPref.MARKER_SIZE, 4);

        dots.add(new Dot(x, y, radius));
      /*  isMisses = false;
        for (int i = 0; i < dots.size(); i++) {
            if (CalculateMisses.check_miss(convertedX(dots.get(i).x), convertedY(dots.get(i).y))){
                isMisses = true;
            }
        }
        Log.d("ismiss==>",isMisses+"");*/
        invalidate();
    }

    public void removeDots() {
        dots = new ArrayList<>();
        averagePosition = null;
        trianglePosition = null;
        invalidate();
    }

    public void addAverageDot(DotsModel averagePosition, float goalx, float goaly) {
        this.averagePosition = averagePosition;
        this.trianglePosition = new DotsModel(goalx, goaly);
        invalidate();
    }


    private void setup() {
        dotPaint = new Paint();
        dotPaint.setColor(AppMethods.getMarkerColor(context,SharedPref.getValue(context, SharedPref.MARKER_COLOR, context.getResources().getString(R.string.red))));


        avgDotPaint = new Paint();
        avgDotPaint.setColor(getResources().getColor(R.color.dot_average_color));

        trianglePaint = new Paint();
        trianglePaint.setColor(getResources().getColor(R.color.dark_green));

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "DOWN");
                        if (scale > MIN_ZOOM) {
                            mode = Mode.DRAG;
                            startX = motionEvent.getX() - prevDx;
                            startY = motionEvent.getY() - prevDy;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == Mode.DRAG) {
                            dx = motionEvent.getX() - startX;
                            dy = motionEvent.getY() - startY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = Mode.ZOOM;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = Mode.DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "UP");
                        mode = Mode.NONE;
                        prevDx = dx;
                        prevDy = dy;
                        break;
                }
                scaleDetector.onTouchEvent(motionEvent);

                if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float maxDx = (getWidth() - (getWidth() / scale)) / 2 * scale;
                    float maxDy = (getHeight() - (getHeight() / scale)) / 2 * scale;
                    dx = Math.min(Math.max(dx, -maxDx), maxDx);
                    dy = Math.min(Math.max(dy, -maxDy), maxDy);
                    Log.i(TAG, "Width: " + getWidth() + ", scale " + scale + ", dx " + dx
                            + ", max " + maxDx);
                    applyScaleAndTranslation();
                }

                return true;
            }
        });

    }

    private void applyScaleAndTranslation() {
        setScaleX(scale);
        setScaleY(scale);
        setTranslationX(dx);
        setTranslationY(dy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int markerShape=AppMethods.getMarkerShape(context,SharedPref.getValue(context, SharedPref.MARKER_SHAPE, context.getResources().getString(R.string.circle)));

        for (Dot dot : dots) {
            if(markerShape==1)
            {
                canvas.drawCircle(convertedX(dot.getX()), convertedY(dot.getY()), dot.getRadius(), dotPaint);

            }
            if(markerShape==2)
            {
                canvas.drawRect(convertedX(dot.getX()),convertedY(dot.getY()), convertedX(dot.getX()) - dot.getRadius()-3 , convertedY(dot.getY()) - dot.getRadius()-3 , dotPaint);
            }
            if(markerShape==3)
            {
                drawPentagon(canvas,dotPaint,convertedX(dot.getX()), convertedY(dot.getY()),(int)dot.getRadius(),(int)dot.getRadius());
            }
            if(markerShape==4)
            {
                RectF rect = new RectF(convertedX(dot.getX()),convertedY(dot.getY()),convertedX(dot.getX()) - dot.getRadius()-5 , convertedY(dot.getY()) - dot.getRadius()-5 );

                canvas.drawOval(rect,dotPaint);

            }
            if(markerShape==5)
            {
                drawStar(convertedX(dot.getX()),convertedY(dot.getY()),(int)dot.getRadius()+10,canvas);
            }
        }

       /* if (averagePosition != null) {
            canvas.drawCircle(convertedX(averagePosition.x, canvas), convertedY(averagePosition.y, canvas), 10, avgDotPaint);
        }*/
        if (averagePosition != null) {
            float cx = convertedX(averagePosition.x);
            float cy = convertedY(averagePosition.y);
//            canvas.drawRect(cx-10, cy-10, cx+10, cy+10, avgDotPaint);
            drawRhombus(canvas, avgDotPaint, cx, cy, 20);
        }

        if (trianglePosition != null) {
            int trix = Math.round(convertedX(trianglePosition.x/100 ));
            int triy = Math.round(convertedY(trianglePosition.y/100 ));
            Log.d("triangle==>", trianglePosition.x+","+trianglePosition.y+"   ("+trix+","+triy+")");
            drawTriangle( trix, triy, 10, 10, false, trianglePaint, canvas);
        }
    }

    public void drawRhombus(Canvas canvas, Paint paint, float x, float y, int width) {
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x, y + width - 7); // Top
        path.lineTo(x - halfWidth, y); // Left
        path.lineTo(x, y - width + 7); // Bottom
        path.lineTo(x + halfWidth, y); // Right
        path.lineTo(x, y + width - 7); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawTriangle(int x, int y, int width, int height, boolean inverted, Paint paint, Canvas canvas) {
        x = x - 5;
        y = y + 3;
        Point p1 = new Point(x, y);
        int pointX = x + width / 2;
        int pointY = inverted ? y + height : y - height;

        Point p2 = new Point(pointX, pointY);
        Point p3 = new Point(x + width, y);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    /*private float convertedX(float x, Canvas canvas) {
        float Scale_x = (Image_pixels_X) / (real_life_X);
        float x_center = (Image_pixels_X) / 2 - offset_x * Scale_x;
        float x_plotted = x_center + x * Scale_x;
        return x_plotted;
    }

    private float convertedY(float y, Canvas canvas) {
        float Scale_y = (Image_pixels_Y) / (real_life_Y);
        float y_center = (Image_pixels_Y) / 2 + offset_y * Scale_y;
        float y_plotted = y_center - y * Scale_y;
        return y_plotted;
    }*/

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scale = 1 - scaleGestureDetector.getScaleFactor();
        float prevScale = lastScaleFactor;
        lastScaleFactor += scale;

        // we can maximise our focus to 10f only
        if (lastScaleFactor > 10f)
            lastScaleFactor = 10f;

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / lastScaleFactor, 1f / prevScale, 1f / lastScaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());

        // duration of animation will be 0.It will
        // not change by self after that
        scaleAnimation.setDuration(0);
        scaleAnimation.setFillAfter(true);
        // we are setting it as animation
        startAnimation(scaleAnimation);

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private static class Dot {
        private float x;
        private float y;
        private final float radius;

        public Dot(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getRadius() {
            return radius;
        }

        public boolean isInside(float x, float y) {
            return (getX() - x) * (getX() - x) + (getY() - y) * (getY() - y) <= radius * radius;
        }
    }
}
