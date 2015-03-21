/** 
 * The OpenGL renderer
 */

package tk.zielony.gravity.effectview;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class EffectRenderer implements GLSurfaceView.Renderer {
    public float mAngleX;
    public float mAngleY;

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;

    private Effect shader;

    // vertex information - clockwise
    final float _quadv[] = { 0, 0, 0, 1, 1, 1, 1, 0 };

    private FloatBuffer vertexBuffer;
    // index
    final int _quadi[] = { 0, 1, 2, 2, 3, 0 };
    private IntBuffer indexBuffer;

    // Modelview/Projection matrices
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mScaleMatrix = new float[16]; // scaling
    private final float[] mRotXMatrix = new float[16]; // rotation x
    private final float[] mRotYMatrix = new float[16]; // rotation x
    private final float[] mMMatrix = new float[16]; // rotation
    private final float[] mVMatrix = new float[16]; // modelview
    // angle rotation for light

    // RENDER TO TEXTURE VARIABLES
    int[] frameBuffer, depthBuffer, renderTex;

    IntBuffer texBuffer;
    // viewport variables
    float ratio = 1.0f;
    int w, h;

    private static String TAG = "Renderer";

    private OnEffectCompletedListener onEffectCompleted;

    private List<Runnable> initializers = Collections.synchronizedList(new ArrayList<Runnable>());

    /*
     * Draw function - called for every frame
     */
    public synchronized void onDrawFrame(final GL10 glUnused) {
    	effectView.draw();
    	
        while (!initializers.isEmpty()) {
            initializers.remove(0).run();
        }
        if (image != null && shader != null) {
            render();
        }
    }

    /*
     * Called when viewport is changed
     * 
     * @see
     * android.opengl.GLSurfaceView$Renderer#onSurfaceChanged(javax.microedition
     * .khronos.opengles.GL10, int, int)
     */
    public void onSurfaceChanged(final GL10 glUnused, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        w = width;
        h = height;
        ratio = (float) width / height;
        // Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);
        Matrix.orthoM(mProjMatrix, 0, -1, 1, -1, 1, 0.5f, 10);
    }

    /**
     * Initialization function
     */
    public void onSurfaceCreated(final GL10 glUnused, final EGLConfig config) {
        // initialize shaders

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // cull backface
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        // GLES20.glCullFace(GLES20.GL_BACK);

        // set the view matrix
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5.0f, 0.0f, 0f, 0f, 0f, -1.0f, 0.0f);

        // Setup quad
        // Generate your vertex, normal and index buffers
        // vertex buffer
        vertexBuffer = ByteBuffer.allocateDirect(_quadv.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(_quadv);
        vertexBuffer.position(0);

        // index buffer
        indexBuffer = ByteBuffer.allocateDirect(_quadi.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        indexBuffer.put(_quadi);
        indexBuffer.position(0);

    }

    int[] textures;
    private Bitmap image;
    private Bitmap resultBitmap;
    public EffectView effectView;
    private long start;
    private boolean renderToTexture = false;

    public EffectRenderer() {
        start = System.currentTimeMillis();
    }

    private void checkGlError(final String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void render() {
        // much bigger viewport?
        // Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);
        if (renderToTexture) {
            Matrix.orthoM(mProjMatrix, 0, 0, 1, 0, 1, 0.5f, 10);
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, 5.0f, 0.0f, 0f, 0f, 0f, 1.0f, 0.0f);
            GLES20.glViewport(0, 0, image.getWidth(), image.getHeight());
        } else {
            Matrix.orthoM(mProjMatrix, 0, 0, 1, -1, 0, 0.5f, 10);
            Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5.0f, 0.0f, 0f, 0f, 0f, -1.0f, 0.0f);
            GLES20.glViewport(0, 0, effectView.getWidth(), effectView.getHeight());
        }

        if (renderToTexture)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        // specify texture as color attachment
        if (renderToTexture)
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                    renderTex[0], 0);

        // attach render buffer as depth buffer
        if (renderToTexture)
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER,
                    depthBuffer[0]);

        // check status
        final int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            return;
        }

        GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // RENDER A FULL-SCREEN QUAD

        // use gouraud shader to render it?
        final int program = shader.getProgram();

        // Start using the shader
        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");

        // set the viewport
        // GLES20.glViewport(0, 0, w, h);
        // Matrix.orthoM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);

        // scaling
        Matrix.setIdentityM(mScaleMatrix, 0);
        // Matrix.scaleM(mScaleMatrix, 0, scaleX, scaleY, scaleZ);

        // Rotation along x
        Matrix.setRotateM(mRotXMatrix, 0, 0, -1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(mRotYMatrix, 0, 0, 0.0f, 1.0f, 0.0f);

        // Set the ModelViewProjectionMatrix
        final float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, mRotYMatrix, 0, mRotXMatrix, 0);
        Matrix.multiplyMM(mMMatrix, 0, mScaleMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        // send to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "u_contentTransform"), 1, false, mMVPMatrix, 0);

        // Vertex buffer

        // the vertex coordinates
        vertexBuffer.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(program, "a_texCoord"), 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(program, "a_texCoord"));

        // bind the framebuffer texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "s_texture"), 0);

        int sizeUniform = GLES20.glGetUniformLocation(program, "size");
        if (sizeUniform != -1)
            GLES20.glUniform2f(sizeUniform, (float) image.getWidth(), (float) image.getHeight());
        int timeUniform = GLES20.glGetUniformLocation(program, "time");
        if (timeUniform != -1)
            GLES20.glUniform1f(timeUniform, (System.currentTimeMillis() - start) / 1000.0f);

        // Draw with indices
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, _quadi.length, GLES20.GL_UNSIGNED_INT, indexBuffer); // NOTE:
        // On
        // some
        // devices
        // GL_UNSIGNED_SHORT
        // works

        if (renderToTexture)
            saveBufferToBitmap();

    }

    private void saveBufferToBitmap() {
        // if (resultBitmap != null)
        // resultBitmap.recycle();

        int[] array = new int[image.getWidth() * image.getHeight()];
        Buffer pixels = IntBuffer.wrap(array);
        pixels.flip();
        GLES20.glReadPixels(0, 0, image.getWidth(), image.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixels);
        if (resultBitmap == null || resultBitmap.getWidth() != image.getWidth()
                || resultBitmap.getHeight() != image.getHeight())
            resultBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Config.ARGB_8888);
        resultBitmap.setPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        if (onEffectCompleted != null)
            onEffectCompleted.onEffectCompleted(resultBitmap);

        Activity activity = (Activity) effectView.getContext();
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                effectView.setBackgroundDrawable(new BitmapDrawable(resultBitmap));
            }

        });
    }

    private void setupRenderToTexture() {
        frameBuffer = new int[1];
        depthBuffer = new int[1];
        renderTex = new int[1];

        // generate
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glGenRenderbuffers(1, depthBuffer, 0);
        GLES20.glGenTextures(1, renderTex, 0);

        // generate color texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);

        // parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        // create it
        // create an empty intbuffer first?
        final int[] buf = new int[image.getWidth() * image.getHeight()];
        texBuffer = ByteBuffer.allocateDirect(buf.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asIntBuffer();

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, image.getWidth(), image.getHeight(), 0,
                GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);

        // create render buffer and bind 16-bit depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, image.getWidth(),
                image.getHeight());
    }

    class ImageInitializer implements Runnable {
        public void run() {
            if (textures != null) {
                GLES20.glDeleteTextures(textures.length, textures, 0);
            }

            textures = new int[1];

            GLES20.glGenTextures(1, textures, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            // parameters
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);

            uninitRenderToTexture();
            setupRenderToTexture();
        }
    }

    public synchronized void setImage(final Bitmap image) {
        this.image = image;
        resultBitmap = null;

        if (image == null) {
            return;
        }

        initializers.add(new ImageInitializer());
    }

    public Effect getShader() {
        return shader;
    }

    public synchronized void setEffect(final Effect shader) {
        this.shader = shader;
        resultBitmap = null;

        if (shader != null) {
            initializers.add(new Runnable() {
                public void run() {
                    shader.load();
                }
            });
        }
    }

    public void onPause() {
        if (textures != null) {
            GLES20.glDeleteTextures(1, textures, 0);
            initializers.add(new ImageInitializer());
        }

        uninitRenderToTexture();

    }

    private void uninitRenderToTexture() {
        if (frameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
            GLES20.glDeleteRenderbuffers(1, depthBuffer, 0);
            GLES20.glDeleteTextures(1, renderTex, 0);

            texBuffer = null;
        }
    }

    public void setOnEffectCompleted(OnEffectCompletedListener onEffectCompleted) {
        this.onEffectCompleted = onEffectCompleted;
    }

    public boolean getRenderToTexture() {
        return renderToTexture;
    }

    public void setRenderToTexture(boolean renderToTexture) {
        this.renderToTexture = renderToTexture;
    }

}

// END CLASS