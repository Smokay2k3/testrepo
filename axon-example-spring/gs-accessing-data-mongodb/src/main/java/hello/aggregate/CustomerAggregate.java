package hello.aggregate;

import hello.commands.CreateCustomerCommand;
import hello.commands.UpdateCustomerCommand;
import hello.events.CustomerCreatedEvent;
import hello.events.CustomerUpdateEvent;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.stereotype.Component;

@Component
public class CustomerAggregate extends AbstractAnnotatedAggregateRoot<String>{
    private static final long serialVersionUID = 20150622001L;
    
    @AggregateIdentifier
    private String id; 
    
    public CustomerAggregate() {}

    @CommandHandler
    public CustomerAggregate(CreateCustomerCommand command) {
        apply(new CustomerCreatedEvent(command.getCustomerId(), command.getFirstName(), command.getSurname()));
    }
    
    @CommandHandler
    public void update(UpdateCustomerCommand command) {
        apply(new CustomerUpdateEvent(id, command.getFirstName(), command.getSurname()));  
    }
    
    @EventHandler
    public void handle(CustomerCreatedEvent event) {
        this.id = event.getCustomerId();
    }
    
    @EventHandler
    public void handle(CustomerUpdateEvent event) {
        System.out.println("Update customer with event " + event);
    }
}
