package me.shreyas.osgi.jcr.persistence.service.impl;

import java.util.Arrays;
import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import me.shreyas.osgi.jcr.persistence.service.PersistenceService;
import me.shreyas.osgi.jcr.persistence.service.RepositoryUnavailableException;
import org.osgi.framework.BundleContext;

/**
 *
 * @author shreyasdube
 */
public class PersistenceServiceImpl implements PersistenceService {

    private final BundleContext bundleContext;
    private final Repository repository;

    public PersistenceServiceImpl(BundleContext bundleContext, final Repository repository) {
        this.bundleContext = bundleContext;
        this.repository = repository;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public Session getDefaultWorkspace() throws RepositoryUnavailableException {
        return getWorkspace(null);
    }

    @Override
    public Session getWorkspace(final String workspaceName) throws RepositoryUnavailableException {
        final Repository r = getRepository();
        final Credentials credz = new SimpleCredentials("foo", "bar".toCharArray());
        final Session defaultSession;
        // Note that a Session instance is not guaranteed to be thread-safe so 
        // you should start multiple sessions if you need to access repository 
        // content simultaneously from different threads. This is especially 
        // important for things like web applications.
        try {
            defaultSession = r.login(credz);

            if (workspaceName == null || workspaceName.trim().isEmpty()) {
                return defaultSession;
            } else {
                try {
                    // create new workspace if required
                    createWorkSpaceIfRequired(defaultSession, workspaceName.trim());
                    // return session to this workspace
                    return r.login(credz, workspaceName.trim());
                } finally {
                    // logout from default session
                    defaultSession.logout();
                }
            }
        } catch (RepositoryException ex) {
            throw new RepositoryUnavailableException(ex.getMessage(), ex);
        }
    }

    private void createWorkSpaceIfRequired(Session s, String workspaceName) throws RepositoryException {
        if (!Arrays.asList(s.getWorkspace().getAccessibleWorkspaceNames()).contains(workspaceName)) {
            System.out.println("creating new workspace - " + workspaceName);
            s.getWorkspace().createWorkspace(workspaceName);
        } else {
            System.out.println("workspace already exists - " + workspaceName);
        }
    }

}
