package me.shreyas.osgi.jcr.persistence.service.impl;

import me.shreyas.osgi.jcr.persistence.service.PersistenceService;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

/**
 *
 * @author shreyasdube
 */
public class Activator extends DependencyActivatorBase {

    private static final Logger LOG = Logger.getLogger(Activator.class);
    private Component persistenceComponent;

    @Override
    public void destroy(final BundleContext context, final DependencyManager manager)
            throws Exception {
        log("Deactivating " + context.getBundle().getSymbolicName());
        if (persistenceComponent != null) {
            manager.remove(persistenceComponent);
        }
    }

    @Override
    public void init(final BundleContext context, final DependencyManager manager)
            throws Exception {
        log("Activating " + context.getBundle().getSymbolicName());

        persistenceComponent = createComponent()
                .setInterface(PersistenceService.class.getName(), null)
                .setImplementation(PersistenceServiceFactory.class);
        manager.add(persistenceComponent);
    }

    private void log(final String msg) {
        System.out.println(msg);
        LOG.info(msg);
    }
}
