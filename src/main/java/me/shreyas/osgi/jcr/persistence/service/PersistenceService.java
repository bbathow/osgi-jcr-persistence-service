package me.shreyas.osgi.jcr.persistence.service;

import javax.jcr.Repository;
import javax.jcr.Session;

/**
 *
 * @author shreyasdube
 */
public interface PersistenceService {

    Repository getRepository();

    Session getDefaultWorkspace() throws RepositoryUnavailableException;

    Session getWorkspace(String workspaceName) throws RepositoryUnavailableException;
}
