package com.example.gymtimer.interfaces;

import com.example.gymtimer.models.LinkGroupTimer;

import java.util.ArrayList;

public interface ILinkGroupTimerDaoManager {
  void insert(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple);
  void update(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple);
  void getAllByGroup (int groupId, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple);
  void delete(LinkGroupTimer linkGroupTimer, DMLOperations<LinkGroupTimer> dmlOperations);
  void deleteMultiple(ArrayList<LinkGroupTimer> linkGroupTimers, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple);
  void getAllByTimer (int timerId, DMLOperationsOnMultiple<LinkGroupTimer> dmlOperationsOnMultiple);
}
