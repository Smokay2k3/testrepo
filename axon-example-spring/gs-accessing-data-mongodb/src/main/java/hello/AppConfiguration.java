package hello;

import hello.aggregate.CustomerAggregate;
import hello.events.handlers.CustomerRepoPopulationEventHandler;
import hello.events.handlers.CustomerReportingEventHandler;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.eventhandling.replay.BackloggingIncomingMessageHandler;
import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.eventstore.mongo.MongoTemplate;
import org.axonframework.unitofwork.NoTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.Mongo;

@Configuration
public class AppConfiguration {

    public static final String DEFAULT_CLUSTER_NAME = "DEFAULT_CLUSTER";
    
    //Provided by spring as we have mongo data
    @Autowired
    Mongo mongo;

    @Bean
    public MongoEventStore eventStore(){
        return new MongoEventStore(mongoTemplate());      
    }
    
    @Bean(name="axonMongoTemplate")
    @ConditionalOnClass(Mongo.class)
    public MongoTemplate mongoTemplate(){
        return new DefaultMongoTemplate(mongo);
    }
    
    @Bean
    public EventSourcingRepository<CustomerAggregate> customerEventRepo(){
        EventSourcingRepository<CustomerAggregate> customerRepo = new EventSourcingRepository<CustomerAggregate>(CustomerAggregate.class, eventStore());
        customerRepo.setEventBus(clusteringEventBus());
        AggregateAnnotationCommandHandler.subscribe(CustomerAggregate.class, customerRepo, commandBus());
        
        return customerRepo;
    }
    
    @Bean
    public CommandBus commandBus() {
        SimpleCommandBus commandBus = new SimpleCommandBus();
        return commandBus;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandGateway commandGateway() {
        return new DefaultCommandGateway(commandBus());
    }

   /* @Bean  
    public EventBus eventBus() {
        EventBus eventBus = new SimpleEventBus();   
        eventBus.subscribe(new AnnotationEventListenerAdapter(customerEventHandler()));
        return eventBus;
    }*/
    
   /*
    * - Config replay to recreate events so that readEvents returns laters
    * - Configure snapshot
    *
    */
    
    
    /**
     * This allows events to be replayed onto the default cluster and
     * handled by the normal event handlers.  This may be good to populate a 
     * JPA repo so that people can be retrieved easily by their details and then
     * another mechanism for looking at a single person at a given timestamp/
     * @return
     */
    @Bean
    public ReplayingCluster replayingCluster(){
        ReplayingCluster rc = new ReplayingCluster(
                defaultCluster(), 
                eventStore(), 
                new NoTransactionManager(), 
                2, 
                new BackloggingIncomingMessageHandler());
        
        rc.subscribe(new AnnotationEventListenerAdapter(customerRepoPopulationEventHandler()));
        
        return rc;
    }
    
    @Bean
    public CustomerRepoPopulationEventHandler customerRepoPopulationEventHandler(){
        return new CustomerRepoPopulationEventHandler();
    }
    
    
    @Bean
    public Cluster defaultCluster(){
        return new SimpleCluster(DEFAULT_CLUSTER_NAME);
    }
    
    @Bean 
    public ClusterSelector clusterSelector(){
        return new DefaultClusterSelector(defaultCluster());  
    }
    
    @Bean
    public ClusteringEventBus clusteringEventBus() {    
        ClusteringEventBus eventBus = new ClusteringEventBus(clusterSelector());
        return eventBus;
    }
  
    @Bean
    public CustomerReportingEventHandler customerEventHandler(){
        return new CustomerReportingEventHandler();
    }

    @Bean
    //@ConditionalOnClass(PersonRepository.class)
    public AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor() {
        AnnotationCommandHandlerBeanPostProcessor p =  new AnnotationCommandHandlerBeanPostProcessor();
        p.setCommandBus(commandBus());
        return p;
    }

    @Bean
    //@ConditionalOnClass(PersonRepository.class)
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor() {
        AnnotationEventListenerBeanPostProcessor p = new AnnotationEventListenerBeanPostProcessor();
        p.setEventBus(clusteringEventBus());
        return p;
    }
    
}
