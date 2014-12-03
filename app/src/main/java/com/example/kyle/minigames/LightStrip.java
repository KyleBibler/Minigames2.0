package com.example.kyle.minigames;

/**
 * Created by Kyle on 11/25/2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Alex on 11/9/2014.
 * Modeled strongly on LunarLander sample Android project
 */
public class LightStrip extends SurfaceView implements SurfaceHolder.Callback {
    class LightStripThread extends Thread {
        private Bitmap background;          // holds current screen to be drawn
        private int canvasHeight = 1;   // current height
        private int canvasWidth = 1;    // current width
        private LightModel lights;      // the light strip
        private final int numLights = 32;
        private final SurfaceHolder holder;   //
        private Context context;        //
        private boolean canRun;
        private BlockingQueue<LightModel> queue;
        private final int backgroundColor = Color.WHITE;

        public LightStripThread(SurfaceHolder holder, Context context, BlockingQueue<LightModel> queue) {
            this.holder = holder;
            this.context = context;
            this.queue = queue;

            background = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(background);
            c.drawColor(backgroundColor);
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (holder) {
                // fill with environment state
                canRun = true;
            }
        }

        @Override
        public void run() {
            while (canRun) {
                Canvas c = null;
                try {
                    c = holder.lockCanvas(null);
                    doDraw(c);
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
                try {
                    lights = queue.take();
                } catch (InterruptedException ex) {
                    Log.d("[LightStrip]", ex.getMessage());
                }
            }
        }

        public void doDraw(Canvas canvas) {
            // draw background, similar to clearing the screen
            canvas.drawColor(backgroundColor);

            // draw lights
            if (lights != null && lights.getLights().size() > 0) {
                int radius;
                float cx, cy;
                if(canvasWidth > canvasHeight) {
                    radius = canvasWidth / (2 * numLights);
                    cx = radius;
                    cy = canvasHeight / 2;
                } else {
                    radius = canvasHeight / (2 * numLights);
                    cy = radius;
                    cx = canvasWidth / 2;
                }
                // make paint
                Paint p = new Paint();
                p.setDither(true);
                p.setStyle(Paint.Style.FILL);
                // get first light and draw it
                Light l = lights.getLights().get(0);
                Light prevLight = l;
                int lightId = 0;
                int idOfLight = l.getId();
                boolean propagate = lights.isPropagate();
                boolean pastFirstIndex = false;
                for (int i = 1; i <= numLights; i++) {
                    if (i < idOfLight) {
                        if (propagate && pastFirstIndex)
                            p.setShader(new RadialGradient(cx, cy, radius, new int[] {
                                Color.argb(255, prevLight.getRed(), prevLight.getGreen(), prevLight.getBlue()),
                                Color.argb((int) (prevLight.getIntensity() * 255), prevLight.getRed(), prevLight.getGreen(), prevLight.getBlue()),
                                Color.WHITE},
                                    new float[] {0.0f, 0.4f, 0.4f + 0.6f *(float)(prevLight.getIntensity())}, Shader.TileMode.CLAMP));
                        else
                            p.setShader(new RadialGradient(cx, cy, radius, new int[] {
                                    Color.BLACK,
                                    Color.BLACK,
                                    backgroundColor},
                                    new float[] {0.0f, 0.4f, 0.4f}, Shader.TileMode.CLAMP));
                            p.setARGB(255, 0, 0, 0);
                    } else {
                        if (!pastFirstIndex) {
                            pastFirstIndex = true;
                        }
                        p.setShader(new RadialGradient(cx, cy, radius, new int[] {
                                Color.argb(255, l.getRed(), l.getGreen(), l.getBlue()),
                                Color.argb((int) (l.getIntensity() * 255), l.getRed(), l.getGreen(), l.getBlue()),
                                backgroundColor},
                                new float[] {0.0f, 0.4f, 0.4f + 0.6f *(float)(l.getIntensity())}, Shader.TileMode.CLAMP));

                        prevLight = l;
                        lightId++;
                        if (lightId >= lights.getLights().size()) {
                            idOfLight = 33;
                        } else {
                            l = lights.getLights().get((lightId));
                            idOfLight = l.getId();
                        }
                    }
                    canvas.drawCircle(cx, cy, radius, p);
                    if (canvasWidth > canvasHeight)
                        cx += 2 * radius;
                    else
                        cy += 2 * radius;
                }
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (holder) {
                canvasWidth = width;
                canvasHeight = height;

                // don't forget to resize the background image
                background = Bitmap.createScaledBitmap(
                        background, width, height, true);
            }
        }

        public void setRunning(boolean b) {
            canRun = b;
        }

        public void pause() {
            canRun = false;
        }
    }

    // thread responsible for actually drawing
    private LightStripThread thread = null;
    private boolean threadCreated = false;
    private Context context;

    public LightStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        // register for events about surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public LightStripThread getThread() {
        return thread;
    }

    public LightStripThread createThread(BlockingQueue<LightModel> queue) {
        // create drawing thread, started in surfaceCreated()
        if (!threadCreated && queue != null) {
            threadCreated = true;
            thread = new LightStripThread(getHolder(), context, queue);
            return thread;
        } else {
            return null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
        else {
            thread.setRunning(true);
        }
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        thread.queue.add(new LightModel(new ArrayList<Light>(), true));
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException ex) {
                Log.d("[LightStrip]", ex.getMessage());
            }
        }
    }
}
