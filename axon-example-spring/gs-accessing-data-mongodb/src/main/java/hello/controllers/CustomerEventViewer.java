package hello.controllers;

import hello.aggregate.CustomerAggregate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerEventViewer {
         
    private static final String TEMPLATE_CUST_EVENTS = "";
    
    @Autowired
    EventStore eventStore;

    @RequestMapping(value="/customer-events", method=RequestMethod.POST)
    public String home(Model model, @RequestParam String id) throws IOException {  
        
        DomainEventStream des = eventStore.readEvents(CustomerAggregate.class.getSimpleName(), id);
        List<DomainEventMessage> dems = new ArrayList<DomainEventMessage>();
        
        while(des.hasNext()){
            DomainEventMessage dem = des.next();
  
            dems.add(des.next());
        }
        
        model.addAttribute("eventmessages", dems);
        
        return "customer-events";
    }
    
  
}
