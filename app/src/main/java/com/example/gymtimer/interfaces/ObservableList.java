package com.example.gymtimer.interfaces;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface ObservableList<T> {
  void onSuccess(LiveData<List<T>> itemsList);
  void onFailure(Exception e);
}
