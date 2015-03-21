package tk.zielony.gravity.effectview;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class EffectView extends GLSurfaceView {

	EffectRenderer renderer;

	private void init() {
		setEGLContextClientVersion(2);
		renderer = new EffectRenderer();
		renderer.effectView = this;
		setRenderer(renderer);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void onPause() {
		renderer.onPause();
		super.onPause();
	}

	public EffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EffectView(Context context) {
		super(context);
		init();
	}

	public EffectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
	}

	public void setImageBitmap(Bitmap image) {
		renderer.setImage(image);
	}
	
	public void draw(){
		//renderer.setImage(image);
	}

	public void setEffect(Effect t) {
		renderer.setEffect(t);
	}

	public void setOnEffectCompletedListener(OnEffectCompletedListener listener) {
		renderer.setOnEffectCompleted(listener);
	}

}
