package tk.zielony.gravity.effectview;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Effect {
    private static final String TAG = Effect.class.getSimpleName();
    private int program;
    private String pixelCode;
    private String vertexCode;

    private static String defaultVertexShaderCode = "attribute vec2 a_texCoord;\n"
            + "uniform mat4 u_contentTransform;\n" + "varying vec2 v_texCoord;\n" + "void main(){\n"
            + "v_texCoord = a_texCoord;\n" + "gl_Position = u_contentTransform * vec4(a_texCoord,0,1);\n" + "}";

    private static final String defaultPixelShaderCode = "precision mediump float;\n"
            + "uniform sampler2D s_texture;\n" + "varying vec2 v_texCoord;\n" + "void main(){\n"
            + "gl_FragColor = texture2D(s_texture, v_texCoord).bgra;\n" + "}";

    public Effect(Context context, String pixelFile) {
        loadFromAssets(context, null, pixelFile);
    }

    public Effect(Context context, String vertexFile, String pixelFile) {
        loadFromAssets(context, vertexFile, pixelFile);
    }

    public Effect(String pixelCode) {
        this.vertexCode = defaultVertexShaderCode;
        this.pixelCode = pixelCode != null ? pixelCode : defaultPixelShaderCode;
    }

    public Effect(String vertexCode, String pixelCode) {
        this.vertexCode = vertexCode != null ? vertexCode : defaultVertexShaderCode;
        this.pixelCode = pixelCode != null ? pixelCode : defaultPixelShaderCode;
    }

    public void loadFromAssets(Context context, String vertexAsset, String pixelAsset) {
        try {
            if (vertexAsset != null) {
                StringBuffer vs = new StringBuffer();
                InputStream inputStream = context.getAssets().open(vertexAsset);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                String read = in.readLine();
                while (read != null) {
                    vs.append(read + "\n");
                    read = in.readLine();
                }

                vs.deleteCharAt(vs.length() - 1);
                vertexCode = vs.toString();
            } else {
                vertexCode = defaultVertexShaderCode;
            }
            if (pixelAsset != null) {
                StringBuffer fs = new StringBuffer();
                InputStream inputStream = context.getAssets().open(pixelAsset);
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                String read = in.readLine();
                while (read != null) {
                    fs.append(read + "\n");
                    read = in.readLine();
                }

                fs.deleteCharAt(fs.length() - 1);
                pixelCode = fs.toString();
            } else {
                pixelCode = defaultPixelShaderCode;
            }
        } catch (Exception e) {
            Log.d("ERROR-readingShader", "Could not read shader: " + e.getLocalizedMessage());
        }
    }

    private void createEffect(String vertexSource, String fragmentSource) {
        int vertexShader = createShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int pixelShader = createShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
    }

    private int createShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public int getProgram() {
        return program;
    }

    public void load() {
        createEffect(vertexCode, pixelCode);
    }

}
