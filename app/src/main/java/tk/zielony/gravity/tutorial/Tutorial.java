package tk.zielony.gravity.tutorial;

import tk.zielony.gravity.R;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Tutorial extends FrameLayout {

	private TextView textView;
	protected TutorialLogic tutorialLogic;
	protected boolean running;
	int times = 3;

	public Tutorial(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Tutorial(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Tutorial(Context context) {
		super(context);
		init();
	}

	private void init() {
		View.inflate(getContext(), R.layout.tutorial, this);
		textView = (TextView) findViewById(R.id.text);
	}

	public void start() {
		Animation animation = AnimationUtils.loadAnimation(getContext(),
				R.anim.notification_in);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				running = true;
				if (tutorialLogic != null)
					tutorialLogic.onStart();
			}
		});
		this.startAnimation(animation);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (tutorialLogic != null && tutorialLogic.isFinished() && running) {
			times--;
			if (times == 0) {
				finish();
			} else {
				tutorialLogic.onStart();
				TextView more = (TextView) findViewById(R.id.more);
				more.setVisibility(View.VISIBLE);
				more.setText("" + times + " more to go");
			}
		}
	}

	public void finish() {
		running = false;
		Animation animation = AnimationUtils.loadAnimation(getContext(),
				R.anim.notification_out);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ViewGroup parent = (ViewGroup) getParent();
				parent.removeView(Tutorial.this);
				if (tutorialLogic != null)
					tutorialLogic.onFinished();
			}
		});
		this.startAnimation(animation);
	}

	public void setTutorialLogic(TutorialLogic logic) {
		tutorialLogic = logic;
	}

	public void setText(String string) {
		textView.setText(string);
	}

	public void setTapEnabled() {
		findViewById(R.id.next).setVisibility(View.VISIBLE);
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
