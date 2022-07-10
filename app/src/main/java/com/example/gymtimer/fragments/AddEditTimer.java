package com.example.gymtimer.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymtimer.MainActivity;
import com.example.gymtimer.R;
import com.example.gymtimer.models.Timer;
import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.viewmodels.TimerViewModel;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Locale;

public class AddEditTimer extends Fragment {
  private Context context;
  private Boolean isNew;
  private Timer timer;
  private MainActivity mainActivity;
  private TimerViewModel timerViewModel;

  public AddEditTimer(Context context, Timer timer, TimerViewModel timerViewModel) {
    this.context = context;
    this.timer = timer;
    mainActivity = (MainActivity) context;
    this.timerViewModel = timerViewModel;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // variable declaration and initialisation
    View rootView = inflater.inflate(R.layout.fragment_add_edit_timer, container, false);
    TextView addEditLabel = rootView.findViewById(R.id.addEditLabelText);
    final EditText timerName = rootView.findViewById(R.id.timerNameEdit);
    final TextView workOutTimeText = rootView.findViewById(R.id.workOutTimeValue);
    final LinearLayout workOutTimeContainer = rootView.findViewById(R.id.workOutTimeContainer);
    final NumberPicker workOutTimeMin = rootView.findViewById(R.id.workOutTimeMin);
    final NumberPicker workOutTimeSec = rootView.findViewById(R.id.workOutTimeSec);
    final ImageButton saveWorkOutTimer = rootView.findViewById(R.id.saveWorkOutTimeBtn);
    final TextView setsValueText = rootView.findViewById(R.id.setsValueText);
    final NumberPicker setsPicker = rootView.findViewById(R.id.setsPicker);
    final ImageButton saveSetsButton = rootView.findViewById(R.id.saveSetsBtn);
    final TextView setBreakTimeText = rootView.findViewById(R.id.setBreakValueText);
    final LinearLayout setBreakTimeContainer = rootView.findViewById(R.id.setBreakTimeContainer);
    final NumberPicker setBreakTimeMin = rootView.findViewById(R.id.setBreakTimeMin);
    final NumberPicker setBreakTimeSec = rootView.findViewById(R.id.setBreakTimeSec);
    final ImageButton saveSetBreakTimeBtn = rootView.findViewById(R.id.saveSetBreakTimeBtn);
    final Button cancelButton = rootView.findViewById(R.id.cancelBtn);
    final Button saveButton = rootView.findViewById(R.id.saveTimerBtn);
    final TextView alert1Text = rootView.findViewById(R.id.alert1Text);
    final TextView alert2Text = rootView.findViewById(R.id.alert2Text);
    final LinearLayout alert1Container = rootView.findViewById(R.id.alert1Container);
    final LinearLayout alert2Container = rootView.findViewById(R.id.alert2Container);
    final ImageButton saveInWorkoutAlertsBtn = rootView.findViewById(R.id.saveInWorkoutAlertsBtn);
    final NumberPicker alert1TimeMin = rootView.findViewById(R.id.alert1Min);
    final NumberPicker alert1TimeSec = rootView.findViewById(R.id.alert1Sec);
    final NumberPicker alert2TimeMin = rootView.findViewById(R.id.alert2Min);
    final NumberPicker alert2TimeSec = rootView.findViewById(R.id.alert2Sec);

    // applying settings
    workOutTimeMin.setMinValue(0);
    workOutTimeMin.setMaxValue(59);
    workOutTimeSec.setMinValue(0);
    workOutTimeSec.setMaxValue(59);
    setsPicker.setMinValue(0);
    setsPicker.setMaxValue(99);
    setBreakTimeMin.setMinValue(0);
    setBreakTimeMin.setMaxValue(59);
    setBreakTimeSec.setMinValue(0);
    setBreakTimeSec.setMaxValue(59);
    alert1TimeMin.setMinValue(0);
    alert1TimeMin.setMaxValue(59);
    alert1TimeSec.setMinValue(0);
    alert1TimeSec.setMaxValue(59);
    alert2TimeMin.setMinValue(0);
    alert2TimeMin.setMaxValue(59);
    alert2TimeSec.setMinValue(0);
    alert2TimeSec.setMaxValue(59);

    if (timer == null) {
      isNew = true;
      addEditLabel.setText(R.string.add_new_timer);
      workOutTimeMin.setValue(0);
      workOutTimeSec.setValue(0);
      setsPicker.setValue(0);
      setBreakTimeMin.setValue(0);
      setBreakTimeSec.setValue(0);
      alert1TimeMin.setValue(0);
      alert1TimeSec.setValue(0);
      alert2TimeMin.setValue(0);
      alert2TimeSec.setValue(0);
    }
    else {
      isNew = false;
      addEditLabel.setText(R.string.edit_timer);
      if (getActivity() != null) {
        timerName.setText(timer.getTimerName());
        workOutTimeText.setText(timer.getWorkOutTime());
        String sets = String.format(Locale.ENGLISH, "%02d", timer.getSets());
        setsValueText.setText(sets);
        setBreakTimeText.setText(timer.getSetBreak());
        alert1Text.setText(timer.getInWorkoutAlert1());
        alert2Text.setText(timer.getInWorkoutAlert2());
        int workOutMin = Integer.parseInt(timer.getWorkOutTime().substring(0, 2));
        int workOutSec = Integer.parseInt(timer.getWorkOutTime().substring(3));
        workOutTimeMin.setValue(workOutMin);
        workOutTimeSec.setValue(workOutSec);
        setsPicker.setValue(timer.getSets());
        int setBreakMin = Integer.parseInt(timer.getSetBreak().substring(0, 2));
        int setBreakSec = Integer.parseInt(timer.getSetBreak().substring(3));
        setBreakTimeMin.setValue(setBreakMin);
        setBreakTimeSec.setValue(setBreakSec);
        int alert1Min = Integer.parseInt(timer.getInWorkoutAlert1().substring(0, 2));
        int alert1Sec = Integer.parseInt(timer.getInWorkoutAlert1().substring(3));
        alert1TimeMin.setValue(alert1Min);
        alert1TimeSec.setValue(alert1Sec);
        int alert2Min = Integer.parseInt(timer.getInWorkoutAlert2().substring(0, 2));
        int alert2Sec = Integer.parseInt(timer.getInWorkoutAlert2().substring(3));
        alert2TimeMin.setValue(alert2Min);
        alert2TimeSec.setValue(alert2Sec);
      }
    }

    //  functions
    workOutTimeText.setOnClickListener(v -> {
      if (workOutTimeContainer.getVisibility() == View.GONE) {
        workOutTimeContainer.setVisibility(View.VISIBLE);
        saveWorkOutTimer.setVisibility(View.VISIBLE);
      }
    });

    saveWorkOutTimer.setOnClickListener(v -> {
      workOutTimeContainer.setVisibility(View.GONE);
      saveWorkOutTimer.setVisibility(View.GONE);
    });

    workOutTimeMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = String.format(Locale.ENGLISH, "%02d", newVal) + workOutTimeText.getText().toString().substring(2);
      workOutTimeText.setText(time);
    });

    workOutTimeSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = workOutTimeText.getText().toString().substring(0, 3) + String.format(Locale.ENGLISH, "%02d", newVal);
      workOutTimeText.setText(time);
    });

    setsValueText.setOnClickListener(v -> {
      if (setsPicker.getVisibility() == View.GONE) {
        setsPicker.setVisibility(View.VISIBLE);
        saveSetsButton.setVisibility(View.VISIBLE);
      }
    });

    saveSetsButton.setOnClickListener(v -> {
      setsPicker.setVisibility(View.GONE);
      saveSetsButton.setVisibility(View.GONE);
    });

    setsPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String sets = String.format(Locale.ENGLISH, "%02d", newVal);
      setsValueText.setText(sets);
    });

    setBreakTimeText.setOnClickListener(v -> {
      if (setBreakTimeContainer.getVisibility() == View.GONE) {
        setBreakTimeContainer.setVisibility(View.VISIBLE);
        saveSetBreakTimeBtn.setVisibility(View.VISIBLE);
      }
    });

    saveSetBreakTimeBtn.setOnClickListener(v -> {
      setBreakTimeContainer.setVisibility(View.GONE);
      saveSetBreakTimeBtn.setVisibility(View.GONE);
    });

    setBreakTimeMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = String.format(Locale.ENGLISH, "%02d", newVal) + setBreakTimeText.getText().toString().substring(2);
      setBreakTimeText.setText(time);
    });

    setBreakTimeSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = setBreakTimeText.getText().toString().substring(0, 3) + String.format(Locale.ENGLISH, "%02d", newVal);
      setBreakTimeText.setText(time);
    });

    PushDownAnim.setPushDownAnimTo(cancelButton)
      .setOnClickListener(v -> requireActivity().onBackPressed());

    alert1Text.setOnClickListener(v -> {
      if (alert2Container.getVisibility() == View.VISIBLE)
        alert2Container.setVisibility(View.GONE);
      if (alert1Container.getVisibility() == View.GONE)
        alert1Container.setVisibility(View.VISIBLE);
      if (saveInWorkoutAlertsBtn.getVisibility() == View.GONE)
        saveInWorkoutAlertsBtn.setVisibility(View.VISIBLE);
    });

    alert1TimeMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = String.format(Locale.ENGLISH, "%02d", newVal) + alert1Text.getText().toString().substring(2);
      alert1Text.setText(time);
    });

    alert1TimeSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = alert1Text.getText().toString().substring(0, 3) + String.format(Locale.ENGLISH, "%02d", newVal);
      alert1Text.setText(time);
    });

    alert2Text.setOnClickListener(v -> {
      if (alert1Container.getVisibility() == View.VISIBLE)
        alert1Container.setVisibility(View.GONE);
      if (alert2Container.getVisibility() == View.GONE)
        alert2Container.setVisibility(View.VISIBLE);
      if (saveInWorkoutAlertsBtn.getVisibility() == View.GONE)
        saveInWorkoutAlertsBtn.setVisibility(View.VISIBLE);
    });

    alert2TimeMin.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = String.format(Locale.ENGLISH, "%02d", newVal) + alert2Text.getText().toString().substring(2);
      alert2Text.setText(time);
    });

    alert2TimeSec.setOnValueChangedListener((picker, oldVal, newVal) -> {
      String time = alert2Text.getText().toString().substring(0, 3) + String.format(Locale.ENGLISH, "%02d", newVal);
      alert2Text.setText(time);
    });

    saveInWorkoutAlertsBtn.setOnClickListener(v -> {
      saveInWorkoutAlertsBtn.setVisibility(View.GONE);
      if (alert1Container.getVisibility() == View.VISIBLE)
        alert1Container.setVisibility(View.GONE);
      else
        alert2Container.setVisibility(View.GONE);
    });

    timerName.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        timerName.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_white, null));
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    PushDownAnim.setPushDownAnimTo(saveButton)
      .setOnClickListener(v -> {
        if (timerName.getText().toString().isEmpty()) {
          showMessage(getString(R.string.workout_name_cannot_be_empty));
          timerName.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_red, null));
          return;
        }
        mainActivity.appProgressBar.setVisibility(View.VISIBLE);
        if (isNew)
          timer = new Timer(timerName.getText().toString());
        else
          timer.setTimerName(timerName.getText().toString());

        timer.setWorkOutTime(workOutTimeText.getText().toString());
        timer.setSets(Integer.parseInt(setsValueText.getText().toString()));
        timer.setSetBreak(setBreakTimeText.getText().toString());
        timer.setInWorkoutAlert1(alert1Text.getText().toString());
        timer.setInWorkoutAlert2(alert2Text.getText().toString());
        if (isNew)
          timerViewModel.insert(timer, new DMLOperations<>() {
            @Override
            public void onSuccess(Timer item) {
              mainActivity.appProgressBar.setVisibility(View.GONE);
              if (getActivity() != null) {
                showMessage(timer.getTimerName() + getString(R.string.inserted_successfully));
                requireActivity().onBackPressed();
              }
            }

            @Override
            public void onFailure(Timer item, Exception e) {
              if (getActivity() != null) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                showMessage(e.getMessage());
              }
            }
          });
        else
          timerViewModel.update(timer, new DMLOperations<>() {
            @Override
            public void onSuccess(Timer item) {
              mainActivity.appProgressBar.setVisibility(View.GONE);
              if (getActivity() != null) {
                showMessage(timer.getTimerName() + getString(R.string.updated_successfully));
                requireActivity().onBackPressed();
              }
            }

            @Override
            public void onFailure(Timer item, Exception e) {
              if (getActivity() != null) {
                mainActivity.appProgressBar.setVisibility(View.GONE);
                showMessage(e.getMessage());
              }
            }
          });
      });

    return rootView;
  }

  private void showMessage(String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
