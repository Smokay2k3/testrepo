package hello.events.handlers;

import hello.events.CustomerCreatedEvent;
import hello.events.CustomerUpdateEvent;

import org.axonframework.eventhandling.annotation.EventHandler;

public class CustomerReportingEventHandler {

    @EventHandler
    public void on(CustomerCreatedEvent event) {
        System.out.println("Customer created event handler triggered for: " + event);
    }

    @EventHandler
    public void on(CustomerUpdateEvent event) {
        System.out.println("Customer update event handler triggered for: " + event);
    }
}
