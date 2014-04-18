package me.shreyas.osgi.jcr.persistence.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import me.shreyas.osgi.jcr.persistence.service.PersistenceService;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author shreyasdube
 */
public class PersistenceServiceFactory implements ServiceFactory<PersistenceService> {

    private TempRepository tempRepository;
    private Repository repository;
    private boolean init = false;
    private String failureCause = null;

    /**
     * Called by Apache Felix Dependency Manager - init -> start -> stop ->
     * destroy. Initializes the temp directory for JackRabbit's configuration
     * files
     */
    public void init() {
        System.out.println(PersistenceServiceFactory.class.getName() + ".init()");
        try {
            tempRepository = new TempRepository().create();
            repository = tempRepository.getRepository();
            init = true;
        } catch (IOException | RepositoryException ex) {
            failureCause = ex.getMessage();
            System.err.println("FATAL: " + ex.getMessage());
        }
    }

    /**
     * Called by Apache Felix Dependency Manager - init -> start -> stop ->
     * destroy. Recursively deletes the temp directory for JackRabbit's
     * configuration files
     */
    public void destroy() {
        System.out.println(PersistenceServiceFactory.class.getName() + ".destroy()");
        if (tempRepository != null) {
            try {
                tempRepository.cleanup();
            } catch (FileNotFoundException ex) {
                // ignore
                System.err.println("WARN: " + ex.getMessage());
            }
        }
    }

    @Override
    public PersistenceService getService(Bundle bundle, ServiceRegistration<PersistenceService> sr) {
        if (init) {
            // create new instance per bundle
            return new PersistenceServiceImpl(bundle.getBundleContext(), repository);
        } else {
            throw new IllegalStateException("PersistenceService was not initialized correctly: "
                    + failureCause);
        }
    }

    @Override
    public void ungetService(Bundle bundle, ServiceRegistration<PersistenceService> sr, PersistenceService s) {
        // TODO
        System.out.println("Not supported yet");
    }

}
