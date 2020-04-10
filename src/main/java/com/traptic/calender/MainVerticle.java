package com.traptic.calender;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainVerticle extends AbstractVerticle {

  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  Datastore datastore = new Datastore();

  private Date getFutureDate(Date date, long durationFromNowInSeconds) {
    // convert date to calendar
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    // manipulate date
    c.add(Calendar.SECOND, (int)durationFromNowInSeconds);
    // convert calendar to date
    return c.getTime();
  }

  private List<Date> getAllRecurDates(Date startDate, Date endDate, long recurInterval) {
    Calendar c = Calendar.getInstance();
    List<Date> recurDates = new ArrayList<>();
    c.setTime(startDate);

    while (c.getTime().compareTo(endDate) < 0) {
      // manipulate date
      c.add(Calendar.SECOND, (int) recurInterval);
      // convert calendar to date
      System.out.println("Adding recur date: " + dateFormat.format(c.getTime()));
      recurDates.add(c.getTime());
    }
    return recurDates;
  }


  @Override
  public void start(Future<Void> startFuture) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    Datastore datastore = new Datastore();

    System.out.println("Starting server!");

    // User
    Route userPostRoute = router.route(HttpMethod.POST, "/user/:userid").produces("application/json");
    userPostRoute.handler(routingContext -> {
      String userId = routingContext.request().getParam("userid");
      User user = new User();
      System.out.println("Setting userId: " + userId);
      user.setUserId(userId);
      datastore.addUser(user);
      System.out.println("Sending back response: " + Json.encodePrettily(user));
      routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily(user));
    });


    // Set event
    Route eventPostRoute = router.route(HttpMethod.POST, "/event").produces("application/json");

    eventPostRoute.handler(routingContext -> {

      routingContext.request().bodyHandler(bodyHandler -> {
        final JsonObject json = bodyHandler.toJsonObject();
        Event event = new Event();

        // Create event object
        event.setUserId(json.getString("userId"));
        event.setCreateTime(new Date());
        event.setEventId(String.valueOf(UUID.randomUUID()));
        event.setDurationInSeconds(json.getLong("durationInSeconds"));
        event.setTitle(json.getString("title"));

        // Handle parse exceptions, return user error
        try {
          Date eventDate = dateFormat.parse(json.getString("eventTime"));
          event.setEventTime(eventDate);
        } catch (ParseException e) {
          e.printStackTrace();
        }

        // Check if recurring event or not!
        if (json.containsKey("recurringTimeInSeconds")) {
          event.setRecurringTimeInSeconds(json.getLong("recurringTimeInSeconds"));
          datastore.addRecurringEvent(event);
        } else {
          event.setRecurringTimeInSeconds(-1);
          datastore.addEvent(event);
        }

        System.out.println("Added event: " + event.getEventId() + ", for user: " + event.getUserId() + ": Recurring time: " + event.getRecurringTimeInSeconds());
        // Add event to user
        User user = datastore.getUser(event.getUserId());
        user.addEventId(event.getEventId());
      });

      routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily("Created event"));
    });


    // Get Event
    Route eventGetRoute = router.route(HttpMethod.GET, "/event/:userid").produces("application/json");
    eventGetRoute.handler(routingContext -> {

      // Fetch userId and duration param

      String userId = routingContext.request().getParam("userid");
      String durationStr = routingContext.request().getParam("duration");
      long duration = durationStr != null && !durationStr.trim().isEmpty() ?
        Long.valueOf(durationStr) : (2 * 365 * 24 * 60 * 60); // 2 years
      Date currentDate = new Date();

      // Get future date until which we want the event.
      Date futureDate = getFutureDate(currentDate, duration);

      System.out.println("Fetching events for userId: " + userId + ", duration: " + durationStr + ", currentDate: " + dateFormat.format(currentDate)
        + ", upto: " + dateFormat.format(futureDate));
      User user = datastore.getUser(userId);

      if (user != null) {
        // Get all events for the user
        List<String> userEvents = user.getEventIdList();

        // Fetch recurring and non-recurring user events.
        List<Event> nonRecurEvents = new ArrayList<>();
        List<Event> recurEvents = new ArrayList<>();

        // Fetch nonrecur events.
        for (String eventStr: userEvents) {
          Event event = datastore.getEvent(eventStr);
          if (event != null) {
            nonRecurEvents.add(event);
          }
        }

        // Fetch recur events.
        for (String eventStr: userEvents) {
          Event event = datastore.getRecurringEvent(eventStr);
          if (event != null) {
            recurEvents.add(event);
          }
        }

        List<Event> eventsWithinDuration = new ArrayList<>();

        // non recur events
        for (Event e : nonRecurEvents) {
          Date eventDate = e.getEventTime();
          System.out.println("Checking non-recur event: " + e.getEventId());

          // Event should be > current date and < future date.
          if (eventDate.compareTo(futureDate) < 0 && eventDate.compareTo(currentDate) > 0) {
            eventsWithinDuration.add(e);
          }
        }

        // recur events.
        for (Event e : recurEvents) {
          System.out.println("Checking recur event: " + e.getEventId());

          // Get all recurring events from event start to future date.
          List<Date> recurDates = getAllRecurDates(e.getEventTime(), futureDate, e.getRecurringTimeInSeconds());

          // Get all recurrences.
          if (recurDates != null && !recurDates.isEmpty()) {
            for (Date r : recurDates) {
              // Only add recur dates > current date
              if (r.compareTo(currentDate) > 0) {
                // Create a new event for it.
                Event newEvent = new Event(e.getTitle(), String.valueOf(UUID.randomUUID()),
                  userId, new Date(), r, e.getDurationInSeconds(), -1, null);
                eventsWithinDuration.add(newEvent);
              }
            }
          }
        }
        // Debug:
        for (Event e : eventsWithinDuration) {
          System.out.println("Events within duration: id: " + e.getEventId() + ", eventDate: " + dateFormat.format(e.getEventTime()) +
            ", duration: " + e.getDurationInSeconds());
        }
        routingContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(200)
          .end(Json.encodePrettily(eventsWithinDuration));
      }
    });

    server.requestHandler(router).listen(8080);
  }
}
