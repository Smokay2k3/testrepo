package hello.events.handlers;

import hello.events.CustomerCreatedEvent;
import hello.events.CustomerUpdateEvent;
import hello.models.Customer;
import hello.repositories.CustomerRepository;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.replay.ReplayAware;

public class CustomerRepoPopulationEventHandler implements ReplayAware {
 
    //Need to find out how to get this in with spring
    private CustomerRepository customerRepo;
    
    @EventHandler
    public void on(CustomerCreatedEvent event) {
       customerRepo.save(new Customer(
                event.getCustomerId(),
                event.getFirstName(),
                event.getSurname()));
    }

    @EventHandler
    public void on(CustomerUpdateEvent event) {
        Customer customer = customerRepo.findOne(event.getCustomerId());
        
        if(customer == null){
            //Should scream blue murder as there should have never been an update even without a create event before
            return;
        }
        
        if(event.getFirstName() != null){
            customer.setFirstName(event.getFirstName());
        }
        
        if(event.getSurname() != null){
            customer.setLastName(event.getSurname());
        }
        
        customerRepo.save(customer);
    }

    @Override
    public void beforeReplay() {
       System.out.println("beforeReplay");  
       
       //Nuke the latest versions of customers in the repo.
       //Danger, only once, otherwise new events could be lost.     
       customerRepo.deleteAll();
    }

    @Override
    public void afterReplay() {
        System.out.println("afterReplay");
    }

    @Override
    public void onReplayFailed(Throwable cause) {
        System.out.println("onReplayFailed" + cause);  
    }

    public void setCustomerRepo(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }
}
