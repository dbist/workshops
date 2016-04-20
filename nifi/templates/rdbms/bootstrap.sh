# create directories for local FileSystem
# mkdir -p /tmp/csv /tmp/ssn

# create a destination directory on HDFS
# sudo -u hdfs hdfs dfs -mkdir /tmp/nossn
# sudo -u hdfs hdfs dfs -chmod -R 777 /tmp/nossn

# download the latest HDF release
cd ~
wget http://public-repo-1.hortonworks.com.s3.amazonaws.com/HDF/centos6/1.x/updates/1.2.0.0/HDF-1.2.0.0-91.tar.gz
tar xvzf HDF-1.2.0.0-91.tar.gz && cd HDF-1.2.0.0/nifi/conf

# replace nifi port 8080 to 9090
cd ~/HDF-1.2.0.0/nifi/conf
sed 's/8080/9090/' nifi.properties > new
mv new nifi.properties

# start nifi
../bin/nifi.sh start
