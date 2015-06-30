package hello.events;

//Add interface to both this and cust update
public class CustomerCreatedEvent {

    private final String customerId;
    private final String firstName;
    private final String surname;
       
    public CustomerCreatedEvent(String customerId, String firstName, String surname) {
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

    @Override
    public String toString() {
        return "CustomerCreatedEvent [customerId=" + customerId + ", firstName=" + firstName + ", surname=" + surname + "]";
    }
}
