package tk.zielony.gravity;

import java.util.ArrayList;
import java.util.List;

import tk.zielony.gravity.effectview.Effect;
import tk.zielony.gravity.effectview.EffectView;
import tk.zielony.gravity.game.AchievementListener;
import tk.zielony.gravity.game.AchievementType;
import tk.zielony.gravity.game.GamePanel;
import tk.zielony.gravity.game.SpaceShip;
import tk.zielony.gravity.knob.KnobView;
import tk.zielony.gravity.knob.OnKnobStateChanged;
import tk.zielony.gravity.tutorial.LongpressTutorial;
import tk.zielony.gravity.tutorial.Tutorial;
import tk.zielony.gravity.tutorial.TutorialLogic;
import tk.zielony.gravity.tutorial.PinchTutorial;
import tk.zielony.gravity.tutorial.SwipeTutorial;
import tk.zielony.gravity.tutorial.TapTutorial;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class GameActivity extends Activity {
	GravityApplication application;
	//Spaceship spaceship;

	Achievement[] achievement = new Achievement[] {
			new Achievement(AchievementType.LOW_APM, "Sunday gamer",
					"keep apm below 5 after 1 minute"),
			new Achievement(AchievementType.HIGH_APM, "Micro master",
					"keep apm above 150 after 1 minute"),
			new Achievement(AchievementType.PLAY_ZOOMED_OUT,
					"Eyes on the world", "play zoomed out for 5 minutes"),
			new Achievement(AchievementType.PLAY_ZOOMED_IN,
					"Careful scientist", "play zoomed in for 5 minutes"),
			new Achievement(AchievementType.DESTROY_SPACESHIP,
					"It's not easy being green", "destroy a spaceship"),
			new Achievement(AchievementType.GOOD_GAME, "Good game",
					"play for 20 minutes"),
			new Achievement(AchievementType.CREATE_EARTH, "Sweet home earth",
					"create an earth"),
			new Achievement(AchievementType.CREATE_SUN, "Let's land at night",
					"create a sun"),
			new Achievement(AchievementType.SLOW_JAVA, "Knock, knock ...Java!",
					"go below 10 fps"),
			new Achievement(AchievementType.UPSIDE_DOWN, "Anti - gravity",
					"flip your phone upside down") };

	List<AchievementType> achievementsToShow = new ArrayList<AchievementType>();
	boolean animating;
	GamePanel gamePanel;
	SharedPreferences settingsPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game);
		application = (GravityApplication) getApplication();

		//initLevel();

		gamePanel = (GamePanel) findViewById(R.id.gamePanel1);
		gamePanel.setAchievementListener(new AchievementListener() {

			@Override
			public void onAchievement(AchievementType type) {
				if (application.achievements.contains(type))
					return;

				application.addAchievement(type);
				showAchievement(type);
			}
		});
		gamePanel.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
				return false;
			}
		});

		KnobView leftKnob = (KnobView) findViewById(R.id.knobViewLeft);
		leftKnob.setOnKnoStateChanged(new OnKnobStateChanged() {

			@Override
			public void onKnobStateChanged(int action, float x, float y) {
				// TODO Auto-generated method stub

			}
		});
		KnobView rightKnob = (KnobView) findViewById(R.id.knobViewRight);
		rightKnob.setOnKnoStateChanged(new OnKnobStateChanged() {

			@Override
			public void onKnobStateChanged(int action, float x, float y) {
				// TODO Auto-generated method stub

			}
		});

		//initTutorial();
	}

	private void initTutorial() {
		final PinchTutorial pinchTutorial = new PinchTutorial(this);
		final TapTutorial tapTutorial = new TapTutorial(this);
		final LongpressTutorial longpressTutorial = new LongpressTutorial(this);
		final SwipeTutorial swipeTutorial = new SwipeTutorial(this);
		
		final Tutorial landscapeTutorial = new Tutorial(this);
		landscapeTutorial.setText("Rotate device to landscape for action mode");
		landscapeTutorial.setTapEnabled();
		
		final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		final RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
		content.addView(pinchTutorial, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));

		pinchTutorial.setTutorialLogic(new TutorialLogic() {
			float startScale;

			@Override
			public void onStart() {
				gamePanel.scaleTo(1);
				startScale = gamePanel.getScale();
			}

			@Override
			public void onFinished() {
				content.removeView(pinchTutorial);
				content.addView(tapTutorial, layoutParams);
				tapTutorial.start();
			}

			@Override
			public boolean isFinished() {
				return Math.abs(gamePanel.getScale() - startScale) > 0.5;
			}
		});
		pinchTutorial.start();

		tapTutorial.setTutorialLogic(new TutorialLogic() {
			int startStars;

			@Override
			public void onStart() {
				startStars = gamePanel.getStars();
				gamePanel.scaleTo(2);
			}

			@Override
			public void onFinished() {
				content.removeView(tapTutorial);
				content.addView(longpressTutorial, layoutParams);
				longpressTutorial.start();
			}

			@Override
			public boolean isFinished() {
				return gamePanel.getStars() > startStars;
			}
		});
		
		longpressTutorial.setTutorialLogic(new TutorialLogic() {
			int startStars;

			@Override
			public void onStart() {
				startStars = gamePanel.getStars();
				gamePanel.scaleTo(2);
			}

			@Override
			public void onFinished() {
				content.removeView(longpressTutorial);
				content.addView(swipeTutorial, layoutParams);
				swipeTutorial.start();
			}

			@Override
			public boolean isFinished() {
				return gamePanel.getStars() > startStars;
			}
		});

		swipeTutorial.setTutorialLogic(new TutorialLogic() {
			int startStars;

			@Override
			public void onStart() {
				startStars = gamePanel.getStars();
				gamePanel.scaleTo(2);
			}

			@Override
			public void onFinished() {
				content.removeView(swipeTutorial);
				content.addView(landscapeTutorial,layoutParams);
				landscapeTutorial.start();
			}

			@Override
			public boolean isFinished() {
				return gamePanel.getStars() > startStars;
			}
		});
		
		landscapeTutorial.setTutorialLogic(new TutorialLogic() {
			
			@Override
			public void onStart() {
			}
			
			@Override
			public void onFinished() {
				content.removeView(landscapeTutorial);
			}
			
			@Override
			public boolean isFinished() {
				return !gamePanel.isInEditMode();
			}
		});
	}

	/*private void initLevel() {
		spaceship = new Spaceship();
		spaceship.x = 0;
		spaceship.y = 0;
	}*/

	public void showAchievement(AchievementType type) {
		synchronized (GameActivity.this) {
			if (animating) {
				achievementsToShow.add(type);
				return;
			}
			animating = true;
		}
		for (Achievement a : achievement) {
			if (a.type == type) {
				TextView title = (TextView) findViewById(R.id.title);
				title.setText(a.title);
				TextView description = (TextView) findViewById(R.id.description);
				description.setText(a.description);

				final Animation animationOut = AnimationUtils.loadAnimation(
						this, R.anim.notification_out);

				animationOut.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						findViewById(R.id.notification).setVisibility(
								View.INVISIBLE);
						synchronized (GameActivity.this) {
							if (achievementsToShow.size() > 0) {
								showAchievement(achievementsToShow.remove(0));
							} else {
								animating = false;
							}
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationStart(Animation animation) {
						findViewById(R.id.notification).setVisibility(
								View.VISIBLE);
					}
				});

				Animation animationIn = AnimationUtils.loadAnimation(this,
						R.anim.notification_in);

				animationIn.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								findViewById(R.id.notification).startAnimation(
										animationOut);
							}
						}, 2000);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationStart(Animation animation) {
						findViewById(R.id.notification).setVisibility(
								View.VISIBLE);
					}
				});

				findViewById(R.id.notification).startAnimation(animationIn);

				return;
			}
		}
	}
}
