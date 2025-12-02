package ru.example.hotel.rest.client;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.example.hotel.api.service.HotelServiceRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Продюсер для получения Remote EJB из WildFly
 */
@ApplicationScoped
public class EjbClientProducer {

    private static final Logger LOG = Logger.getLogger(EjbClientProducer.class.getName());

    @ConfigProperty(name = "wildfly.host", defaultValue = "localhost")
    String wildflyHost;

    @ConfigProperty(name = "wildfly.port", defaultValue = "8080")
    String wildflyPort;

    @ConfigProperty(name = "wildfly.username")
    Optional<String> wildflyUsername;

    @ConfigProperty(name = "wildfly.password")
    Optional<String> wildflyPassword;

    private Context context;

    @PostConstruct
    void init() {
        try {
            // Load wildfly-config.xml for authentication
            URL configUrl = getClass().getClassLoader().getResource("wildfly-config.xml");
            if (configUrl != null) {
                System.setProperty("wildfly.config.url", configUrl.toString());
                LOG.info("Loaded wildfly-config.xml from: " + configUrl);
            }

            Hashtable<String, Object> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            env.put(Context.PROVIDER_URL, "remote+http://" + wildflyHost + ":" + wildflyPort);

            // Enable EJB context
            env.put("jboss.naming.client.ejb.context", true);

            context = new InitialContext(env);
            LOG.info("EJB Context initialized successfully for " + wildflyHost + ":" + wildflyPort);
        } catch (NamingException e) {
            LOG.log(Level.SEVERE, "Failed to initialize EJB context", e);
            throw new RuntimeException("Failed to initialize EJB context", e);
        }
    }

    @PreDestroy
    void cleanup() {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                LOG.log(Level.WARNING, "Error closing context", e);
            }
        }
    }

    @Produces
    @ApplicationScoped
    public HotelServiceRemote produceHotelService() {
        try {
            // JNDI lookup для Remote EJB
            // Формат: ejb:<app-name>/<module-name>/<bean-name>!<interface-name>
            String jndiName = "ejb:hotel-app/hotel-ejb/HotelServiceBean!" +
                    HotelServiceRemote.class.getName();

            LOG.info("Looking up EJB: " + jndiName);

            HotelServiceRemote service = (HotelServiceRemote) context.lookup(jndiName);
            LOG.info("Successfully obtained HotelServiceRemote EJB");

            return service;
        } catch (NamingException e) {
            LOG.log(Level.SEVERE, "Failed to lookup HotelServiceRemote", e);
            throw new RuntimeException("Failed to lookup HotelServiceRemote", e);
        }
    }
}