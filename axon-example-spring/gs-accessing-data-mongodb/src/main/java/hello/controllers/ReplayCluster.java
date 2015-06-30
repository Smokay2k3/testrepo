package hello.controllers;

import hello.events.handlers.CustomerRepoPopulationEventHandler;
import hello.models.Customer;
import hello.repositories.CustomerRepository;

import java.io.IOException;
import java.util.List;

import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.axonframework.eventstore.mongo.MongoTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReplayCluster {
    
    private static final boolean EVENT_DELETION_ENABLED = false;
    
    @Autowired
    ReplayingCluster recplayingCluster;
    
    @Autowired
    CustomerRepoPopulationEventHandler customerRepoPopulationEventHandler;
    
    @Autowired
    CustomerRepository customerRepo;
    
    @Autowired
    @Qualifier(value="axonMongoTemplate")
    MongoTemplate axonMongoTemplate; 
    
    @RequestMapping("/replay")
    public String replay(Model model) throws IOException {    
        //Just before we start the replay make sure than handler has the customer repo
        //Crappy and disconjoint... needs more thinking about
        customerRepoPopulationEventHandler.setCustomerRepo(customerRepo);
        
        //Replay the events that are in the eventstore to allow easy and flexable access to the customer.
        //The hanglers should readd them to the repo.
        //This means double data, but allows access to the latest version of the customer by multiple attributes,
        //Instead of having to know the id.
        recplayingCluster.startReplay();
        
        //The replay is finished now lets print out our final customer states
        System.out.println("");
        System.out.println("********** Customer repo has the following customers ***********");
        List<Customer> customers = customerRepo.findAll();
        System.out.println("Event count =" + customers.size());
        
        for(Customer customer : customers){
            System.out.println(customer);
        }

        System.out.println("****************************************************************");
        
        return "replay";
    }

    @RequestMapping("/delete")
    public String delete(Model model) throws IOException {       
        if(EVENT_DELETION_ENABLED){                   
            axonMongoTemplate.domainEventCollection().drop();
            axonMongoTemplate.snapshotEventCollection().drop();
        } else {
            System.out.println("Currently deletion of the event database is diabled");
        }
        
        return "replay";
    }
   
    
    
}
