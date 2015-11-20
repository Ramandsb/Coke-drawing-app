package com.example.abner.stickerdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chetan on 10/7/2015.
 */
public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private ArrayList<Integer> paints = new ArrayList<Integer>();


    //initial color
    private int paintColor = 0xFF660000, paintAlpha = 255;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //brush sizes
    private float brushSize, lastBrushSize;
    //erase flag
    private boolean erase = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        Log.d("parsedColor","Calling Setiup");
    }

    //setup drawing
    private void setupDrawing() {

        //prepare for drawing and setup paint stroke properties
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
       // canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        Log.d("ARRAY", "NO OF PAINTS: "+paints.size()+""+paints.toString());
        int i=0;
        for (Path p : paths){
       //   canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

            drawPaint.setColor(paints.get(i));
            canvas.drawPath(p, drawPaint);
            i++;
        }
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();


//        drawPath.reset();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);


                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("changedval", paintColor+"");
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                paths.add(drawPath);
                paints.add(paintColor);
                Log.d("my color",drawPaint.getColor()+"");
                drawPath = new Path();
                undonePaths.clear();
                drawPath.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;

    }

    //update color
    public void setColor(String newColor) {

        //check whether color value or pattern name

        drawPath.reset();
        if (newColor.startsWith("#")) {
            invalidate();
            paintColor = Color.parseColor(newColor);
            Log.d("parsedcolor",paintColor+"");

//            paints.add(paintColor);
            drawPaint.setColor(paintColor);
            drawPaint.setShader(null);
        } else {
            invalidate();
            //pattern
            int patternID = getResources().getIdentifier(
                    newColor, "drawable", "com.example.abner.stickerdemo");
            //decode
            Bitmap patternBMP = BitmapFactory.decodeResource(getResources(), patternID);
            //create shader
            BitmapShader patternBMPshader = new BitmapShader(patternBMP,
                    Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            //color and shader
            drawPaint.setColor(0xFFFFFFFF);
            drawPaint.setShader(patternBMPshader);

        }

    }

    //set brush size
    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    //get and set last brush size
    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    //set erase true or false
    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    //start new drawing
    public void startNew() {
        paths.clear();
        undonePaths.clear();
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //return current alpha
    public int getPaintAlpha() {
        return Math.round((float) paintAlpha / 255 * 100);
    }

    //set alpha
    public void setPaintAlpha(int newAlpha) {
        paintAlpha = Math.round((float) newAlpha / 100 * 255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public void redo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {

        }

    }

    public void undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            paints.remove(paths.size()-1);
            invalidate();
        } else {

        }

    }
}