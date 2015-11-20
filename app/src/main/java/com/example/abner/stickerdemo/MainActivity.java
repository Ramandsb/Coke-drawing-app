package com.example.abner.stickerdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abner.stickerdemo.utils.FileUtils;
import com.example.abner.stickerdemo.view.StickerView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private StickerView mCurrentView;

    private ArrayList<View> mStickers;


    private RelativeLayout mContentRootView;
    String colorSel="111111";


    //custom drawing view
    private DrawingView drawView;
    //buttons
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, opacityBtn, redobut, undobut;
    //sizes
    private float extrasmallBrush,smallestbrush,smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentRootView = (RelativeLayout) findViewById(R.id.rl_content_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //get the palette and first color button
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //get drawing view
        drawView = (DrawingView) findViewById(R.id.drawing);
        if (mCurrentView!=null) {
            drawView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mCurrentView.setInEdit(false);
                    return false;
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addStickerView();
                colorPicker();
            }
        });
        mStickers = new ArrayList<>();
        //sizes from dimensions
        smallestbrush=getResources().getInteger(R.integer.smallest_brush);
        extrasmallBrush=getResources().getInteger(R.integer.extrasmall_size);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //draw button
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);


        //set initial size
        drawView.setBrushSize(mediumBrush);

        //erase button
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        //new button
        newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        //save button
        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        //opacity
        opacityBtn = (ImageButton) findViewById(R.id.opacity_btn);
        opacityBtn.setOnClickListener(this);

    }

    //user clicked paint
    public void paintClicked(View view) {
        //use chosen color
if (mCurrentView!=null) {

    mCurrentView.setInEdit(false);
}
        //set erase false
        drawView.setErase(false);
        drawView.setPaintAlpha(50);
        drawView.setBrushSize(drawView.getLastBrushSize());



        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            Log.d("mycolor",color);
            drawView.setColor(color);

            //update ui
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_complete) {
            if (mCurrentView!=null){
                mCurrentView.setInEdit(false);
            }

            generateBitmap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //添加表情
    private void addStickerView() {
        final StickerView stickerView = new StickerView(this);
        stickerView.setImageResource(R.mipmap.coke);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mStickers.remove(stickerView);
                mContentRootView.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {

                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);
            }

            @Override
            public void onTop(StickerView stickerView) {
                int position = mStickers.indexOf(stickerView);
                if (position == mStickers.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mStickers.remove(position);
                mStickers.add(mStickers.size(), stickerTemp);
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContentRootView.addView(stickerView, lp);
        mStickers.add(stickerView);
        setCurrentEdit(stickerView);
    }

    /**
     * 设置当前处于编辑模式的贴纸
     */
    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    private void generateBitmap() {
//        mCurrentView.setInEdit(true);

        Bitmap bitmap = Bitmap.createBitmap(mContentRootView.getWidth(),
                mContentRootView.getHeight()
                , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mContentRootView.draw(canvas);

        String iamgePath = FileUtils.saveBitmapToLocal(bitmap, this);
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra("image", iamgePath);
        startActivity(intent);
    }

    public void colorPicker(){
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .initialColor(R.color.white_alpha_95)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {


                        StringBuffer sb = new StringBuffer("#");

                        String joined = Integer.toHexString(selectedColor);

                        colorSel = sb.append(joined).toString();
                        Log.d("colorselected", colorSel);
                        drawView.setColor(colorSel);

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        Log.d("oksel", selectedColor + "");

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onClick(View view) {
        if (mCurrentView!=null) {

            mCurrentView.setInEdit(false);
        }


        if (view.getId() == R.id.draw_btn) {
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            //listen for clicks on size buttons
            ImageButton smallest = (ImageButton) brushDialog.findViewById(R.id.smallest_brush);
            smallest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(smallestbrush);
                    drawView.setLastBrushSize(smallestbrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton extrasmallBtn = (ImageButton) brushDialog.findViewById(R.id.extrasmall_brush);
            extrasmallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(extrasmallBrush);
                    drawView.setLastBrushSize(extrasmallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            //show and wait for user interaction
            brushDialog.show();
        } else if (view.getId() == R.id.erase_btn) {
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.eraser_chooser);
            //size buttons
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (view.getId() == R.id.new_btn) {
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if (view.getId() == R.id.save_btn) {
//            drawView.redo();
//            //save drawing
//            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
//            saveDialog.setTitle("Save drawing");
//            saveDialog.setMessage("Save drawing to device Gallery?");
//            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    //save drawing
//                    drawView.setDrawingCacheEnabled(true);
//                    //attempt to save
//                    String imgSaved = MediaStore.Images.Media.insertImage(
//                            getContentResolver(), drawView.getDrawingCache(),
//                            UUID.randomUUID().toString() + ".png", "drawing");
//                    //feedback
//                    if (imgSaved != null) {
//                        Toast savedToast = Toast.makeText(getApplicationContext(),
//                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
//                        savedToast.show();
//                    } else {
//                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
//                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
//                        unsavedToast.show();
//                    }
//                    drawView.destroyDrawingCache();
//                }
//            });
//            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            saveDialog.show();
        } else if (view.getId() == R.id.opacity_btn) {
            drawView.undo();
//            //launch opacity chooser
//            final Dialog seekDialog = new Dialog(this);
//            seekDialog.setTitle("Opacity level:");
//            seekDialog.setContentView(R.layout.opacity_chooser);
//            //get ui elements
//            final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
//            final SeekBar seekOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
//            //set max
//            seekOpq.setMax(100);
//            //show current level
//            int currLevel = drawView.getPaintAlpha();
//            seekTxt.setText(currLevel + "%");
//            seekOpq.setProgress(currLevel);
//            //update as user interacts
//            seekOpq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    seekTxt.setText(Integer.toString(progress) + "%");
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//
//            });
//            //listen for clicks on ok
//            Button opqBtn = (Button) seekDialog.findViewById(R.id.opq_ok);
//            opqBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    drawView.setPaintAlpha(seekOpq.getProgress());
//                    seekDialog.dismiss();
//                }
//            });
//            //show dialog
//            seekDialog.show();
        }



    }
}