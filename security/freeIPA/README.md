Tested using ambari-quickstart-guide https://cwiki.apache.org/confluence/display/AMBARI/Quick+Start+Guide
Ambari 2.2.2.0 and HDP 2.4.2.0

------------------------------------
1 A. Install IPA server on Linux VM 
------------------------------------

##Apply OS updates
yum -y update

#turn off firewall
service iptables save
service iptables stop
chkconfig iptables off

#install IPA server
yum install -y "*ipa-server" bind bind-dyndb-ldap

#add entry for ipa.hortonworks.com into the /etc/hosts file of the VM 
#echo "192.168.191.198 ipa.hortonworks.com ipa" >> /etc/hosts

#Run IPA setup: Hit enter 3 times to accept all defaults, then enter hortonworks as passwords, then enter Y to continue.
#For DNS, enter 8.8.8.8
#For reverse proxy, pick the default
ipa-server-install --setup-dns 

#This sets up the FreeIPA Server.This includes a number of components and may take a few min
#  Install/configure the Network Time Daemon (ntpd)
#  Install/configure a stand-alone Certificate Authority (CA) for certificate management
#  Install/create and configure an instance of Directory Server
#  Install/create and configure a Kerberos Key Distribution Center (KDC)
#  Configure Apache (httpd)

#To remove IPA
#ipa-client-install --uninstall
#http://adam.younglogic.com/2011/02/sterilizing-for-ipa-uninstall/



#configure the components to startup automatically on reboot
for i in ipa krb5kdc ntpd named httpd dirsrv; do chkconfig $i on; done

#Note: each time IPA VM is rebooted you need to ensure the IPA services came up before starting the HDP VM
service ipa status


#Sync time with ntp server to ensure time is upto date 
service ntpd stop
ntpdate pool.ntp.org
service ntpd start

#Add the same to startup to correct time on each reboot
vi  /etc/rc.local
service ntpd stop
ntpdate pool.ntp.org
//ntpdate north-america.pool.ntp.org
service ntpd start
service ipa start

#Restart the SSH service to retrieve the Kerberos principal and to refresh the name server switch (NSS) configuration file:
service sshd restart

#obtain a kerberos ticket for admin user
kinit admin

## Password is hortonworks
##uid=admin,cn=users,cn=accounts,dc=mycluster,dc=com

## Set Password Policy
#ipa pwpolicy-find
#ipa pwpolicy-mod --maxlife=0 --minlife=0 global_policy
ipa pwpolicy-mod --maxlife=360 --minlife=1 global_policy


#Modify Expiration

ldapmodify -D "cn=Directory Manager" -w hortonworks -h ambari1.mycluster -p 389 -vv
dn: uid=admin,cn=users,cn=accounts,dc=mycluster,dc=com
changetype: modify
replace: krbpasswordexpiration
krbpasswordexpiration: 20201231011529Z


# To Unlock account if needed
ldapmodify -D "cn=Directory Manager" -w hortonworks -h ambari1.mycluster -p 389 -vv
dn: uid=admin,cn=users,cn=accounts,dc=mycluster,dc=com
changetype: modify
replace: nsaccountlock
nsaccountlock: false



#Setup LDAP groups
ipa group-add marketing --desc marketing
ipa group-add legal --desc legal
ipa group-add hr --desc hr
ipa group-add sales --desc sales
ipa group-add finance --desc finance


#Setup LDAP users
#ipa user-show paul --all

ipa user-add sales1 --first=sales1 --last=sales1
ipa user-add sales2 --first=sales2 --last=sales2
ipa user-add legal1 --first=legal1 --last=legal1
ipa user-add legal2 --first=legal2 --last=legal2
ipa user-add legal3 --first=legal3 --last=legal3
ipa user-add hr1 --first=hr1 --last=hr1
ipa user-add hr2 --first=hr2 --last=hr2
ipa user-add hr3 --first=hr3 --last=hr3

ipa user-add hadoopadmin --first=Hadoop --last=Admin
ipa user-add rangeradmin --first=Ranger --last=Admin
ipa user-add keyadmin --first=Key --last=Admin

ipa user-add  demo --first=DEMO --last=DEMO


#Add users to groups
ipa group-add-member sales --users=sales1,sales2
ipa group-add-member finance --users=ali,paul
ipa group-add-member legal --users=legal1,legal2,legal3
ipa group-add-member hr --users=hr1,hr2,hr3
ipa group-add-member admins --users=hadoopadmin,rangeradmin,keyadmin


#Set passwords for accounts: hortonworks
echo hortonworks >> tmp.txt
echo hortonworks >> tmp.txt

ipa passwd sales1 < tmp.txt
ipa passwd sales2 < tmp.txt
ipa passwd legal1 < tmp.txt
ipa passwd legal2 < tmp.txt
ipa passwd legal3 < tmp.txt
ipa passwd hr1 < tmp.txt
ipa passwd hr2 < tmp.txt
ipa passwd hr3 < tmp.txt
ipa passwd hadoopadmin < tmp.txt
ipa passwd rangeradmin < tmp.txt
ipa passwd keyadmin < tmp.txt

ipa passwd demo < tmp.txt
rm -f tmp.txt

ipa user-mod sales1 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod sales2 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod legal1 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod legal1 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod legal3 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod hr1 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod hr2 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod hr3 --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod hadoopadmin --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod rangeradmin --setattr=krbPasswordExpiration=20201231011529Z
ipa user-mod keyadmin --setattr=krbPasswordExpiration=20201231011529Z

#Use JXplorer to browse the LDAP structure we just setup
com->hortonworks->accounts->users
com->hortonworks->accounts->groups

Click on sales1 user and notice attributes. Some important ones
uiud, uidNumber, posixaccount, person, krbPrincipalName

Click on hr group and notice attributes. Some important ones
cn, gidNumber, posixgroup

--------------------------------------------------------------------------
1 B. Install IPAclient on sandbox and secure cluster with KDC on IPA server  
--------------------------------------------------------------------------

i
# On all other rnodes
#Install client: When prompted enter: yes > yes > hortonworks

yum install ipa-client openldap-clients -y

ipa-client-install --domain=my.cluster --server=master1.mycluster  --mkhomedir --ntp-server=north-america.pool.ntp.org -p admin@MYCLUSTER.COM -W
ipa-client-install --domain=my.cluster --server=slave1.mycluster  --mkhomedir --ntp-server=north-america.pool.ntp.org -p admin@MYCLUSTER.COM -W
ipa-client-install --domain=my.cluster --server=slave2.mycluster  --mkhomedir --ntp-server=north-america.pool.ntp.org -p admin@MYCLUSTER.COM -W


service ntpd stop
ntpdate pool.ntp.org
//ntpdate north-america.pool.ntp.org
service ntpd start

#review that kerberos conf file was updated correctly with realm
vi /etc/krb5.conf

#review that SSSD was correctly configured with ipa and sandbox hostnames
vi /etc/sssd/sssd.conf 

#enable sssd on startup 
chkconfig sssd on

#review PAM related files and confirm the pam_sss.so entries are present
vi /etc/pam.d/smartcard-auth
vi /etc/pam.d/password-auth 
vi /etc/pam.d/system-auth
vi /etc/pam.d/fingerprint-auth

#test that LDAP queries work
#ldapsearch -h ipa.hortonworks.com:389 -D 'uid=admin,cn=users,cn=accounts,dc=hortonworks,dc=com' -w hortonworks -x -b 'dc=hortonworks,dc=com' uid=paul
#ldapsearch -x -LLL -D "cn=Directory Manager" -w hortonworks "uid=admin" krbPasswordExpiration
#ldapsearch -x -LLL -D "cn=Directory Manager" -w hortonworks "cn=global_policy"
#ldapsearch -x -LLL -D "cn=MYCLUSTER.COM,cn=kerberos,dc=mycluster,dc=com" -w hortonworks "cn=global_policy"

ldapsearch -h ambari1.mycluster:389 -D 'uid=admin,cn=users,cn=accounts,dc=mycluster,dc=com' -w hortonworks -x -b 'dc=mycluster,dc=com' uid=paul

#test that LDAP users can be accessed from filesystem.  
id sales1
groups sales1
#This shows that the OS now recognizes users and groups defined only in our LDAP 
#The end user is getting a combined view of the linux and LDAP worlds in single lookup


#Test access to users
id paul
groups paul

------------------------------------
1B. Start Kerberos Wizard in Ambari and get kerberos.csv file
- make sure and take out {clustername} for all service principals
- add storm user to kerberos.csv file
- copy kerberos.csv file to all nodes 
------------------------------------

------------------------------------
2. Create Principals on IPA Server on Linux VM 
------------------------------------

##On the IPA node Create principals using csv file
## authenticate
#create principals. The following message is ignorable: service with name "HTTP/sandbox.hortonworks.com@HORTONWORKS.COM" already exists

#First install JCE on all nodes
scp -i secloud.pem jce_policy-8.zip root@mckingedge.cloud.hortonworks.com:~


kinit admin
awk -F"," '/SERVICE/ {print "ipa service-add --force "$3}' kerberos.csv | sort -u > ipa-add-spn.sh
awk -F"," '/USER/ {print "ipa user-add "$5" --first="$5" --last=Hadoop --shell=/sbin/nologin"}' kerberos.csv > ipa-add-upn.sh
chmod +x ipa-add-spn.sh
chmod +x ipa-add-upn.sh
sh ipa-add-spn.sh
sh ipa-add-upn.sh


##On the HDP nodes authenticate and create the keytabs
## authenticate

sudo kinit admin
export ipa_server=$(cat /etc/ipa/default.conf | awk '/^server =/ {print $3}')

sudo mkdir /etc/security/keytabs/
sudo chown root:hadoop /etc/security/keytabs/
#awk -F"," '/'$(hostname -f)'/ {print "ipa-getkeytab -s '${ipa_server}' -p "$3" -k "$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > gen_keytabs.sh

##Create ONLY Service Users FOR ALL NODES

awk -F"," '/'$(hostname -f)'/ {if ($4 == "SERVICE") print "ipa-getkeytab -s '${ipa_server}' -p "$3" -k "$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > gen_service_keytabs.sh
#ipa-getkeytab -s mckingedge.cloud.hortonworks.com -p hdfs@MYCLUSTER.COM -k /etc/security/keytabs/hdfs.headless.keytab;chown hdfs:hadoop /etc/security/keytabs/hdfs.headless.keytab;chmod 440 /etc/security/keytabs/hdfs.headless.keytab

#sudo bash ./gen_keytabs.sh
sudo bash ./gen_service_keytabs.sh


#Verify kinit works before proceeding (should not give errors)
#export realm=$(cat /etc/ipa/default.conf | awk '/^realm =/ {print $3}')
#sudo sudo -u hdfs kinit -kt /etc/security/keytabs/nn.service.keytab nn/$(hostname -f)@${realm}
#sudo sudo -u ambari-qa kinit -kt /etc/security/keytabs/smokeuser.headless.keytab ambari-qa@${realm}
#sudo sudo -u hdfs kinit -kt /etc/security/keytabs/hdfs.headless.keytab hdfs@${realm}

## TEST SERVICE ACCOUNTS FOR ALL NODES
#awk -F"," '/headless/ {print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > ipa-test-keytabs.sh

awk -F"," '/'$(hostname -f)'/ {if ($4 == "SERVICE") print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > test_service_keytabs.sh
sudo bash ./test_service_keytabs.sh


##Create ONLY Headless User Accounts ON ONE NODE
#awk -F"," '/'$(hostname -f)'/ {if ($4 == "USER") print "ipa-getkeytab -s '${ipa_server}' -p "$3" -k "$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > gen_user_keytabs.sh
awk -F"," '/USER/ {print "ipa-getkeytab -s '${ipa_server}' -p "$3" -k "$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > gen_user_keytabs.sh

sudo bash ./gen_user_keytabs.sh

#awk -F"," '/'$(hostname -f)'/ {if ($4 == "USER") print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > test_user_keytabs.sh
#awk -F"," '/USER/ {if ($1 != "'$(hostname -f)'") print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > test_user_keytabs.sh
awk -F"," '/USER/ {print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > test_user_keytabs.sh

sudo bash ./test_user_keytabs.sh

##COPY Headless User Accounts to all other nodes as appropriate FROM ONE NODE
#ipa-getkeytab -s mckingedge.cloud.hortonworks.com -p hdfs@MYCLUSTER.COM -k /etc/security/keytabs/hdfs.headless.keytab;chown hdfs:hadoop /etc/security/keytabs/hdfs.headless.keytab;chmod 440 /etc/security/keytabs/hdfs.headless.keytab

#awk -F"," '/'$(hostname -f)'/ {if ($4 == "USER") print "scp root@'${ipa_server}':"$6,$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > cp_user_keytabs.sh
#awk -F"," '/USER/ {if ($1 != "'$(hostname -f)'") print "scp root@'${ipa_server}':"$6,"root@"$1":"$6";chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > cp_user_keytabs.sh
awk -F"," '/USER/ {if ($1 != "'$(hostname -f)'") print "scp root@'${ipa_server}':"$6,"root@"$1":"$6}' kerberos.csv | sort -u > cp_user_keytabs.sh

sudo bash ./cp_user_keytabs.sh

## GO TO EACH NODE AND CHANGE PERMISSIONS

awk -F"," '/'$(hostname -f)'/ {if ($4 == "USER") print "chown "$7":"$9,$6";chmod "$11,$6}' kerberos.csv | sort -u > chmod_user_keytabs.sh
sudo bash ./chmod_user_keytabs.sh

awk -F"," '/'$(hostname -f)'/ {if ($4 == "USER") print "sudo sudo -u "$7" kinit -kt "$6" "$3}' kerberos.csv | sort -u > test_user_keytabs.sh
sudo bash ./test_user_keytabs.sh



#To Remove Keytabs:

#ipa-rmkeytab -dc=myclusterr EXAMPLE.COM -k /etc/krb5.keytab
#ipa-rmkeytab -p ldap/client.example.com -k /etc/krb5.keytab
#ipa-rmkeytab -p hdfs@MYCLUSTER.COM -k /etc/security/keytabs/hdfs.headless.keytab
#ipa-rmkeytab -p HTTP/mckingedge.cloud.hortonworks.com@MYCLUSTER.COM -k /etc/security/keytabs/spnego.service.keytab

##IMPORTANT REMOVE HTTP
#kinit admin
#awk -F"," '/USER/ {print "ipa-rmkeytab  -p "$3" -k "$6}' kerberos.csv | sort -u > ipa-rm-keytabs.sh
#awk -F"," '/'$(hostname -f)'/ {print "ipa-rmkeytab  -p "$3" -k "$6}' kerberos.csv | sort -u > ipa-rm-keytabs.sh
#sudo bash ./ipa-rm-keytabs.sh

#rm -f /etc/security/keytabs/*


##########################################################################################################################################
#TEST Kerberos Cluser #
#   #
##########################################################################################################################################


#Test kinits:

sudo sudo -u ambari-qa kinit -kt /etc/security/keytabs/smokeuser.headless.keytab ambari-qa@MYCLUSTER.COM
sudo sudo -u hbase kinit -kt /etc/security/keytabs/hbase.headless.keytab hbase@MYCLUSTER.COM
sudo sudo -u hdfs kinit -kt /etc/security/keytabs/hdfs.headless.keytab hdfs@MYCLUSTER.COM
sudo sudo -u spark kinit -kt /etc/security/keytabs/spark.headless.keytab spark@MYCLUSTER.COM
sudo sudo -u storm kinit -kt /etc/security/keytabs/storm.headless.keytab storm@MYCLUSTER.COM

# Create HDFS Directories

su - hdfs
kinit -kt /etc/security/keytabs/hdfs.headless.keytab hdfs@MYCLUSTER.COM

hdfs dfs -mkdir /user/demo
hdfs dfs -chown demo:hdfs /user/demo
hdfs dfs -chmod 755 /user/demo


hdfs dfs  -mkdir /user/sales1
hdfs dfs  -chown sales1:sales /user/sales1
hdfs dfs  -chmod 755 /user/sales1

hdfs dfs  -mkdir /user/sales2
hdfs dfs  -chown sales2:sales /user/sales2
hdfs dfs  -chmod 755 /user/sales2

hdfs dfs  -mkdir /user/legal1
hdfs dfs  -chown legal1:legal /user/legal1
hdfs dfs  -chmod 755 /user/legal1

hdfs dfs  -mkdir /user/legal2
hdfs dfs  -chown legal2:legal /user/legal2
hdfs dfs  -chmod 755 /user/legal2

hdfs dfs  -mkdir /user/legal3
hdfs dfs  -chown legal3:legal /user/legal3
hdfs dfs  -chmod 755 /user/legal3

hdfs dfs  -mkdir /user/hr1
hdfs dfs  -chown hr1:hr /user/hr1
hdfs dfs -chmod 755 /user/hr1

hdfs dfs  -mkdir /user/hr2
hdfs dfs  -chown hr2:hr /user/hr2
hdfs dfs  -chmod 755 /user/hr2

hdfs dfs  -mkdir /user/hr3
hdfs dfs  -chown hr3:hr /user/hr3
hdfs dfs  -chmod 755 /user/hr3

# Create Home directories

mkdir /home/demo
chown sales1:sales /home/sales1

mkdir /home/paul
chown sales2:sales /home/sales2


mkdir /home/legal1
chown legal1:legal /home/legal1


mkdir /home/legal2
chown legal2:legal /home/legal2


mkdir /home/legal3
chown legal3:legal /home/legal3


mkdir /home/hr1
chown hr1:hr /home/hr1


mkdir /home/hr2
chown hr2:hr /home/hr2


mkdir /home/hr3
chown hr3:hr /home/hr3



## Test WebHDFS

curl -skL --negotiate -u : "http://$(hostname -f):50070/webhdfs/v1/user/?op=LISTSTATUS"

Test Hive (using Beeline or another Hive JDBC client)

Hive in Binary mode (the default)
#beeline -u "jdbc:hive2://localhost:10000/default;principal=hive/$(hostname -f)@HORTONWORKS.COM"
beeline -u "jdbc:hive2://master1.mycluster:10000/default;principal=hive/master1.mycluster@MYCLUSTER.COM"


Hive in HTTP mode
## note the update to use HTTP and the need to provide the kerberos principal.
#beeline -u "jdbc:hive2://localhost:10001/default;transportMode=http;httpPath=cliservice;principal=HTTP/$(hostname -f)@HORTONWORKS.COM"






##########################################################################################################
#SETUP AMBARI#
https://github.com/abajwa-hw/security-workshops/blob/master/Setup-Ambari.md
#authentication-via-ldap-or-active-directory
##########################################################################################################


        
#allow hive to impersonate users from whichever LDAP groups you choose
#hadoop.proxyuser.hive.groups = users, sales, legal, admins


ldapsearch -x -LLL -D "cn=Directory Manager" -w hortonworks "uid=admin"

ldapsearch -x -LLL -D "cn=Directory Manager" -w hortonworks "cn=admins"


sudo ambari-server setup-ldap

[root@mckingedge ~]# sudo ambari-server setup-ldap
Setting up LDAP properties...
Primary URL* {host:port} (ambari1.mycluster:389):
Secondary URL {host:port} :
Use SSL* [true/false] (false):
User object class* (posixAccount):
User name attribute* (uid):
Group object class* (posixGroup):
Group name attribute* (cn):
Group member attribute* (memberOf): member
Distinguished name attribute* (dn):
Base DN* (cn=accounts,dc=mycluster,dc=com):
Referral method [follow/ignore] :
Bind anonymously* [true/false] (false):
Manager DN* (uid=admin,cn=users,cn=accounts,dc=mycluster,dc=com):
Enter Manager Password* :
Re-enter password:


ambari-server sync-ldap --all

ambari-server restart

*** Note Though, the admin user from FreeIPA would be synched and used in Ambari instead

#Setup an ambari-server Kerberos user for Ambari
#http://docs.hortonworks.com/HDPDocuments/Ambari-2.2.2.0/bk_Ambari_Security_Guide/content/_set_up_kerberos_for_ambari_server.html

kinit admin
ipa user-add ambari-server --first=Ambari --last=Server --shell=/sbin/nologin

ipa-getkeytab -s ambari1.mycluster -p ambari-server@MYCLUSTER.COM -k /etc/security/keytabs/ambari-server.keytab;chown root:hadoop /etc/security/keytabs/ambari-server.keytab;chmod 440 /etc/security/keytabs/ambari-server.keytab

ambari-server stop

ambari-server setup-security (Option 3)

#Add in Services > HDFS > Configs -> Advanced -> Custom core-site
hadoop.proxyuser.ambari-server.groups=*
hadoop.proxyuser.ambari-server.hosts=*



##########################################################################################################
#SETUP AMBARI  VIEWS
https://github.com/abajwa-hw/security-workshops/blob/master/Setup-Ambari.md#authentication-via-ldap-or-active-directory
##########################################################################################################

- Yarn Scheduler works
- File View:  auth=KERBEROS;proxyuser=ambari-server and custom config
- Hive View
- Tez View: 
- Pig View: 


      
----------------------------------------------------------------------------------------------------------------------------------------------------------------
Part 2 - Authorization & Audit: To allow users to specify access policies and enable audit around Hadoop from a central location via a UI, integrated with LDAP
----------------------------------------------------------------------------------------------------------------------------------------------------------------
Goals: 
	-Install Apache Ranger
	-Sync users between Apache Ranger and FreeIPA
	-Configure HDFS & Hive to use Apache Ranger 
	-Define HDFS & Hive Access Policy For Users
	-Log into Hue as the end user and note the authorization policies being enforced


#Login as rangeradmin
sudo sudo -u rangeradmin kinit


#Go to the Node MySQL is installed on
#sudo mysql -h $(hostname -f)

sudo mysql or sudo mysql -p -h $(hostname -f) /*If sudo does not work; try without the sudo */

CREATE USER 'root'@'%';
GRANT ALL PRIVILEGES ON *.* to 'root'@'%' WITH GRANT OPTION;
SET PASSWORD FOR 'root'@'%' = PASSWORD('hortonworks');
SET PASSWORD = PASSWORD('hortonworks');
FLUSH PRIVILEGES;
exit

#Test Root user
mysql -u root -h $(hostname -f) -p -e "select count(user) from mysql.user;"


#Go to ambari server node: 
#Install MySQL JDBC: 

>ls /usr/share/java/mysql-connector-java.jar

>sudo yum install mysql-connector-java*

> sudo ambari-server setup --jdbc-db=mysql --jdbc-driver=/usr/share/java/mysql-connector-java.jar

#Go to Node where Ranger will be installed and install mysql client: 

> yum install mysql

#Then test
> mysql -u root -h mckingnn3.cloud.hortonworks.com -p -e "select count(user) from mysql.user;"

To test it running: ps -f -C java | grep "Dproc_ranger" | awk '{print $9}'

##########################################################################################################
#INSTALL SOLR AUDIT for Ranger
http://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.4.2/bk_Security_Guide/content/solr_ranger_installing.html
##########################################################################################################

export JAVA_HOME=/usr/jdk64/jdk1.8.0_60

yum install lucidworks-hdpsearch

#### Follow http://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.4.2/bk_Security_Guide/content/solr_ranger_configure_standalone.html

cd /usr/local/solr_for_audit_setup
> vi install.priperties

export JAVA_HOME=/usr/jdk64/jdk1.8.0_60
/opt/solr/ranger_audit_server/scripts/start_solr.sh


##########################################################################################################
#INSTALL Ranger VIA Ambari
http://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.4.2/bk_Security_Guide/content/solr_ranger_installing.html
##########################################################################################################



Install Ranger via Ambari
Enter rangeradmin/rangeradmin for password
Ranger URL is the web UI host: http://RANGER_HOST(Client):6080



Ranger - Setup HDFS repo
-------------------------
In the Ranger UI, under PolicyManager tab, click the + sign next to HDFS and enter below 
most values come from HDFS configs in Ambari):

Repository name: hdfs_sandbox
Username: xapolicymgr
Password: hortonworks
fs.default.name: hdfs://sandbox.hortonworks.com:8020
hadoop.security.authorization: true
hadoop.security.authentication: kerberos
hadoop.security.auth_to_local: (copy from HDFS configs)
dfs.datanode.kerberos.principal: dn/_HOST@HORTONWORKS.COM
dfs.namenode.kerberos.principal: nn/_HOST@HORTONWORKS.COM
dfs.secondary.namenode.kerberos.principal: nn/_HOST@HORTONWORKS.COM
Common Name For Certificate: (leave this empty)

#install HDFS plugin
#Note: if this were a multi-node cluster, you would run these steps on the host running the NameNode




Ranger - HDFS Audit Exercises:
------------------------------




#Now look at the audit reports for the above and notice that audit reports for Beeswax queries show up in Ranger 


                


### Demo TDE

sudo -u hdfs kinit -kt /etc/security/keytabs/hdfs.headless.keytab hdfs@MYCLUSTER.COM
sudo -u keyadmin kinit
sudo -u hr1 kinit
sudo -u legal1 kinit


sudo -u keyadmin hadoop key list -metadata

sudo -u keyadmin hdfs dfs -mkdir /data/unsecure


sudo -u keyadmin hdfs dfs -mkdir /data/hrsecure
sudo -u hdfs hdfs dfs -chown hr1:hr /data/hrsecure


sudo -u keyadmin hdfs dfs -mkdir /data/legalsecure
sudo -u hdfs hdfs dfs -chown legal1:legal /data/legalsecure

#Create an encryption Zone
sudo -u hdfs hdfs crypto -createZone -keyName hrkey -path /data/hrsecure
sudo -u hdfs hdfs crypto -createZone -keyName legalkey -path /data/legalsecure

#check EZs got created
sudo -u hdfs hdfs crypto -listZones


##
sudo -u hr1 echo "HR Employee File" > /tmp/hr.txt

sudo -u hr1 hdfs dfs -put /tmp/hr.txt /data/hrsecure

sudo -u hr1 hdfs dfs -cat  /data/hrsecure/hr.txt

sudo -u legal1 hdfs dfs -cat  /data/hrsecure/hr.txt



sudo -u legal1 echo "Legal Contracts" > /tmp/legal.txt

sudo -u legal1 hdfs dfs -put /tmp/legal.txt /data/legalsecure

sudo -u legal1 hdfs dfs -cat /data/legalsecure/legal.txt

sudo -u hr1 hdfs dfs -cat /data/legalsecure/legal.txt

## Test Deleting from Encryption Zone

sudo -u hr1 echo "HR Employee File2" > /tmp/hr2.txt
sudo -u hr1 hdfs dfs -put /tmp/hr2.txt /data/hrsecure

sudo -u hr1 hdfs dfs -rm  /data/hrsecure/hr2.txt

sudo -u hr1 hdfs dfs -rm -skipTrash /data/hrsecure/hr2.txt

# View contents of raw file

sudo -u hdfs hdfs dfs -cat /.reserved/raw/data/legalsecure/legal.txt


