package edu.ucsd.cse110.mainpage.notification;

import edu.ucsd.cse110.mainpage.Factory;

public class NotificationServiceFactory extends Factory<NotificationService> {
    private static NotificationServiceFactory instance;

    public static NotificationServiceFactory getInstance() {
        if (instance == null) {
            instance = new NotificationServiceFactory();
        }
        return instance;
    }
}
