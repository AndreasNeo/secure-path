# SecurePath
Android app with distributed backend for finding safe routes to a destination based on past crime incidents in England and allows the user to visualise and view information of recent crimes on a map. 

# Requirements
Java SE Deveopment Kit (JDK) 8
winutils.exe for handoop-2.7.1
Apache Spark 2.3.0 with package type 'Pre-built for apache handoop 2.7 and later'
NetBeans 8.2
Android Studio 3

The System was tested and created on Windows 10.

# Installation of required software
(Skip winutils,Apache Spark,NetBeans if you do not want to run with Spark Backend)
__________________________________________________________________________________
JDK:
1. Download and install JDK: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
3. Set NEW Environment Variable JAVA_HOME pointing to the installation folder e.g. D:\SoftwareFiles\Java\jdk1.8.0_152
2. Set Environment Variable to Path: bin folder in JDK installation directory e.g. %JAVA_HOME%\bin

winutils:
1. Download https://github.com/steveloughran/winutils/blob/master/hadoop-2.7.1/bin/winutils.exe
2. Create a folder named 'winutils', in /winutils/ create another folder named 'bin' and place downloaded file there. e.g. D:\SoftwareFiles\winutils\bin\winutils.exe
3. Set NEW Environment Variable HADOOP_HOME pointing to the winutils folder e.g. D:\SoftwareFiles\winutils
4. Set Environment Variable to Path: bin folder in winutils folder e.g. %HADOOP_HOME%\bin

Apache Spark:
1. Download Spark 2.3.0 with package type 'Pre-built for apache handoop 2.7 and later' from http://apache.mirror.anlx.net/spark/spark-2.3.0/spark-2.3.0-bin-hadoop2.7.tgz
2. Create a folder named 'spark' and Extract *.tgz with WinRAR there
3. Set NEW Environment Variable SPARK_HOME pointing to the 'spark' folder e.g. D:\SoftwareFiles\spark
4. Set Environment Variable to Path: bin folder in spark folder e.g. %SPARK_HOME%\bin

NetBeans:
1. Download and install NetBeans IDE - select 'All' option - https://netbeans.org/downloads/index.html

Android Studio 3:
1. Download and install Android Studio https://developer.android.com/studio/#downloads
2. Download SDK from Android Studio


# Running the System: 
(Skip steps 1,2,3c if you do not want to run with Spark Backend)
__________________________________________________________________________________
1. Start Master and Workers:
	a. Open command window
	b. To start Master: 
	spark-class org.apache.spark.deploy.master.Master
	c. To start Worker 1 (-c cores, -m memory adjust to your machine): 
	spark-class org.apache.spark.deploy.worker.Worker spark://MASTER_IP_HERE  -c 4 -m 4g 
	d. To start Worker 2 (-c cores, -m memory adjust to your machine): 
	spark-class org.apache.spark.deploy.worker.Worker spark://MASTER_IP_HERE  -c  4 -m 4g 
	
2. Start Hive ThriftServer2:
	a. Extract Data.zip
	b. Navigate to the extracted files 
	c. Open command window there with Shift+Right Click
	d. Run the following command (--master the address of Master) (--executor-memory the amount of ram of each Worker) :
	spark-class org.apache.spark.deploy.SparkSubmit --master  spark://MASTER_IP_HERE  --executor-memory 4G  --class org.apache.spark.sql.hive.thriftserver.HiveThriftServer2
	e. Wait until final line is:  Starting ThriftBinaryCLIService on port 10000 with 5...500 worker threads

3. Run NetBeans Web Service  
	a. Open NetBeans IDE then -> File -> Open Project -> Select SecurePath_WebApp_NetBeans
	c. Press Play button 
	d. To check if there is a connection with HiveThriftServer2: navigate to http://localhost:8084/SecurePathService/example and JSON file should be returned in about 30 seconds

4. Run Android Studio Application: 
	a. Extract SecurePath_Android
	b. Open Android Studio -> Open project -> Select extracted folder
	c. Edit IP_ADDRESS variable in DirectionsSparkActivity in app\src\main\java\andreasneokleous\com\securepath to your IP address
		Find IP Address: 
		i. Open command prompt
		ii. Execute: ipconfig
		iii. Find 'IPv4 Address' under the adapter that connects you to the internet e.g. Wireless LAN adapter WiFi 2
	d. Press Play button and run it on an Emulator or an External device.
	e. If Error: split lib_slice_2_apk was defined multiple times / Error installing APK then: File -> Settings -> Build,Execution,Depolyment -> Instand Run -> Untick Enable Instant Run ...


