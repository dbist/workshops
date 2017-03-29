# Intro
created by Biren Saini

# Concept 

## Flow Summary

* The java class is configured to 
 - have the hbase-site.xml, core-site.xml & hdfs-site.xml in classpath 
 - read a property file for jdbc url, query and name of the column to read
* hbase-site.xml, core-site.xml & hdfs-site.xml tell the program to use KERBEROS auth instead of SIMPLE

* JDBC url tells program the keytab and principal to use for KERBEROS auth

* JVM argument tell the location of KDC via krb5.conf file and tell program on how to authenticate the user 


## Things to know

* JDBC URL Format - TBD

* Windows Client vs Mac Client - TBD

# Note

Setup tested on HDP2.6.0


# Usage 

## Setup
	. Add following files from your environment 
		- auth/krb5.conf 
		- auth/smokeuser.headless.keytab  (Keytab for the actual end user - name whatever you want) 
		- conf/hbase-site.xml
		- conf/core-site.xml
		- conf/hdfs-site.xml
		- get phoenix client jar - phoenix-4.7.0.2.6.0.0-597-client.jar - and add it to ./lib/
		
	. Update the configuration file 
		- Update ./conf/app.conf and update the JDBC Url, Query and Column to read
		
## Building 
	. Execute following command from the project root directory 
	----
		build.sh
	----

# Running
	. Execute following command from the project root directory 
	----
		run.sh
	----
		
