[![Build Status](https://travis-ci.org/Gearon/JavaCrawler4Pexels.svg?branch=master)](https://travis-ci.org/Gearon/JavaCrawler4Pexels)

## Introduction
[Pexels][1] is a high quality photos website and photos on this site are free to use for even commercial usage. 
This application is a web crawler to dump photos from Pexels to local disk with specified topics and sharpness.


## Usage with CLI
 ```
 -folder <arg>      Specify the folder to store images 
 -sharpness <arg>   Specify the sharpness of crawled images. Can be 'thumbnail', 'clearer', 'pefect' and 'clearest' 
 -topics            Specify a list of topic to be searched 
 ```
 
## Example

  ```java -jar Crawler4Pexels-1.0-SNAPSHOT-jar-with-dependencies.jar -sharpness clearest -topics sky flowers clock```

## Build
  You can build JavaCrawler4Pexels using maven with below command
  ```mvn clean install```
  

  [1]: https://www.pexels.com

## Todo
  1. Crawl popular topic tags to dump photos automatically
  2. Add database support
