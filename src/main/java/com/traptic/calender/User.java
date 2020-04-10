package com.traptic.calender;

import java.util.ArrayList;
import java.util.List;

public class User {
  private String userId;
  private List<String> eventIdList = new ArrayList<>();

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<String> getEventIdList() {
    return eventIdList;
  }

  public void setEventIdList(List<String> eventIdList) {
    this.eventIdList = eventIdList;
  }

  public void addEventId(String eventId) {
    this.eventIdList.add(eventId);
  }
}
