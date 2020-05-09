package com.dansoftware.libraryapp.gui.notification;


import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationTest {

    private final DummyNotificationStrategy strategy = new DummyNotificationStrategy();

    public NotificationTest() {
        Notification.setStrategy(strategy);
    }

    @Test
    public void test() {
        IntStream.range(0, 10);
             //   .forEach(i -> assertEquals(new Notification(NotificationLevel.ERROR, "msg"), strategy.getHandledNotification()));
    }

}
