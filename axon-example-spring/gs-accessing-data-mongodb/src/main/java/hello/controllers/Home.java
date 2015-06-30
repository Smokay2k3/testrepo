package hello.controllers;

import hello.commands.CreateCustomerCommand;
import hello.commands.UpdateCustomerCommand;
import hello.models.Customer;
import hello.repositories.CustomerRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Home {
         
    private static final String TEMPLATE_HOME = "home";
    
    private static final String TEMPLATE_REDIRECT_HOME = "redirect:/";
    
    @Autowired
    CustomerRepository customerRepo;
    
    @Autowired
    CommandGateway commandGateway;

    @RequestMapping("/")
    public String home(Model model) throws IOException {  
        model.addAttribute("customers", customerRepo.findAll());
        return TEMPLATE_HOME;
    }
    
    @RequestMapping("/loadTestCustomers")
    public String loadTestData(){
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        
        commandGateway.send(new CreateCustomerCommand(uuid1, "James", "King"));
        commandGateway.send(new CreateCustomerCommand(uuid2, "Billy", "Test"));
        
        commandGateway.send(new UpdateCustomerCommand(uuid2, null, "Jamerson"));
        
        
        return TEMPLATE_REDIRECT_HOME;
    }
    
    @RequestMapping(value="/addNewCustomer", method=RequestMethod.POST)
    public String createNewCustomer(@RequestParam String id, @RequestParam String firstName, @RequestParam String lastName, Model model) {
        commandGateway.send(new CreateCustomerCommand(id, firstName, lastName));
        
        return TEMPLATE_REDIRECT_HOME;
    }

    @RequestMapping(value="/updateCustomer", method=RequestMethod.POST)
    public String updateCustomer(@RequestParam String id, @RequestParam String firstName, @RequestParam String lastName, Model model) {
        commandGateway.send(new UpdateCustomerCommand(id, firstName, lastName));
        
        return TEMPLATE_REDIRECT_HOME;
    }
    
}
