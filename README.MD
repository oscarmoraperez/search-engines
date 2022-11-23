# Search engines

This maven-based project contains a demo project on how to interact with an Apache SOLR instance for the storage of ebooks. 

0) To run the project one have the following options:

- mvn test. Compiles and runs the unit tests.
- mvn verify. Compiles, runs all the test and generates the following reports: jacoco coverage

1) To make use of the application, follow the next steps:

- Launch SOLR instance using the docker-compose file in the docker folder
  `docker-compose up -d`
- Check that SOLR instance is up and running by browsing: http://localhost:8983/solr/#/
- Launch the SpringBoot application by running the entry point: `App.java`
- Browse the Swagger page to interact with the different
  endpoints: http://localhost:8080/swagger-ui/index.html#/search-controller

2) The Swagger page provides the following endpoints where the homework is implemented:

- `GET /api/v1/book/{id}`. Retrieves a book based on the unique id
- `POST /api/v1/book/populate`. Repopulates (deletes all first) solr instance with a new set of books (from a set of
  epub files locally saved in the resources folder)
- `POST /api/v1/book/search`. Returns a set of books based on the SearchRequest parameters
- `GET /api/v1/book/suggest`. Returns a set of suggestions based on the request handler and search component of the solr
  instance.

For the last point, the SOLR instance must be properly configured. The solrconfig.xml should contain the request handler
and the search component configuration. This has been already configured in the docker-compose.yaml and solrconfig.xml contained in the
`/docker` folder as a part of the last exercise.

3) Extra addons:

- The code is documented, via Javadoc and SpringFox.
- The code is covered by:
    - Unit tests: using mockito to mock all the external dependencies.
    - Integration tests: using @SpringBootTest and test containers to set up a SOLR instance on the fly during the
      integration tests execution  