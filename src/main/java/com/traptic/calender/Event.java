package com.traptic.calender;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
  private String eventId;
  private String userId;
  private Date createTime;
  private Date eventTime;
  private long durationInSeconds;
  private long recurringTimeInSeconds;
  private String title;
  private List<String> invitedUserIds = new ArrayList<>();

  public Event() {}

  public Event(String title, String eventId, String userId, Date createTime, Date eventTime, long durationInSeconds, long recurringTimeInSeconds, List<String> invitedUserIds) {
    this.eventId = eventId;
    this.userId = userId;
    this.createTime = createTime;
    this.eventTime = eventTime;
    this.durationInSeconds = durationInSeconds;
    this.recurringTimeInSeconds = recurringTimeInSeconds;
    this.invitedUserIds = invitedUserIds;
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public List<String> getInvitedUserIds() {
    return invitedUserIds;
  }

  public void setInvitedUserIds(List<String> invitedUserIds) {
    this.invitedUserIds = invitedUserIds;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getEventTime() {
    return eventTime;
  }

  public void setEventTime(Date eventTime) {
    this.eventTime = eventTime;
  }

  public long getDurationInSeconds() {
    return durationInSeconds;
  }

  public void setDurationInSeconds(long durationInSeconds) {
    this.durationInSeconds = durationInSeconds;
  }

  public long getRecurringTimeInSeconds() {
    return recurringTimeInSeconds;
  }

  public void setRecurringTimeInSeconds(long recurringTimeInSeconds) {
    this.recurringTimeInSeconds = recurringTimeInSeconds;
  }
}
