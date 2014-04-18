package me.shreyas.osgi.jcr.persistence.service;

/**
 *
 * @author shreyasdube
 */
public class RepositoryUnavailableException extends Exception {

    /**
     * Creates a new instance of <code>RepositoryUnavailableException</code>
     * without detail message.
     */
    public RepositoryUnavailableException() {
    }

    /**
     * Constructs an instance of <code>RepositoryUnavailableException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public RepositoryUnavailableException(String msg) {
        super(msg);
    }

    public RepositoryUnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
