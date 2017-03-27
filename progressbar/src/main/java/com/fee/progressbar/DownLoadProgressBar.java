package com.fee.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

/**
 * =========================
 * <br/>Created by fee on 2017/3/24 17:31
 * <br/>QQ：752115651
 * <br/>Version：1.0
 * <br/>description:
 * <br/>
 * =========================
 */

public class DownLoadProgressBar extends View implements View.OnClickListener {
    public static final String TAG = DownLoadProgressBar.class.getSimpleName();
    public static final int VISIBLE = 0;
//    public static final int INVIEWSIBLE = 1;

    /**
     * 初始状态
     */
    public static final int STATUS_INIT = 0;
    /**
     * 正在下载中
     */
    public static final int STATUS_DOWNLOADING = 1;
    /**
     * 下载完成
     */
    public static final int STATUS_DOWNLOAD_COMPLETE = 2;
    /**
     * 暂停下载
     */
    public static final int STATUS_STOP = 3;
    /**
     * 打开下载文件
     */
    public static final int STATUS_OPEN = 4;

    private final int change_text_color = Color.WHITE;

    private final int default_change_color = Color.rgb(25, 140, 236);

    //            private final int default_origin_color = 0xcccccc;
    private final int default_origin_color = Color.rgb(239, 239, 239);
//    private final int default_origin_color = Color.GRAY;
    /**
     * 默认文字大小 24sp
     */
    private final int default_text_size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            24, getResources().getDisplayMetrics());
    /**
     * 默认中间矩形的高度 80dp
     */
    private final int default_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            80, getResources().getDisplayMetrics());
    /**
     * 默认中间矩形的宽度 100dp
     */
    private final int default_wight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            100, getResources().getDisplayMetrics());
    /**
     * 最大的进度 100
     */
    private int mMaxProgress = 100;
    /**
     * 当前进度
     */
    private int mCurrentProgress = 0;
    /**
     * 正在改变的进度条颜色
     */
    private int mChangeColor;
    /**
     * 默认颜色
     */
    private int mOriginColor;
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 文字大小
     */
    private float mTextSize;
    /**
     * 中间绘制的文字
     */
    private String mText;
    /**
     * 是否显示文字
     */
    private boolean isTextShow;
    /**
     * 绘制文字的画笔
     */
    private Paint mTextPaint;
    /**
     * 进度条画笔
     */
    private Paint mProgressbarPaint;

//    private Paint mLayerUpPaint;

    private RectF mRectF;
    private Rect mRect;
    private int mTotalHeight;
    private int mRadius;
    private int mCurrentStatus;
    private Context mContext;
    private boolean isDownloading;
    private OnProgressListener mOnProgressListener;

    public DownLoadProgressBar(Context context) {
        this(context, null);
    }

    public DownLoadProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownLoadProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.DownLoadProgressBar, defStyleAttr, 0);

        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.DownLoadProgressBar_progress_max) {
                mMaxProgress = a.getInteger(attr, mMaxProgress);
            } else if (attr == R.styleable.DownLoadProgressBar_progress_reached_color) {
                mChangeColor = a.getColor(attr, default_change_color);
            } else if (attr == R.styleable.DownLoadProgressBar_progress_unreached_color) {
                mOriginColor = a.getColor(attr, default_origin_color);
            } else if (attr == R.styleable.DownLoadProgressBar_progress_text_color) {
                mTextColor = a.getColor(attr, default_change_color);
            } else if (attr == R.styleable.DownLoadProgressBar_progress_text_size) {
                mTextSize = a.getDimensionPixelSize(attr, default_text_size);
            } else if (attr == R.styleable.DownLoadProgressBar_progress_text_visibility) {
                isTextShow = a.getInteger(attr, VISIBLE) == VISIBLE;
            } else if (attr == R.styleable.DownLoadProgressBar_progress_current) {
                int p = a.getInteger(attr, mCurrentProgress);
                if (p <= 0) {
                    mCurrentStatus = 0;
                } else if (p < mMaxProgress) {
                    mCurrentProgress = p;
                } else {
                    mCurrentProgress = mMaxProgress;
                }
            }
        }
        a.recycle();
        initPaint();

        mCurrentStatus = STATUS_INIT;
        mCurrentProgress = 0;


        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCurrentStatus == STATUS_DOWNLOAD_COMPLETE) {
            // TODO: 2017/3/27 实现下载完成之后的功能
            Toast.makeText(mContext, "点击了控件", Toast.LENGTH_SHORT).show();
        } else {
            if (isDownloading) {
                pause();
            } else {
                start();
            }
        }
        Log.d(TAG, "onClick: "+mCurrentStatus);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mChangeColor = default_change_color;
        mOriginColor = default_origin_color;
        mTextColor = default_change_color;
        mTextSize = default_text_size;
        //默认显示文字
        isTextShow = true;

        //初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setFakeBoldText(true);
        //初始化一个矩形 用于测量文字宽度和高度
        mRect = new Rect();


        mProgressbarPaint = new Paint();
        mProgressbarPaint.setColor(mOriginColor);
        mProgressbarPaint.setStrokeWidth((float) mTotalHeight);
        mProgressbarPaint.setStyle(Paint.Style.FILL);
        mProgressbarPaint.setAntiAlias(true);

        mRectF = new RectF();
        mText = mContext.getString(R.string.download);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = default_wight + default_height;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mTotalHeight = heightSize;
        } else {
            mTotalHeight = default_height;
        }
        mRadius = mTotalHeight / 2;


        mRectF.left = getPaddingLeft();
        mRectF.top = getPaddingTop();
        mRectF.right = width - getPaddingRight();
        mRectF.bottom = mTotalHeight - getPaddingBottom();

        setMeasuredDimension(width, mTotalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentStatus == STATUS_DOWNLOADING || mCurrentStatus == STATUS_STOP) {
            int currentWidth = (int) (((float) mCurrentProgress / (float) mMaxProgress) * getWidth());
            drawProgressbar(canvas, 0, currentWidth, mChangeColor, mProgressbarPaint);
            drawProgressbar(canvas, currentWidth, getWidth(), mOriginColor, mProgressbarPaint);
        } else if (mCurrentStatus == STATUS_INIT) {
            drawProgressbar(canvas, 0, getWidth(), mOriginColor, mProgressbarPaint);
        } else {
            drawProgressbar(canvas, 0, getWidth(), mChangeColor, mProgressbarPaint);
        }
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        //设置不显示文字
        if (!isTextShow) return;
        switch (mCurrentStatus) {
            case STATUS_DOWNLOAD_COMPLETE://下载完成状态
                mText = mContext.getString(R.string.open);
                mTextPaint.setColor(Color.WHITE);
                break;
            case STATUS_DOWNLOADING://正在下载中
                mText = String.format(mContext.getString(R.string.downloading),
                        mCurrentProgress * 100 / mMaxProgress);
                break;
            case STATUS_OPEN://打开
                mText = mContext.getString(R.string.open);
                mTextPaint.setColor(Color.WHITE);
                break;
            case STATUS_STOP://暂停下载
                mText = String.format(mContext.getString(R.string.stop),
                        mCurrentProgress * 100 / mMaxProgress);
                break;
            case STATUS_INIT://初始状态
            default:
                mText = mContext.getString(R.string.download);
                mTextPaint.setColor(mChangeColor);
                break;
        }
        mTextPaint.getTextBounds(mText, 0, mText.length(), mRect);
        drawChangeText(canvas);
        drawOriginText(canvas);
    }

    private void drawChangeText(Canvas canvas) {
        int endX = (int) (((float) mCurrentProgress / (float) mMaxProgress) * getWidth());
        drawText(canvas, change_text_color, getWidth() / 2 - mRect.width() / 2, endX, mText, mTextPaint);
    }

    private void drawOriginText(Canvas canvas) {
        int startX = (int) (((float) mCurrentProgress / (float) mMaxProgress) * getWidth());
        int endX = getWidth() / 2 + mRect.width() / 2;
        drawText(canvas, default_change_color, startX, endX, mText, mTextPaint);
    }

    private void drawText(Canvas canvas, int textColor, int startX, int endX, String text, Paint paint) {

        paint.setColor(textColor);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());
        canvas.drawText(text, getWidth() / 2 - mRect.width() / 2,
                getHeight() / 2 + mRect.height() / 2, paint);
        canvas.restore();

    }

    private void drawProgressbar(Canvas canvas, int startX, int endX, int color, Paint paint) {
        paint.setColor(color);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());
        canvas.drawRoundRect(mRectF, mRadius, mRadius, paint);
        canvas.restore();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };

    public void start() {
        isDownloading = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isDownloading) {
                    mCurrentProgress++;
                    mHandler.sendEmptyMessage(0);
                    if (mCurrentProgress <= 0) {
                        mCurrentStatus = STATUS_INIT;
                    } else if (mCurrentProgress < mMaxProgress) {
                        mCurrentStatus = STATUS_DOWNLOADING;
                    } else {
                        mCurrentStatus = STATUS_DOWNLOAD_COMPLETE;
                        isDownloading = false;
                        return;
                    }
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void install() {


    }

    public void open() {

    }

    public void pause() {
        isDownloading = false;
        mCurrentStatus = STATUS_STOP;
        invalidate();
    }

    public void reset() {
        isDownloading = false;
        mCurrentProgress = 0;
        mCurrentStatus = STATUS_INIT;
        invalidate();
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }



    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentStatus==STATUS_INIT)


                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }*/


}
