package com.example.gymtimer.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.gymtimer.interfaces.DMLOperations;
import com.example.gymtimer.interfaces.DMLOperationsOnMultiple;
import com.example.gymtimer.interfaces.ILinkGroupTimerDaoManager;
import com.example.gymtimer.models.LinkGroupTimer;
import com.example.gymtimer.repositories.LinkGroupTimerRepository;

import java.util.ArrayList;

public class LinkGroupTimerViewModel extends AndroidViewModel implements ILinkGroupTimerDaoManager {
  private LinkGroupTimerRepository linkGroupTimerRepository;

  public LinkGroupTimerViewModel(@NonNull Application application) {
    super(application);
    linkGroupTimerRepository = new LinkGroupTimerRepository(application);
  }

  @Override
  public void insert(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    linkGroupTimerRepository.insert(linkGroupTimers, dmlOperationsOnMultiple);
  }

  @Override
  public void update(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    linkGroupTimerRepository.update(linkGroupTimers, dmlOperationsOnMultiple);
  }

  @Override
  public void getAllByGroup(int groupId, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    linkGroupTimerRepository.getAllByGroup(groupId, dmlOperationsOnMultiple);
  }

  @Override
  public void delete(LinkGroupTimer linkGroupTimer, DMLOperations<LinkGroupTimer> dmlOperations) {
    linkGroupTimerRepository.delete(linkGroupTimer, dmlOperations);
  }

  @Override
  public void deleteMultiple(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    linkGroupTimerRepository.deleteMultiple(linkGroupTimers, dmlOperationsOnMultiple);
  }

  @Override
  public void getAllByTimer(int timerId, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple) {
    linkGroupTimerRepository.getAllByTimer(timerId, dmlOperationsOnMultiple);
  }
}
