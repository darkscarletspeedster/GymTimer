package com.example.gymtimer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gymtimer.common.BounceInterpolator;
import com.example.gymtimer.fragments.AddEditTimer;
import com.example.gymtimer.fragments.GroupManager;
import com.example.gymtimer.fragments.MainListTab;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.OnBreakFinish;
import com.example.gymtimer.interfaces.OnCountDownFinish;
import com.example.gymtimer.interfaces.OnGroupFinish;
import com.example.gymtimer.interfaces.OnTimerFinish;
import com.example.gymtimer.interfaces.OnWorkoutTimeFinish;
import com.example.gymtimer.models.Group;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.models.PauseModel;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
  private long lastClickedTime = 0;
  public RelativeLayout appProgressBar;
  public RelativeLayout navLayout;
  public boolean isDeleteOn;
  private Button groupsBtn;
  private ImageButton addTimerButton;
  private ImageButton delTimerButton;
  private CheckBox selectAllCheck;
  private TextView itemSelectedCount;
  private MainListTab mainListTab;
  private TextView itemSelectedCountText;
  public boolean selectAll;
  private Animation countDownAnimation;
  private SoundPool soundPool;
  private int countDownSound, inBetweenSound, workoutEnd;
  private int curStreamId;
  public Application application;
  private Thread timerThread, groupThread;
  private RelativeLayout startLayout;
  private TextView groupNameInClockText;
  private TextView timerNameInClockText;
  private TextView countDownText;
  private boolean isStopped, isPaused, isTimerOn, waiting;
  private LinearLayout mainTimerLayout;
  private LinearLayout controlLayout;
  private TextView breakOnText;
  private TextView startMinText;
  private TextView startSecText;
  private ImageButton cancelBreakBtn, pauseBtn;
  private OnGroupFinish onGroupFinish;
  private CountDownTimer countDownTimer;
  private final Object lockObject = new Object();
  private PauseModel pauseModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // variable declaration and initialisation
    application = getApplication();
    final TimerViewModel timerViewModel = new TimerViewModel(application);
    addTimerButton = findViewById(R.id.addTimerBtn);
    delTimerButton = findViewById(R.id.delTimerBtn);
    groupsBtn = findViewById(R.id.mngGrpsBtn);
    final Animation addBtnAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
    final Animation delBtnAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
    final BounceInterpolator bounceInterpolator = new BounceInterpolator(0.3, 20);
    final BounceInterpolator counterBounceInterpolator = new BounceInterpolator(0.3, 15);
    FrameLayout frameLayout = findViewById(R.id.fragmentContainer);
    final FragmentManager fragmentManager = getSupportFragmentManager();
    mainListTab = new MainListTab(this, timerViewModel);
    appProgressBar = findViewById(R.id.appProgressBar);
    navLayout = findViewById(R.id.navLayout);
    selectAllCheck = findViewById(R.id.selectAllCheck);
    itemSelectedCount = findViewById(R.id.itemSelectedCount);
    itemSelectedCountText = findViewById(R.id.itemSelectedCountText);
    countDownAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

    // setting up audio
    AudioAttributes audioAttributes = new AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_MEDIA)
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
      .build();
    soundPool = new SoundPool.Builder()
      .setMaxStreams(1)
      .setAudioAttributes(audioAttributes)
      .build();

    countDownSound = soundPool.load(this, R.raw.count_down_sound, 1);
    inBetweenSound = soundPool.load(this, R.raw.in_between_workout, 1);
    workoutEnd = soundPool.load(this, R.raw.workout_end, 1);

    GradientDrawable gradientDrawable = new GradientDrawable(
      GradientDrawable.Orientation.TOP_BOTTOM,
      new int[]{
        ResourcesCompat.getColor(getResources(), R.color.base, null),
        ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null),
        ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null),
        ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null)
      }
    );

    // timer UI elements
    startLayout = findViewById(R.id.startLayout);
    groupNameInClockText = findViewById(R.id.groupNameInClockText);
    timerNameInClockText = findViewById(R.id.timerNameInClockText);
    countDownText = findViewById(R.id.countDownText);
    mainTimerLayout = findViewById(R.id.mainTimerLayout);
    controlLayout = findViewById(R.id.controlLayout);
    breakOnText = findViewById(R.id.breakOnText);
    ImageButton stopBtn = findViewById(R.id.stopBtn);
    cancelBreakBtn = findViewById(R.id.cancelBreakBtn);
    startMinText = findViewById(R.id.startMinText);
    startSecText = findViewById(R.id.startSecText);
    pauseBtn = findViewById(R.id.pauseBtn);

    // applying settings
    frameLayout.setBackground(gradientDrawable);
    addBtnAnimation.setInterpolator(bounceInterpolator);
    delBtnAnimation.setInterpolator(bounceInterpolator);
    countDownAnimation.setInterpolator(counterBounceInterpolator);

    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mainListTab).commit();

    addTimerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - lastClickedTime < 1000) {
          showMessage(getString(R.string.presses_too_soon));
          return;
        }
        lastClickedTime = SystemClock.elapsedRealtime();
        addTimerButton.startAnimation(addBtnAnimation);
        fragmentManager.beginTransaction()
          .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
          .replace(R.id.fragmentContainer, new AddEditTimer(MainActivity.this, null, timerViewModel))
          .addToBackStack(null)
          .commit();

        groupsBtn.setVisibility(View.INVISIBLE);
        addTimerButton.setEnabled(false);
      }
    });

    delTimerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        delTimerButton.startAnimation(delBtnAnimation);
      }
    });

    PushDownAnim.setPushDownAnimTo(groupsBtn)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - lastClickedTime < 1000) {
            showMessage(getString(R.string.presses_too_soon));
            return;
          }
          lastClickedTime = SystemClock.elapsedRealtime();
          fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            .replace(R.id.fragmentContainer, new GroupManager(MainActivity.this, timerViewModel))
            .addToBackStack(null)
            .commit();
          addTimerButton.setVisibility(View.INVISIBLE);
          groupsBtn.setEnabled(false);
          ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", (float)(addTimerButton.getLeft() - v.getLeft()));
          animator.setDuration(500);
          animator.start();
        }
      });

    // timer control buttons
    PushDownAnim.setPushDownAnimTo(stopBtn)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          onTimerDone();
        }
      });

    PushDownAnim.setPushDownAnimTo(cancelBreakBtn)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String min = String.format(Locale.ENGLISH, "%02d", 0);
          String sec = ":" + String.format(Locale.ENGLISH, "%02d", 0);
          startMinText.setText(min);
          startSecText.setText(sec);
          countDownTimer.cancel();
          new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
              countDownTimer.onFinish();
            }
          }, 1000);

          pauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause_button_icon, null));
          pauseBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_primary_dark, null));
          isPaused = false;
        }
      });

    PushDownAnim.setPushDownAnimTo(pauseBtn)
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (SystemClock.elapsedRealtime() - lastClickedTime < 1000) {
            showMessage(getString(R.string.presses_too_soon));
            return;
          }
          lastClickedTime = SystemClock.elapsedRealtime();
          if (isPaused) {
            if (pauseModel != null && pauseModel.isBreak())
              runBreak(pauseModel.getBreakTime(), pauseModel.getOnBreakFinish(), pauseModel.isGroupBreak(), pauseModel.getPendingMill());
            else
              runWorkout(pauseModel.getTimer(), pauseModel.getOnWorkoutTimeFinish(), pauseModel.getPendingMill());

            soundPool.resume(curStreamId);
            pauseModel = null;
            pauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause_button_icon, null));
            pauseBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_primary_dark, null));
          } else {
            isPaused = true;
            soundPool.pause(curStreamId);
            pauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button_icon, null));
            pauseBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_light_green, null));
          }
        }
      });
  }

  /*
  * Starting a group
  *
  */
  public void startGroup(final ArrayList<LinkGroupTimer> linkGroupTimers, final OnGroupFinish onGroupFinish) {
    isTimerOn = true;
    Group group = linkGroupTimers.get(0).getGroup();
    this.onGroupFinish = onGroupFinish;

    startLayout.setVisibility(View.VISIBLE);
    groupNameInClockText.setVisibility(View.VISIBLE);
    groupNameInClockText.setText(group.getGroupName());
    navLayout.setVisibility(View.GONE);

    final Timer groupBreakTimer = new Timer("groupBreakTimer");
    groupBreakTimer.setWorkOutTime(getString(R.string.initial_time));
    groupBreakTimer.setInWorkoutAlert1(getString(R.string.initial_time));
    groupBreakTimer.setInWorkoutAlert2(getString(R.string.initial_time));
    groupBreakTimer.setSets(1);

    /*startCountDown(new OnCountDownFinish() {
      @Override
      public void setOnCountDownFinishListener() {
        groupThread = new Thread(new Runnable() {
          @Override
          public void run() {
            for (int i = 0; i < linkGroupTimers.size(); i++) {
              final int j = i;
              final Timer timer = linkGroupTimers.get(i).getTimer();
              new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                  timerNameInClockText.setText(timer.getTimerName());
                  runTimer(timer, new OnTimerFinish() {
                    @Override
                    public void setOnTimerFinishListener() {
                      isStopped = false;
                      groupThread.interrupt();
                      if (j == linkGroupTimers.size() - 1)
                        onTimerDone();
                    }
                  }, false);
                }
              });
              while (true) {
                try {
                  Thread.sleep(10000);
                } catch (InterruptedException e) {
                  if(isStopped)
                    return;

                  break;
                }
              }
              if (j != linkGroupTimers.size() - 1) {
                new Handler(getMainLooper()).post(new Runnable() {
                  @Override
                  public void run() {
                    groupBreakTimer.setSetBreak(linkGroupTimers.get(j).getInGroupTime());
                    runTimer(groupBreakTimer, new OnTimerFinish() {
                      @Override
                      public void setOnTimerFinishListener() {
                        isStopped = false;
                        groupThread.interrupt();
                      }
                    }, true);
                  }
                });
                while (true) {
                  try {
                    Thread.sleep(10000);
                  } catch (InterruptedException e) {
                    if(isStopped)
                      return;

                    break;
                  }
                }
              }
            }
          }
        });
        groupThread.start();
      }
    });*/
  }

  /*
   * Starting a timer
   */
  public void startTimer(final Timer timer) {
    if (!isTimerOn) {
      isTimerOn = true;
      startLayout.setVisibility(View.VISIBLE);
      navLayout.setVisibility(View.GONE);

      startCountDown(new OnCountDownFinish() {
        @Override
        public void setOnCountDownFinishListener() {
          mainTimerLayout.setVisibility(View.VISIBLE);
          controlLayout.setVisibility(View.VISIBLE);
          breakOnText.setVisibility(View.VISIBLE);
          cancelBreakBtn.setVisibility(View.VISIBLE);
          runTimer(timer, new OnTimerFinish() {
            @Override
            public void setOnTimerFinishListener() {
              onTimerDone();
            }
          });
        }
      });
    }
  }

  private void runTimer(final Timer timer, final OnTimerFinish onTimerFinish) {
    timerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Looper.prepare();
          for (int i = timer.getSets() - 1; i >= 0; i--) {
            final int set = i;
            new Handler(getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                timerNameInClockText.setText(timer.getTimerName());
                String curSet = getString(R.string.set) + String.format(Locale.ENGLISH, "%02d", (timer.getSets() - set));
                breakOnText.setText(curSet);
                runWorkout(timer, new OnWorkoutTimeFinish() {
                  @Override
                  public void setOnWorkoutTimeFinishListener() {
                    synchronized (lockObject) {
                      waiting = false;
                      lockObject.notify();
                    }
                  }
                }, 0);
              }
            });
            synchronized (lockObject) {
              waiting = true;
              while (waiting) {
                try {
                  lockObject.wait();
                } catch (InterruptedException e) {
                  return;
                }
              }
            }
            if (i != 0) {
              new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                  runBreak(timer.getSetBreak(), new OnBreakFinish() {
                    @Override
                    public void setOnBreakFinishListener() {
                      synchronized (lockObject) {
                        waiting = false;
                        lockObject.notify();
                      }
                    }
                  }, false, 0);
                }
              });
              synchronized (lockObject) {
                waiting = true;
                while (waiting) {
                  try {
                    lockObject.wait();
                  } catch (InterruptedException e) {
                    return;
                  }
                }
              }
            }
          }
          new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              onTimerFinish.setOnTimerFinishListener();
            }
          });
        } catch (Exception ignored) {
        }
      }
    });
    timerThread.start();
  }

  private void runWorkout(final Timer timer, final OnWorkoutTimeFinish onWorkoutTimeFinish, long pendingMill) {
    final HashSet<String> alertTimers = new HashSet<>();
    alertTimers.add(timer.getInWorkoutAlert1());
    alertTimers.add(timer.getInWorkoutAlert2());
    String startMin = timer.getWorkOutTime().substring(0, 2);
    String startSec = timer.getWorkOutTime().substring(3);
    long workMin = Long.parseLong(startMin) * 60000;
    long workSec = Long.parseLong(startSec) * 1000;
    long totalMill = workMin + workSec + 1000;
    if (isPaused) {
      isPaused = false;
      totalMill = pendingMill;
    }
    countDownTimer = new CountDownTimer(totalMill, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        if (isPaused) {
          countDownTimer.cancel();
          pauseModel = new PauseModel();
          pauseModel.setIsBreak(false);
          pauseModel.setTimer(timer);
          pauseModel.setOnWorkoutTimeFinish(onWorkoutTimeFinish);
          pauseModel.setPendingMill(millisUntilFinished);
          return;
        }
        long j = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1);
        long k = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1);
        String min = String.format(Locale.ENGLISH, "%02d", j);
        String sec = ":" + String.format(Locale.ENGLISH, "%02d", k);
        startMinText.setText(min);
        startSecText.setText(sec);
        if ((min + sec).equals(getString(R.string.initial_time))) {
          curStreamId = soundPool.play(workoutEnd, 1, 1, 0, 0, 1);
        } else if (alertTimers.contains(min + sec)) {
          curStreamId = soundPool.play(inBetweenSound, 1, 1, 0, 0, 1);
        }
      }

      @Override
      public void onFinish() {
        if (isPaused) {
          pauseModel = new PauseModel();
          pauseModel.setIsBreak(false);
          pauseModel.setTimer(timer);
          pauseModel.setOnWorkoutTimeFinish(onWorkoutTimeFinish);
          pauseModel.setPendingMill(0);
          return;
        }
        countDownTimer = null;
        onWorkoutTimeFinish.setOnWorkoutTimeFinishListener();
      }
    };
    countDownTimer.start();
  }

  private void runBreak(final String breakTime, final OnBreakFinish onBreakFinish, final boolean isGroupBreak, long pendingMill) {
    if (isGroupBreak)
      breakOnText.setText(R.string.group_break);
    else
      breakOnText.setText(R.string.break_on);

    String startMin = breakTime.substring(0, 2);
    String startSec = breakTime.substring(3);
    final long breakMin = Long.parseLong(startMin) * 60000;
    final long breakSec = Long.parseLong(startSec) * 1000;
    long totalMill = breakMin + breakSec + 1000;
    if (isPaused) {
      isPaused = false;
      totalMill = pendingMill;
    }
    countDownTimer = new CountDownTimer(totalMill, 1000) {
      @Override
      public void onTick(final long millisUntilFinished) {
        if (isPaused) {
          countDownTimer.cancel();
          pauseModel = new PauseModel();
          pauseModel.setIsBreak(true);
          pauseModel.setBreakTime(breakTime);
          pauseModel.setIsGroupBreak(isGroupBreak);
          pauseModel.setOnBreakFinish(onBreakFinish);
          pauseModel.setPendingMill(millisUntilFinished);
          return;
        }
        long j = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1);
        long k = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1);
        String min = String.format(Locale.ENGLISH, "%02d", j);
        String sec = ":" + String.format(Locale.ENGLISH, "%02d", k);
        startMinText.setText(min);
        startSecText.setText(sec);
        if (j == 0 && k == 0)
          soundPool.stop(curStreamId);
        else if (j == 0 && k == 3 || (breakMin == 0 && breakSec < 3000 && breakSec == k * 1000))
          curStreamId = soundPool.play(countDownSound, 1, 1, 0, 0, 1);
      }

      @Override
      public void onFinish() {
        if (isPaused) {
          pauseModel = new PauseModel();
          pauseModel.setIsBreak(true);
          pauseModel.setBreakTime(breakTime);
          pauseModel.setIsGroupBreak(isGroupBreak);
          pauseModel.setOnBreakFinish(onBreakFinish);
          pauseModel.setPendingMill(0);
          return;
        }
        countDownTimer = null;
        onBreakFinish.setOnBreakFinishListener();
      }
    };
    countDownTimer.start();
  }

  /*
   * Starting counter of any new activity.
   */
  public void startCountDown(final OnCountDownFinish onCountDownFinish) {
    isStopped = false;
    countDownText.setVisibility(View.VISIBLE);
    curStreamId = soundPool.play(countDownSound, 1, 1, 0, 0, 1);
    countDownTimer = new CountDownTimer(3000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        countDownText.setText(String.format(Locale.ENGLISH, "%d", (millisUntilFinished / 1000) + 1));
        countDownText.startAnimation(countDownAnimation);
      }

      @Override
      public void onFinish() {
        countDownText.setVisibility(View.GONE);
        countDownText.setText(R.string.string_empty);
        if (!isStopped)
          onCountDownFinish.setOnCountDownFinishListener();
        countDownTimer = null;
      }
    };

    countDownTimer.start();
  }

  /*
   * on timer or group done
   */
  public void onTimerDone() {
    isTimerOn = false;
    isPaused = false;
    isStopped = true;
    navLayout.setVisibility(View.VISIBLE);
    startLayout.setVisibility(View.GONE);
    soundPool.stop(curStreamId);
    groupNameInClockText.setVisibility(View.GONE);
    groupNameInClockText.setText(R.string.string_empty);
    timerNameInClockText.setText(R.string.string_empty);
    mainTimerLayout.setVisibility(View.GONE);
    controlLayout.setVisibility(View.GONE);
    breakOnText.setVisibility(View.GONE);
    breakOnText.setText(R.string.string_empty);
    startMinText.setText(R.string.string_empty);
    startSecText.setText(R.string.string_empty);
    cancelBreakBtn.setVisibility(View.GONE);
    pauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause_button_icon, null));
    pauseBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_primary_dark, null));
    pauseModel = null;

    if (countDownTimer != null) {
      countDownTimer.cancel();
      countDownTimer = null;
    }

    if (timerThread != null) {
      timerThread.interrupt();
      timerThread = null;
    }

    if (groupThread != null && groupThread.isAlive())
      groupThread.interrupt();

    if (onGroupFinish != null) {
      onGroupFinish.setOnGroupFinishListener();
      onGroupFinish = null;
    }
  }

  @Override
  public void onBackPressed() {
    if (appProgressBar.getVisibility() == View.VISIBLE)
      appProgressBar.setVisibility(View.GONE);
    if(isDeleteOn || isTimerOn)
      if (isDeleteOn)
        onLongClick(delTimerButton.getRootView());
      else
        onTimerDone();
    else {
      super.onBackPressed();
      Fragment curFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
      final ImageButton addTimerButton = findViewById(R.id.addTimerBtn);
      final Button groupsBtn = findViewById(R.id.mngGrpsBtn);
      if (curFragment instanceof MainListTab) {
        if (navLayout.getVisibility() == View.GONE)
          navLayout.setVisibility(View.VISIBLE);
        else {
          groupsBtn.setVisibility(View.VISIBLE);
          groupsBtn.setEnabled(true);
          Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
              addTimerButton.setVisibility(View.VISIBLE);
              addTimerButton.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
          };
          ObjectAnimator animator = ObjectAnimator.ofFloat(groupsBtn, "translationX", 0f);
          animator.addListener(listener);
          animator.setDuration(500);
          animator.start();
        }
      }
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      View view = getCurrentFocus();
      if (view instanceof EditText) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        if (!rect.contains((int)event.getRawX(), (int)event.getRawY())) {
          view.clearFocus();
          InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
      }
    }
    return super.dispatchTouchEvent( event );
  }

  @Override
  public boolean onLongClick(View v) {
    if(!isDeleteOn) {
      groupsBtn.setVisibility(View.GONE);
      addTimerButton.setVisibility(View.GONE);
      itemSelectedCount.setVisibility(View.VISIBLE);
      selectAllCheck.setVisibility(View.VISIBLE);
      delTimerButton.setVisibility(View.VISIBLE);
      itemSelectedCountText.setVisibility(View.VISIBLE);
      itemSelectedCount.setText(getString(R.string.initial_value_number));
      isDeleteOn = true;

      selectAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          selectAll = isChecked;
          mainListTab.refreshAdapter();
        }
      });

      PushDownAnim.setPushDownAnimTo(delTimerButton)
        .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            appProgressBar.setVisibility(View.VISIBLE);
            boolean goneForDelete = mainListTab.deleteItems(new DMLOperationsOnMultiple<Timer>() {
              @Override
              public void onSuccess(ArrayList<Timer> items) {
                appProgressBar.setVisibility(View.GONE);
                onLongClick(v);
                showMessage(items.size() + getString(R.string.items_deleted));
              }

              @Override
              public void onFailure(ArrayList<Timer> items, Exception e) {
                appProgressBar.setVisibility(View.GONE);
                onLongClick(v);
                showMessage(e.getMessage());
              }
            });
            if (!goneForDelete) {
              appProgressBar.setVisibility(View.GONE);
              showMessage(getString(R.string.no_items_selected));
            }
          }
        });
    } else {
      groupsBtn.setVisibility(View.VISIBLE);
      addTimerButton.setVisibility(View.VISIBLE);
      itemSelectedCount.setVisibility(View.GONE);
      selectAllCheck.setVisibility(View.GONE);
      delTimerButton.setVisibility(View.GONE);
      itemSelectedCountText.setVisibility(View.GONE);
      selectAllCheck.setChecked(false);
      isDeleteOn = false;
    }
    mainListTab.refreshAdapter();
    selectAll = false;
    return true;
  }

  public void updateCounter(int count) {
    String value = String.format(Locale.ENGLISH, "%02d", count);
    itemSelectedCount.setText(value);
  }
  private void showMessage(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onDestroy() {
    soundPool.release();
    soundPool = null;
    countDownTimer = null;
    super.onDestroy();
  }

  public boolean getIsTimerOn () {
    return isTimerOn;
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    final Configuration override = new Configuration(newBase.getResources().getConfiguration());
    override.fontScale = 0.9f;
    applyOverrideConfiguration(override);
  }
}
