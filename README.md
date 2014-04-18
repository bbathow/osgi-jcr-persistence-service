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
