## language_detector
### Cathal Butler | G00346889 | Final Year Software Development
An Asynchronous Language Detection System that builds a language database on the WiLI benchmark dataset. Multiple users 
can query a language by inputting their own string. 

Find project spec [here](https://github.com/butlawr/language_detector/blob/master/oodppAssignment2019.pdf)

### Environment Setups
Environment setup can be done in two ways, ether install [Tomcat](https://tomcat.apache.org/download-80.cgi) or use a [Docker](https://www.docker.com/) container with tomcat.

Place the `wili-2018-Edited.txt` in a directory called `data/` @ the root of your filesystem.

Linux `/data`
Windows `C:/data`

### How to run with Docker

`docker run -p 8888:8080`

* To mount the volume with the ngrams.war amend the command with the the path were you downloaded the .war file too
`-v [download location]/ngrams.war:/usr/local/tomcat/webapps/ngrams.war`
* Container name:
`tomcat:9.0.30-jdk8-openjdk`

Navigate to `localhost:8888/ngrams`

### How to run with Tomcat
Navigate to the install location of tomcat and inside the `/webapps` folder drop the ngrams.war file.

Run the command startup command for tomcat found in the bin folder to start tomcat.

Navigate to `localhost:8080/ngrams`

### Development & Testing
This project was developed on my own personal laptop running
* OS: [Manjaro Linux](https://manjaro.org/download/official/kde/)
* Kernel: 5.4.6-2
* Java 8
* [IntelliJ IDEA 2019.3.1 (Ultimate Edition)](https://www.jetbrains.com/idea/)
  - Build #IU-193.5662.53, built on December 18, 2019
* [Docker](https://www.docker.com/): 19.03.5
