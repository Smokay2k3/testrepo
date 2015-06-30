package hello.commands;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

public class UpdateCustomerCommand {

    @TargetAggregateIdentifier
    private final String customerId;
    
    private final String firstName;
    private final String surname;
       
    public UpdateCustomerCommand(String customerId, String firstName, String surname) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }
}
