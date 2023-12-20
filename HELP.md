# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/native-image.html#native-image)
* [Distributed Tracing Reference Guide](https://micrometer.io/docs/tracing)
* [Getting Started with Distributed Tracing](https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/actuator.html#actuator.micrometer-tracing.getting-started)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#using.devtools)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#appendix.configuration-metadata.annotation-processor)
* [Docker Compose Support](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#features.docker-compose)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#web.reactive)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#data.sql.r2dbc)
* [Spring Data Reactive MongoDB](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#data.nosql.mongodb)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#actuator)
* [Prometheus](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#actuator.metrics.export.prometheus)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links

These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/htmlsingle/#aot)
* [R2DBC Homepage](https://r2dbc.io)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* mongodb: [`mongo:latest`](https://hub.docker.com/_/mongo)

Please review the tags of the used images and set them to the same as you're running in production.

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks

If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run --rm mongoreactive:0.0.1-SNAPSHOT
```

### Executable with Native Build Tools

Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:

```
$ target/mongoreactive
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./mvnw test -PnativeTest
```

## Missing R2DBC Driver

Make sure to include a [R2DBC Driver](https://r2dbc.io/drivers/) to connect to your database.