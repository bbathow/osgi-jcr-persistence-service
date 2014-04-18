osgi-jcr-persistence-service
============================

A simple OSGi service that provides data persistence using JCR/Jackrabbit

## Background

I was using Apache Felix in the bridged mode[0] to embed an OSGi runtime in a Tomcat6 web container.

I was investigating ways to provide a JCR-like abstraction over a RDBMS persistence layer (Oracle). I stumbled across Apache Sling and Jackrabbit and was wondering if it was possible to:

 *   use only the JCR and JackRabbit bundles from Sling (and skip the others)
 *   configure JackRabbit to use Oracle for persistence

The main thread can be found at [1]

[0] http://felix.apache.org/documentation/subprojects/apache-felix-http-service.html#using-the-servlet-bridge.

[1] http://apache-sling.73963.n3.nabble.com/JCR-JackRabbit-Oracle-td4032341.html

## Code Layout

This is a standard maven project that creates an OSGi bundle. 

All jackrabbit-core dependencies (`commons-*`, `concurrent`, `jackrabbit-*`, `lucene-core`, `tika-core`) and `ojdbc6` are embedded in this OSGi bundle. The `bundles/` directory contains JCR API bundles that you can install in your OSGi runtime. 
A sample `repository.xml` is provided that connects to Oracle and also supports [Jackrabbit Clustering](http://wiki.apache.org/jackrabbit/Clustering). 

## Sample usage

```
        Session session = null;
        try {
            session = persistenceService.getDefaultWorkspace();
            Node root = session.getRootNode();

            Long now = System.currentTimeMillis();
            Node hello;
            if (!root.hasNode("hello")) {
                System.out.println("creating new node");
                hello = root.addNode("hello");
            } else {
                hello = root.getNode("hello");
            }
            
            Node world = hello.addNode("world" + now);
            world.setProperty("message", "Hello, World! @ " + new Date(now).toString());
            // save! 
            session.save();

            // Retrieve content 
            Node node = root.getNode("hello");
            NodeIterator it = node.getNodes();
            resp.getWriter().println("Listing Nodes");
            int count = 0;
            while (it.hasNext()) {
                Node child = it.nextNode();
                System.out.print(++count + ". " + child.getIdentifier() + " " + child.getPath() + ": ");
                System.out.println(child.getProperty("message").getString());
            }
        } finally {
            if (session != null) {
                session.logout();
            }
        }
```
