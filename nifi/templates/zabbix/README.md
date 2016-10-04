# Nifi Example to work with Zabbix

### First flow uses zabbix_get utility in conjunction with Nifi's ExecuteProcess processor to get data out of Zabbix

### Second flow uses zabbix-sender Java API in conjunction with Groovy and Nifi's ExecuteScript processor to write data to Zabbix
Groovy script requires paths to zabbix-sender.jar and fastjson.jar in the Modules section of the processor

### to send data to Zabbix, it requires an item created with "Zabbix Agent (Active)" as type
### pom.xml is not required but dependendies must be available in the modules directory 
