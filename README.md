# ucmdb-restapi-get-ci
This project provides information on how to use UCMDB’s REST API and illustrates the usage by means of an exemplary Java Console Application. 

   File name | File content
----------------|----------------------
RestClient.java| Class RestClient communicating with UCMDB REST API. The RestClient class provides methods to send http requests to the API and to receive the response.
Controller.java  | Class Controller manages business flow. Controller provides main class and is responsible for managing the main data flow of the application. 
CI.java  | Class represents Configuration Item from ucmdb call result. Configuration Items are used to format results from  via REST API.

----

## Requirements

* The REST API works with UCMDB Server 10.10 or higher versions 
* Java 1.8.0_231
* Apache Maven
