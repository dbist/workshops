# create directories for local FileSystem
mkdir -p /tmp/csv /tmp/ssn

# create a destination directory on HDFS
sudo -u hdfs hdfs dfs -mkdir /tmp/nossn
sudo -u hdfs hdfs dfs -chmod -R 777 /tmp/nossn

# download the latest HDF release
wget http://s3.amazonaws.com/public-repo-1.hortonworks.com/HDF/1.1.1.0/nifi-1.1.1.0-12-bin.tar.gz
tar xvzf nifi-1.1.1.0-12-bin.tar.gz && cd nifi-1.1.1.0-12/conf

# replace nifi port 8080 to 9090
sed 's/8080/9090/' nifi.properties > new
mv new nifi.properties

# start nifi
../bin/nifi.sh start
