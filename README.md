# About this project
MaxDB DBMMonitor is a java library for easy monitoring of SAP MaxDB database baselines. To connect to the database, the 
**sapdbc.jar** library is used, which is distributed along with the [SAP MaxDB Database Studio](https://help.sap.com/docs/SAP_NETWEAVER_DBOS/e55b190e1c0b462bbaabd2ad8eb6a692/e9005dac1592496783e26133eb7fad0b.html). All indicators of the database are retrieved by commands similar to the Database Studio console; the library does not directly access database tables by SQL queries.  

The project also include a solution for organizing monitoring by the [Zabbix](https://www.zabbix.com/) system.  
# Table of contents 
- [Compatibility](#compatibility)
- [Features](#features)
- [How to use it](#libHowToUseIt)
- [Integration with Zabbix monitoring system](#zab)
- [Building](#build)
<a name="features"></a>
<a name="compatibility"></a>
# Compatibility
Java version        : 1.8 or above  
Zabbix version      : 5.0+, 6.0  
Operation system    : Windows or Linux family  
Tested on           : SAP MaxDB 7.9, Zabbix 5.0, Windows Server 2012/16 (standalone and failover cluster), openSUSE Leap 15
# Features
The solution allows receiving and processing the following data:
- database power state: `online`, `offline`, etc;
- general parameters: status of the internal monitoring system, the number of bad indexes, changing the database parameters;
- DataArea statistics: usage in bytes, % usage, number of volumes;
- LogArea statistics: usage in bytes, % usage, write mode, overwrite mode, mirror mode;
- session usage statistics;
- lock statistics;
- caches hit rate;
- backup job status: for `DAT`, `LOG` and `PAG`;
- connection to XServer availability.     
<a name="libHowToUseIt"></a>
## How to use it
First, you need to place the `sapdbc.jar` file next to the `maxdb-dbm-monior.jar` (in the same directory). By default, 
sapdbc.jar is located at `<MaxDB_InstallationPath>\runtime\jar\sapdbc.jar`.  
Receiving data from the database is carried out through the console interface. To get a list of commands, run  
```
java -jar maxdb-dbm-monitor.jar --help
```
For example, to get information about the DataArea parameters for database ABC, run  
```
java -jar maxdb-dbm-monitor.jar -ris -n serverDnsOrIp -d ABC -u userName -p userPassword
```
<a name="zab"></a>
# Integration with Zabbix monitoring system
The library integrates with Zabbix as follows:  
- For each available request, you need to create a corresponding user parameter in the zabbix-agent configuration. For 
example  
`UserParameter=maxdb.infostate[*],java -jar c:\zabbix\plugins\maxdb\maxdb-dbm-monitor.jar -ris -n $1 -d $2 -u $3 -p $4 -ef $5 -m $6`  
- On the zabbix-server side, you need to configure the assignment of data (received from the request as a JSON string) 
to data items.  
- To monitor and notify about changes in important indicators, it is necessary to create triggers on the the 
zabbix-server side.  

The solution in this project contains ready-made settings for zabbix-agent and a template for zabbix-server (archive 
`maxdb-dbm-monitor-zabbix.zip`). You only need to connect it and configure it to your needs.  

A detailed description is available [here](https://github.com/onlycrab/maxdb-dbm-monitor/tree/master/src/zabbix).  
<a name="build"></a>
# Building
To build you need Java 1.8 or above and Maven 3.2.5 or above.
  
First, add `sapdbc.jar` library to you local repository. By default, sapdbc.jar is located at 
<MaxDB_InstallationPath>\runtime\jar\sapdbc.jar.  
```
mvn install:install-file -Dfile=<yourPath>\sapdbc.jar -DgroupId=com.sap.dbtech -DartifactId=sapdbc -Dversion=1.0.0 -Dpackaging=jar
```  
Also add [argument-parser library](https://github.com/onlycrab/argument-parser/releases)  
```
mvn install:install-file -Dfile=<yourPath>\argument-parser-1.0.0.jar -DgroupId=com.github.onlycrab.argParser -DartifactId=argument-parser -Dversion=1.0.0 -Dpackaging=jar
```
Finally run the build process  
```
mvn clean package
```
