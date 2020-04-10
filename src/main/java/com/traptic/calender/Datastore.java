package com.traptic.calender;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class Datastore {
  private List<Event> eventList;
  private List<User> userList;
  private List<Event> recurringEventList;


  public Datastore() {
    this.eventList = new ArrayList<>();
    this.userList = new ArrayList<>();
    this.recurringEventList = new ArrayList<>();
  }

  public List<Event> getEventList() {
    return eventList;
  }

  public void setEventList(List<Event> eventList) {
    this.eventList = eventList;
  }

  public List<User> getUserList() {
    return userList;
  }

  public void setUserList(List<User> userList) {
    this.userList = userList;
  }

  public void addUser(User user) {
    this.userList.add(user);
  }

  public void addEvent(Event event) {
    this.eventList.add(event);
  }

  public void addRecurringEvent(Event event) {
    this.recurringEventList.add(event);
  }

  public User getUser(String userId) {
    for (User user: userList) {
      if (user.getUserId().equals(userId)) {
        return user;
      }
    }
    return null;
  }

  public Event getEvent(String eventId) {
    for (Event event: eventList) {
      if (event.getEventId().equals(eventId)) {
        return event;
      }
    }
    return null;
  }

  public Event getRecurringEvent(String eventId) {
    for (Event event: recurringEventList) {
      if (event.getEventId().equals(eventId)) {
        return event;
      }
    }
    return null;
  }

  public List<Event> getRecurringEventList() {
    return recurringEventList;
  }

  public void setRecurringEventList(List<Event> recurringEventList) {
    this.recurringEventList = recurringEventList;
  }
}
