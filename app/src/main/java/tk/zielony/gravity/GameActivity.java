package tk.zielony.gravity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.zielony.gravity.game.Achievement;
import tk.zielony.gravity.game.GamePanel;
import tk.zielony.gravity.knob.KnobView;
import tk.zielony.gravity.tutorial.LongpressTutorial;
import tk.zielony.gravity.tutorial.PinchTutorial;
import tk.zielony.gravity.tutorial.SwipeTutorial;
import tk.zielony.gravity.tutorial.TapTutorial;
import tk.zielony.gravity.tutorial.Tutorial;
import tk.zielony.gravity.tutorial.TutorialLogic;

public class GameActivity extends AppCompatActivity {
    GravityApplication application;
    //Spaceship spaceship;

    List<Achievement> achievementsToShow = new ArrayList<Achievement>();
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

        gamePanel = findViewById(R.id.gamePanel1);
        gamePanel.setAchievementListener(type -> {
            if (application.achievements.contains(type))
                return;

            application.addAchievement(type);
            showAchievement(type);
        });
        gamePanel.setOnTouchListener((v, event) -> {
            // rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            return false;
        });

        KnobView leftKnob = findViewById(R.id.knobViewLeft);
        leftKnob.setOnKnobStateChanged((action, x, y) -> {
            // TODO Auto-generated method stub

        });
        KnobView rightKnob = findViewById(R.id.knobViewRight);
        rightKnob.setOnKnobStateChanged((action, x, y) -> {
            // TODO Auto-generated method stub
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

        final RelativeLayout content = findViewById(R.id.content);
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
                content.addView(landscapeTutorial, layoutParams);
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

    public void showAchievement(Achievement a) {
        synchronized (GameActivity.this) {
            if (animating) {
                achievementsToShow.add(a);
                return;
            }
            animating = true;
        }
        TextView title = findViewById(R.id.title);
        title.setText(a.getTitle());
        TextView description = findViewById(R.id.description);
        description.setText(a.getDescription());

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
                findViewById(R.id.notification).setVisibility(View.VISIBLE);
            }
        });

        Animation animationIn = AnimationUtils.loadAnimation(this,
                R.anim.notification_in);

        animationIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(() -> findViewById(R.id.notification).startAnimation(
                        animationOut), 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.notification).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.notification).startAnimation(animationIn);
    }
}
