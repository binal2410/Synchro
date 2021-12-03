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
import com.synchro.activity.GlobalApplication;

import java.util.ArrayList;

import static com.synchro.utils.AppConstants.Canvas_AR;
import static com.synchro.utils.AppConstants.Canvas_pixels_X;
import static com.synchro.utils.AppConstants.Canvas_pixels_Y;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;
import static com.synchro.utils.AppConstants.image_AR;
import static com.synchro.utils.AppConstants.offset_x;
import static com.synchro.utils.AppConstants.offset_y;
import static com.synchro.utils.AppConstants.real_life_X;
import static com.synchro.utils.AppConstants.real_life_Y;

public class DrawableDotImageViewReaction extends androidx.appcompat.widget.AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener {
    private ArrayList<DrawableDotImageViewReaction.Dot> dots = new ArrayList<>();
    private static Paint dotPaint;


    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private static final String TAG = "ZoomLayout";
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private DrawableDotImageViewReaction.Mode mode = DrawableDotImageViewReaction.Mode.NONE;
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
    private int currentRound=1;

public void setCurrentRound(int currentRound)
{
    this.currentRound=currentRound;
}
    public DrawableDotImageViewReaction(@NonNull Context context) {
        super(context);
        this.context = context;
        setup();
    }

    public DrawableDotImageViewReaction(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    public DrawableDotImageViewReaction(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setup();
    }

    public void addDots(float x, float y) {
    Log.e("current Round>>",currentRound+"");
        if(currentRound==1)
        {
            dotPaint.setColor(context.getResources().getColor(R.color.dot_color_red));

        }
        if(currentRound==2)
        {
            dotPaint.setColor(context.getResources().getColor(R.color.dot_color_yellow));

        }
        float radius= SharedPref.getValue(context, SharedPref.MARKER_SIZE, 4);
        dots.add(new DrawableDotImageViewReaction.Dot(x, y, radius));
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
        invalidate();
    }

    private void setup() {
        dotPaint = new Paint();

        dotPaint.setColor(AppMethods.getMarkerColor(context,SharedPref.getValue(context, SharedPref.MARKER_COLOR, context.getResources().getString(R.string.red))));

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "DOWN");
                        if (scale > MIN_ZOOM) {
                            mode = DrawableDotImageViewReaction.Mode.DRAG;
                            startX = motionEvent.getX() - prevDx;
                            startY = motionEvent.getY() - prevDy;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DrawableDotImageViewReaction.Mode.DRAG) {
                            dx = motionEvent.getX() - startX;
                            dy = motionEvent.getY() - startY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = DrawableDotImageViewReaction.Mode.ZOOM;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = DrawableDotImageViewReaction.Mode.DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "UP");
                        mode = DrawableDotImageViewReaction.Mode.NONE;
                        prevDx = dx;
                        prevDy = dy;
                        break;
                }
                scaleDetector.onTouchEvent(motionEvent);

                if ((mode == DrawableDotImageViewReaction.Mode.DRAG && scale >= MIN_ZOOM) || mode == DrawableDotImageViewReaction.Mode.ZOOM) {
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
       if(currentRound==1)
       {
           dotPaint.setColor(context.getResources().getColor(R.color.dot_color_red));

       }
        if(currentRound==2)
        {
            dotPaint.setColor(context.getResources().getColor(R.color.dot_color_yellow));

        }
        for (DrawableDotImageViewReaction.Dot dot : dots) {
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
    }
    public static void drawPentagon(Canvas canvas, Paint paint, float x, float y,int width,int height) {

        // vertices
      /*  float p1x = 10.f + x+width;
        float p1y = 0.f + y+height;
        float p2x = 0.f + x+width;
        float p2y = -8.f + y+height;
        float p3x = -10.f + x+width;
        float p3y = 0.f + y+height;
        float p4x = -8.f + x+width;
        float p4y = 8f + y+height;
        float p5x = 8.f + x+width;
        float p5y = 8.f + y+height;*/

        float p1x =  width+ x+width;
        float p1y = 0.f + y+height;
        float p2x = 0.f + x+width;
        float p2y = -(width) + y+height;
        float p3x = -width + x+width;
        float p3y = 0.f + y+height;
        float p4x = -width + x+width;
        float p4y = width + y+height;
        float p5x = width + x+width;
        float p5y = width + y+height;


        Path path = new Path();
        path.moveTo(p1x, p1y);
        path.lineTo(p2x, p2y);
        path.lineTo(p3x, p3y);
        path.lineTo(p4x, p4y);
        path.lineTo(p5x, p5y);
        path.lineTo(p1x, p1y);

        canvas.drawPath(path, paint);

      /*  x = x-5;
        y = y + 3;
        Point p1 = new Point((int)x, (int)y);
        int pointX =(int) x + width / 2;
        int pointY =(int) y + height;

        Point p2 = new Point(pointX, pointY);
        Point p3 = new Point((int)x, (int)y+height);
        Point p4 = new Point((int)x, (int)y+height+5);
        Point p5 = new Point((int)x+width, (int)y+height+5);

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.lineTo(p4.x, p4.y);
        path.lineTo(p5.x, p5.y);
        path.lineTo(p1.x, p1.y);

        canvas.drawPath(path, paint);*/
    }
    public static void drawStar(float posX, float posY, int size, Canvas canvas){

        int hMargin = size/9;
        int vMargin = size/4;

        Point a = new Point((int) (posX + size/2), (int) posY);
        Point b = new Point((int) (posX + size/2 + hMargin), (int) (posY + vMargin));
        Point c = new Point((int) (posX + size), (int) (posY + vMargin));
        Point d = new Point((int) (posX + size/2 + 2*hMargin), (int) (posY + size/2 + vMargin/2));
        Point e = new Point((int) (posX + size/2 + 3*hMargin), (int) (posY + size));
        Point f = new Point((int) (posX + size/2), (int) (posY + size - vMargin));
        Point g = new Point((int) (posX + size/2 - 3*hMargin), (int) (posY + size));
        Point h = new Point((int) (posX + size/2 - 2*hMargin), (int) (posY + size/2 + vMargin/2));
        Point i = new Point((int) posX, (int) (posY + vMargin));
        Point j = new Point((int) (posX + size/2 - hMargin), (int) (posY + vMargin));

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(e.x, e.y);
        path.lineTo(f.x, f.y);
        path.lineTo(f.x, f.y);
        path.lineTo(g.x, g.y);
        path.lineTo(h.x, h.y);
        path.lineTo(i.x, i.y);
        path.lineTo(j.x, j.y);
        path.lineTo(a.x, a.y);

        path.close();
        dotPaint.setColor(AppMethods.getMarkerColor(GlobalApplication.context,SharedPref.getValue(GlobalApplication.context, SharedPref.MARKER_COLOR, GlobalApplication.context.getResources().getString(R.string.red))));

        canvas.drawPath(path, dotPaint);
    }

    public static void setCanvasWidthHeight(float width, float height) {
        Canvas_pixels_X = width;
        Canvas_pixels_Y = height;
        Canvas_AR = Canvas_pixels_Y / Canvas_pixels_X;
        if (Canvas_AR > image_AR) {
            Image_pixels_X = Canvas_pixels_X;
            Image_pixels_Y = Canvas_pixels_X * image_AR;
        } else if (Canvas_AR < image_AR) {
            Image_pixels_X = Canvas_pixels_Y / image_AR;
            Image_pixels_Y = Canvas_pixels_Y;
        } else {
            Image_pixels_X = Canvas_pixels_X;
            Image_pixels_Y = Canvas_pixels_Y;
        }

        Log.d("canvas==>","Canvas_pixels_X: "+Canvas_pixels_X+", Canvas_pixels_Y: "+Canvas_pixels_Y+", Canvas_AR: "+Canvas_AR+", Image_pixels_X: "+Image_pixels_X+", Image_pixels_Y: "+Image_pixels_Y+", image_AR: "+image_AR);
    }

    public static float convertedX(float x) {

   /*     x = (float) (x * 100 * 37.79);
        x = (canvas.getWidth() * x) / AppConstants.image_dimen_x;
        return (canvas.getWidth() / 2) + x;*/
       /* if (isMisses && image_x_max!=0){
            float Scale_x = (image_x_max-image_x_min) / (real_life_X);
            float x_plotted = CalculateMisses.x_center + x * Scale_x;
            return x_plotted;
        }else {*/
        float Scale_x = (Image_pixels_X) / (real_life_X);
        float x_center = (Canvas_pixels_X) / 2 - offset_x * Scale_x;
        float x_plotted = x_center + x * Scale_x;
        return x_plotted;
//        }

    }

    public static float convertedY(float y) {
     /*   y = (float) (y * 100 * 37.79);
        y = (canvas.getHeight() * y) / AppConstants.image_dimen_y;
        return (canvas.getHeight() / 2) - y;*/
       /* if (isMisses && image_x_max!=0){
            float Scale_y = (image_y_max - image_y_min) / (real_life_Y);
            float y_plotted = y_center - y * Scale_y;
            return y_plotted;
        }else {*/
        float Scale_y = (Image_pixels_Y) / (real_life_Y);
        float y_center = (Canvas_pixels_Y) / 2 + offset_y * Scale_y;
        float y_plotted = y_center - y * Scale_y;
        return y_plotted;
//        }
    }

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