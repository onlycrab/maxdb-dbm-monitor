# Description
This file contains a detailed description of configuring and integrating the maxdb-mdb-monitor.jar library with the 
[Zabbix](https://www.zabbix.com/) monitoring system.  
# Table of contents 
- [Overview](#overview)
- [Compatibility](#compatibility)
- [Features](#features)
    - [Discovery](#discovery)
    - [List of processed data](#dataList)
- [Add plugin to zabbix-agent](#addPlugin)
    - [Standalone server](#addPluginStandalone)
    - [Microsoft failover cluster](#addPluginCluster)
- [Add template to zabbix-server](#addTemplate)
- [Template configuration](#templateConfig)
    - [Discovery](#configDiscovery)
    - [DB user](#confiDbUser)
    - [Host](#configHost)
    - [Usage limit alert](#configUsageAlert)
    - [Custom macro](#configCustomMacro)
    - [DataArea full daily backup](#configDataAreaBackup)
TEMPLATE_OVERVIEW.md
<a name="#overview"></a>
# Overview
Description of all elements of the template is located in the file [TEMPLATE_OVERVIEW.md](https://github.com/onlycrab/maxdb-dbm-monitor/tree/master/src/zabbix/TEMPLATE_OVERVIEW.md).
<a name="compatibility"></a>
# Compatibility
Java version        : 1.8 or above  
Zabbix version      : 5.0+, 6.0  
Operation system    : Windows or Linux family  
Tested on           : SAP MaxDB 7.9, Zabbix 5.0, Windows Server 2012/16 (standalone and failover cluster), openSUSE Leap 15
<a name="features"></a>  
# Features
<a name="discovery"></a>
## Discovery
The template automatically discovers MaxDB and XServer instances.  
Discovery works in 3 modes:  
1. all DB and XServers;  
2. all DB and XServers, except for the names that you specified manually;  
3. only those DB and XServers whose names you specified manually.
  
Each mode is configured through `zabbix macros`. See description in section [Template configuration - Discovery](#configDiscovery).
<a name="dataList"></a>
## List of processed data
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
<a name="addPlugin"></a> 
# Add plugin to zabbix-agent
<a name="addPluginStandalone"></a>
## Standalone server
1. Place the contents of the `zabbixRoot` directory from the `maxdb-dbm-monitor-zabbix.zip` archive into the root 
directory of the zabbix-agent configuration. Make changes to the agent configuration file, similar to the file 
`~zabbix_agentd.conf`. If you are already using the `Include` directive, change it or place `maxdb.conf` so that `maxdb.conf` match within its 
definition.
2. Place the `maxdb-dbm-monior.jar` and `sapdbc.jar` libraries in the `<zabbixRoot>\plugins\maxdb\` directory.  
3. For linux-like systems, make sure the zabbix-agent daemon has enough access to the plugin's directories and files.  
4. Restart the zabbix-agent service.
5. Make sure the settings are correct. To do this, follow  
for Windows  
`<zabbixRoot>\zabbix-agentd.exe -c <zabbixRoot>\zabbix-agentd.conf -t maxdb.discovery[]`  
for Linux  
`/usr/sbin/zabbix-agentd -c <zabbixRoot>/zabbix-agentd.conf -t maxdb.discovery[]`  
If the settings are correct, you will get a list of MaxDB database names as a result, or an empty list if the MaxDB 
database is not found.  
In case of an error in the settings, the result of the command will contain a textual description of the error.
<a name="addPluginCluster"></a>
## Microsoft failover cluster
1. In order to correctly connect the cluster object to zabbix, you need to install a second instance of zabbix-agent 
service on each cluster node. Cmd command example:  
`sc create "Zabbix Agent Cluster" binPath= "c:\zabbixCluster\zabbix_agentd.exe -c c:\zabbixCluster\zabbix_agentd.win.conf" displayname= "Zabbix Agent Cluster" start= auto`  
2. On the zabbix-server, create a host for the cluster object.  
3. Then follow all steps in sequence as for the case with a standalone server.
<a name="addTemplate"></a>
# Add template to zabbix-server
By default, the template is imported into the `Custom Templates` group. If you want to import it into another group, 
change file `zbx_[version]_template_maxdb-dbm-monitor` content from
```
<group>
    <name>Custom Templates</name>
</group>
```
to
```
<group>
    <name>YourAwesomeGroup</name>
</group>
```
Then import template from `zbx_[version]_template_maxdb-dbm-monitor` (.xml or .yaml) and value map from `zbx_5.0_valuemapping_maxdb-dbm-monitor.xml` (only for .xml template, for .yaml map is already included to template file),
<a name="templateConfig"></a>
# Template configuration
The template is configured through `zabbix macros`. Do not forget that macro values can be set in the template 
(then this value will be applied to all hosts) or only on one specific host (only this host will be affected).  
<a name="configDiscovery"></a>
## Discovery
Use macros `{$MAXDBEXCLUDEDB}`, `{$MAXDBONLYDB}` and `{$MAXDBINIPATH}` to customize the discovery process.  

**{$MAXDBEXCLUDEDB}** : exclude database name from DB and XServer discovery. Excluded db-names must be separated by 
comma (f.e. "ADB,BDB").  
Example: Let's say `AAA`, `AAB`, and `AAC` databases are deployed on the server. If you specify `{$MAXDBEXCLUDEDB}=AAB`, 
then discover will return the names`AAA and AAC`. For `{$MAXDBEXCLUDEDB}=AAA,AAC` discover will return `AAB`.  

**{$MAXDBONLYDB}** : discover only DB names in this list (for DB instances and XServer) discovery. Db-names must be 
separated by comma (f.e. "ADB,BDB").  
Example: Let's say `AAA`, `AAB`, and `AAC` databases are deployed on the server. If you specify `{$MAXDBONLYDB}=AAB`, 
then discover will return only name`AAB`. For `{$MAXDBEXCLUDEDB}=AAA,AAC` discover will return `AAA and AAC`.  

**{$MAXDBINIPATH}** : absolute path to `Installations.ini` file (for MaxDB and XServer discovery). By default 
`/sapdb/data/config/Installations.ini`. Use it **only for Linux** systems.  

Linux-like OS stores MaxDB installation data in the file. At the time of creating this template, regardless of the MaxDB 
installation path, a link is created from the `sapdb` directory to the root of the OS file system. Therefore, by default on Linux, the `maxdb-dbm-monior` library looks for the configuration file there, and therefore the `{$MAXDBINIPATH}` macro in the template has an empty value. In the event that something changes in the future, you can always only specify the correct path in the macro without changing any settings on the host side.  
### Additional
At the same time, you can specify a non-empty value only for one of the macros `{$MAXDBEXCLUDEDB}` and `{$MAXDBONLYDB}`. 
If both macros have a non-empty value, the discovery process will return an error like  
```
{"error":"Conflict at discovery mode : selected EXCLUDE and ONLY mode at one time, arguments <ed(exclude-database)> <od(only-database)>"}
```   
<a name="confiDbUser"></a>
## DB user
To get data from the database, of course, you need to pass a user (and password) with read permissions to `maxdb-dbm-monitor`.  

On the server, for each instance of MaxDB, you must create a user (or use an existing one) with [DBInfoRead](https://help.sap.com/docs/JAVA_SERVER_FOR_S4HANA/d3bbe2d506ae4964911a87bc9e14b910/44eab0870d3567d8e10000000a155369.html) permission.  

Authentication data can be passed through macros `{$MAXDBUSER}` and `{$MAXDBPASS}` or specified in the file 
`<zabbixRoot>\plugins\maxdb\.pass` on the host. The file is prioritized. That is, if you specify authentication data 
both in the macros and in the file, data from the file will be used.
<a name="configHost"></a>
## Host
Integration with Zabbix means that commands to `maxdb-dbm-monitor` library will be executed on the host, and not 
somewhere else. So the default hostname is `localhost`
```
{$MAXDBHOST} = localhost
```
<a name="configUsageAlert"></a>
## Usage limit alert
By default, the DataArea and LogArea fill triggers fire when the value reaches `90%`. For large databases, even free 
space 1% can amount to tens of gigabytes, and the database does not require the intervention of administrators.  

Macros `{$MAXDB_DA_FREEBYTESLIMIT}` (for DataArea) and `{$MAXDB_LA_FREEBYTESLIMIT}` (for LogArea) have been created for 
more flexible settings of such alerts. Alert is triggered if the percentage of database filling exceeds 90% **AND** the free space in the database cells is less than `{$MAXDB_DA_FREEBYTESLIMIT}` (for LogArea - `{$MAXDB_LA_FREEBYTESLIMIT}`).  

Example: Let the database be 93% filled  and free space is 5MB (5242880 bytes). If `{$MAXDB_DA_FREEBYTESLIMIT}=6000000`, 
then there will be no notification. And for `{$MAXDB_DA_FREEBYTESLIMIT}=5000000` the alert will be triggered.
<a name="configCustomMacro"></a>
## Custom macro
You may need to create more complex triggers based on the available data. `{$MAXDB_ZMACROS}` has been created to 
implement the possibility of transferring data from the host settings to the data item. Its meaning is to transfer the 
value to the data returned by the host.  
Macro value : `key1=val1#key2=val2...`  
Return data : `{..., "zm_key1":"val1", "zm_key2":"val2", ...}`

Example 1: Let `{$MAXDB_ZMACROS}=check_days=2345#time_since=120000#time_to=180000`. Then the request to get the power 
state will return the value
```
{"operstate":"1", "error":"", "zm_check_days":"2345", "zm_time_since":"120000", "zm_time_to":"180000"}
```
Now you can create the data item, which will be assigned the values `zm_check_days`, `zm_time_since`, `zm_time_to`.  

Example 2: the second example is the algorithm for checking that daily backups are performed only on certain days of 
the week, shown below.
### Additional
Be careful when passing special characters in a key or value: they can be misinterpreted by the host OS command 
interpreter. For example, the character `,` for `sh`.
<a name="configDataAreaBackup"></a>
## DataArea full daily backup
If you don't need it, just disable the trigger `{#MAXDBNAME} DAT daily backup not found` in the template.   

Alert of the daily backup task failure is configured through a custom macro
```
{$MAXDB_ZMACROS} = "backup_daily_dat_days=12345#backup_daily_dat_time=080000"
``` 
You can change the day numbers and check time according to your daily backup job schedule.
  
**backup_daily_dat_days** : list of days of the week on which the success of the backup task will be checked. Day 
numbers : `1 - Monday`, `7 - Sunday` (for compare with zabbix function [dayofweek()](https://www.zabbix.com/documentation/5.0/en/manual/appendix/triggers/functions)).  

**backup_daily_dat_time** : time of day from which the backup task will be checked. Time format is `HHMMSS` (for 
compare with zabbix function [time()](https://www.zabbix.com/documentation/5.0/en/manual/appendix/triggers/functions)).  

How zabbix checks if the daily backup is complete :
1. retrieves the values ​​of the custom macro `backup_daily_dat_time`;
2. extracts the values ​​of the custom macro `backup_daily_dat_days` and then calculates whether it is necessary to check 
today (by javascript function at [preprocessing stage](https://www.zabbix.com/documentation/5.0/en/manual/config/items/preprocessing));
3. checks whether the check time has come: if the result of calculations from **step2** not equals **1** OR the current 
time is less than the time by the value from **step1**, the notification is not performed; otherwise - go to **step4**;
4. gets the data about last DataArea backup task. If the date of completion of the last copy not equals today's date - 
the notification is triggered, otherwise - the notification is not performed.  

As you can see from the last step, if the date of completion of the last copy equals today, but the copy was executed 
with errors, the alert will not work. This is done because the trigger described above is only responsible for the 
existence of a backup in the current day. And trigger `{#MAXDBNAME} last DAT backup failed` is responsible for checking 
the success of the last backup task.  

<a name="dataAreaBackupExample1"></a>
#### Example 1
Let today Wednesday 12/12/2012. Backup completed successfully **today** 12/12/2012 at 05:30.  
Then:
- for the values `backup_daily_dat_days=12345` and `backup_daily_dat_time=080000` check will be performed only after 08:00;
- for the values `backup_daily_dat_days=246` and `backup_daily_dat_time=080000` check will not be performed, because 
today is the 3rd day of the week, and it is not in the list `246`;
- for the values `backup_daily_dat_days=135` and `backup_daily_dat_time=033000` check will be made after 3:30 AM. Since 
the backup will only run at 5:30 AM, after 3:30 AM, a daily backup failure alert will be triggered. After 5:30 AM, the 
trigger will return to normal because the backup task will already be done.
#### Example 2
Let today Wednesday 12/12/2012. Backup completed successfully **yesterday** 11/12/2012 at 22:50.  
Then for the values `backup_daily_dat_days=12345` and `backup_daily_dat_time=080000` check will be made after 8:00 AM, 
and a daily backup failure alert will be triggered. The trigger will return to normal only **tomorrow** after 00:00, and 
up to 8:00 AM zabbix will not checks daily backup.
#### Example 3
Let today Wednesday 12/12/2012. Backup completed **today** 12/12/2012 at 05:30, but **with some errors**.  
Then, the daily backup check trigger will work exactly as in [Example 1](#dataAreaBackupExample1). Instead, the trigger 
`{#MAXDBNAME} last DAT backup failed` will create alert as soon as zabbix receives information about the erroneous 
status of the last backup task.
