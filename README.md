# ucmdb-restapi-get-ci
This project provides information on how to use UCMDB REST API and performs the use by means of Java Console Application. It provides calls to the two endpoints below.
* <base_url>/rest-api/getCI
* <base_url>/rest-api/executeTQL (in-progress)

---

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

### Clone

- Clone this repo to your local machine using `https://github.com/bburak/ucmdb-restapi-get-ci.git`

### Setup

> If you want to run this project in your environment:

- Make sure the Maven dependencies are installed.
- Open Eclipse's embedded terminal.
- And then Execute this command line by line.

```shell
$ mvn clean compile assembly:single
$ java -cp target/RestAPI_UCMDB-0.0.1-SNAPSHOT-jar-with-dependencies.jar tr.com.controller.Controller
```
