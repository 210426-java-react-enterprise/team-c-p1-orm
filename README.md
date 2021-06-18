# Project 1 - Custom Object Relational Mapping Framework

## Description


Custom object-relational mapping (ORM) framework. This framework allows for simplified and SQL-free interaction with the relational data source through a series of annotations. Each annotation represents a structure that allows building the SQL statement to interact with the database. This ORM was developed to be a Maven dependency to be included in an API that exposes endpoints that call the CRUD functionality (for the API please check the following repository https://github.com/210426-java-react-enterprise/team-c-p1-webapp/tree/ozzy-dev).  


## Technologies Used
- [X] Java 8
- [X] Apache Maven 4.0.0
- [X] Java EE Servlet API (v4.0+)
- [X] PostGreSQL deployed on AWS RDS
- [X] DBevear 21.0.4
- [X] AWS CodeBuild
- [X] AWS CodePipeline
- [X] Git SCM (on GitHub)

## Features

- Bring object data from the database.
- Update object data from database.
- Save object data in database.
- Delete object in database.

## Getting Started

To be able to use this ORM,  please following the next steps::  

- The dependency must be deployed locally in the maven repository through the terminal command >>> mvn clean package.

- To use the Object Service class, the method getInstance must be called first (Object Service is a singleton class).

- The methods from the class ObjectService that expose the ORM functionally are the following:

  - sendObjectToDB > Object as input
  - updateObjectInDB > Object as input
  - deleteObjectInDB > Object as input
  - bringObjectFromDbByField > Object class as input, object field as input, object field value as input
