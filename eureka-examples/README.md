## Example Overview
The eureka example requires 3 participants:（eureka example需要三个参与者）
* a centralized eureka server for registration and discovery()（用于注册和发现的集中式eureka服务器）
* an example service to register with eureka that exposes a REST endpoint （一个可以向eureka server暴露的断点去注册服务的例子）
* an example client that discovers the example service via discovery, and then queries the example service on it's
  registered REST endpoint.（一个例子是通过服务发现这个注册的服务，并且可以通过服务断点查到已经注册过的服务）

### Setting up the Eureka server
1. Edit [eureka-server.properties](https://github.com/Netflix/eureka/blob/master/eureka-server/src/main/resources/eureka-server.properties) and uncomment the two settings that makes the demo server start up faster (via disabling safeguards)
2. [Build](https://github.com/Netflix/eureka/wiki/Building-Eureka-Client-and-Server) the application.
3. The above build also sets up all the libraries needed for running the demo service and the demo client.
4. Copy the WAR artifact to your tomcat deployment directory under _$TOMCAT_HOME/webapps/
<pre><code>
cp ./eureka-server/build/libs/eureka-server-XXX-SNAPSHOT.war $TOMCAT_HOME/webapps/eureka.war
</pre></code>
5. Create (or add to) a setenv.sh in tomcat/bin/ with the following java opts (these are for the demo server to start up fast, see EurekaServerConfig.java for their documentation):
<pre>
JAVA_OPTS=" \
  -Deureka.waitTimeInMsWhenSyncEmpty=0 \
  -Deureka.numberRegistrySyncRetries=0"
</pre>
6. Start your tomcat server. Access _**http://localhost:8080/eureka**_ to verify the information there. Your server's eureka client should register itself in 30 seconds and you should see that information there.

### Running the examples directly
1. Start up a local eureka server
2. Run the example service first with <code>./gradlew :eureka-examples:runExampleService</code> and wait until you see "_Service started and ready to process requests.._" indicating that is had registered with eureka.
3. Run the example client with <code>./gradlew :eureka-examples:runExampleClient</code>.

### Running the examples as an app
1. Start up a local eureka server
2. Build a zip distribution with <code>./gradlew :eureka-examples:distZip</code>
3. In the distribution run <code>./bin/ExampleEurekaService</code> and wait until you see "_Service started and ready to process requests.._" indicating that is had registered with eureka.
4. In the distribution run <code>./bin/ExampleEurekaClient</code> to run the example client.

## Examples Provided

### ExampleEurekaService
An example service that registers itself with eureka. (Note: prefer the DI version of creation in ExampleEurekaGovernatedService).

### ExampleEurekaGovernatedService
The same as the ExampleEurekaService, where Governator/Guice is used to initialize everything. The gradle javaExec for this is <code>./gradlew :eureka-examples:runExampleGovernatedService</code> and the distribution generated script is <code>./bin/ExampleEurekaGovernatedService</code>

### ExampleEurekaClient
An example use case of the eureka client to find a particular application/vip (in this case the example service) via eureka for communication.
