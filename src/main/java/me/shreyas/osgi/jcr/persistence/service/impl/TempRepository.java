package me.shreyas.osgi.jcr.persistence.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import org.apache.log4j.Logger;

/**
 * Encapsulates the creation and deletion of a temp directory that is used to
 * house the repository configuration files that JackRabbit needs.
 *
 * @author shreyasdube
 */
public class TempRepository {

    private static final Logger LOG = Logger.getLogger(TempRepository.class);
    private static final String REPOSITORY_CONFIG = "repository.xml";

    private File repoHome;
    private Repository repository;

    public TempRepository() {
    }

    public TempRepository create() throws IOException {
        log("Creating JackRabbit repository home ... ");
        try {
            initClusterId();

            // create temp directory to store JackRabbit's config files
            repoHome = Files.createTempDirectory("jackrabbit-repository_").toFile();
            log("Created JackRabbit repository home at: " + repoHome.getAbsolutePath());

            // copy repository file over
            Files.copy(getClass().getResourceAsStream("/" + REPOSITORY_CONFIG),
                    new File(repoHome, REPOSITORY_CONFIG).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            log("Copied JackRabbit repository configuration");
        } catch (IOException ex) {
            System.out.println("FATAL: " + ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
        return this;
    }

    public Repository getRepository() throws RepositoryException {
        if (repository == null) {
            // create
            log("Initializing new JCR repository at: " + getHome());
            Map<String, String> params = new HashMap<>();
            params.put("org.apache.jackrabbit.repository.home", getHome().getPath());

            for (RepositoryFactory factory
                    : ServiceLoader.load(RepositoryFactory.class,
                            // need to specify the current classLoader otherwise 
                            // serviceLoader does not work; 
                            getClass().getClassLoader())) {
                repository = factory.getRepository(params);
                if (repository != null) {
                    // found it!
                    break;
                }
            }
            log("ServiceLoader created Repository: " + repository);
        }

        return repository;
    }

    public void cleanup() throws FileNotFoundException {
        delete(repoHome);
    }

    /**
     * By default File#delete fails for non-empty directories, it works like
     * "rm". We need something a little more brutual - this does the equivalent
     * of "rm -r"
     *
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     */
    private boolean delete(File path) throws FileNotFoundException {
        if (!path.exists()) {
            throw new FileNotFoundException(path.getAbsolutePath());
        }
        log("Deleting " + path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                ret = ret && delete(f);
            }
        }
        return ret && path.delete();
    }

    private void initClusterId() throws SocketException, UnknownHostException {
        // get all network interfaces
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        byte[] mac = null;
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.getHardwareAddress() != null) {
                // the non localhost ones will have a MAC address
                mac = ni.getHardwareAddress();
                break;
            }
        }

        if (mac == null) {
            throw new UnknownHostException("Could not lookup MAC address for this host");
        }

        // format the MAC address
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        final String macAddress = sb.toString();
        // set this cluster's id to its mac address
        System.setProperty("org.apache.jackrabbit.core.cluster.node_id", macAddress);
        log("macAddress: " + macAddress);
    }

    private void log(String msg) {
        System.out.println("[ml-persistence-service] " + msg);
        LOG.info(msg);
    }

    public File getHome() {
        return repoHome;
    }
}
