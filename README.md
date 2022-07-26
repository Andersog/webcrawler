# Webcrawler

This project performs very simple webcrawling. 
On startup, provide a base domain and it will crawl that page for links, recursively checking each that it find and outputting to console. 
Pages which are off domain will be ignored and those which appear multiple times on the same page are only printed once. 

## Setup
 
The project is written in java 8 and uses maven for dependency management, so to get things running
 
- Install your favourite 8+ JDK e.g. [Liberica](https://bell-sw.com/pages/downloads/#mn)
- Install and setup [Maven](https://maven.apache.org/install.html)
- Checkout the and navigate to the source directory and run 
```
mvn package
mvn exec:java
```


Once the app is running, the console will provide further instructions.

## Approach

The program uses a simple breadth-first approach to identify links and visit them.
As this tasked was time-boxed to < 4 hours, I decided that I would prioritise testing the functionality of:  

- scraping html for links which match a provided domain
- tracking pages which have been visited, and which to visit next 

Once I was confident that this functionality was working, the logic required to connect things should be relatively simple and could be covered by some 
E2E style testing.

A couple of compromises and limitations were made:
- The crawler will only pick up links which are on the html, any dynamically generated content will not be read.  
- It's possible we could improve performance if we enabled some multi-threading on the Webcrawler, but I chose to keep the service small in scope and simple to be more confident of the functionality. 
