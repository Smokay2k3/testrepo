package hello.controllers;

import hello.aggregate.CustomerAggregate;
import hello.commands.CreateCustomerCommand;
import hello.commands.UpdateCustomerCommand;
import hello.models.Customer;

import java.io.IOException;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.serializer.SerializedDomainEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UpdateCustomerES {

    @Autowired
    CommandGateway commandGateway;
    
    @Autowired
    MongoEventStore eventStore;
    
    @Autowired
    MongoTemplate mt;
    
    public static final String CUST_ID = UUID.randomUUID().toString();

    @RequestMapping("/update")
    public String home() throws IOException {

        commandGateway.send(new UpdateCustomerCommand("0a831cb4-2418-4872-a67b-dae5e91e3153", "James", "Malloy"));
        
        return "update";
    }

    @RequestMapping("/update/result")
    public String result() throws IOException {

        DomainEventStream dms = eventStore.readEvents(CustomerAggregate.class.getSimpleName(), CUST_ID);
        
        System.out.println("*****************************");
        while(dms.hasNext()){
            DomainEventMessage dem =  dms.next();
            System.out.println(dem.getClass().getSimpleName() + dem.getPayload());
        }
        
       
        
        return "update";
    }
    
    
    
    @RequestMapping("/update/testmt")
    public String testmt() throws IOException {

        System.out.println(mt.findAll(Customer.class));
       
        
        return "update";
    }
    
   
}
