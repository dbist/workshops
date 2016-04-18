HDF_VERSION=HDF-1.2.0.0-91
HDF_VERSION_SHORT=1.2.0.0

# create directories for local FileSystem
# mkdir -p /tmp/csv /tmp/ssn

# create a destination directory on HDFS
# sudo -u hdfs hdfs dfs -mkdir /tmp/nossn
# sudo -u hdfs hdfs dfs -chmod -R 777 /tmp/nossn

# download the latest HDF release
wget http://s3.amazonaws.com/public-repo-1.hortonworks.com/HDF/centos6/1.x/updates/$HDF_VERSION_SHORT/$HDF_VERSION.tar.gz
# tar xvzf HDF-$HDF_VERSION-bin.tar.gz && cd $HDF_VERSION/conf

# replace nifi port 8080 to 9090
sed 's/8080/9090/' nifi.properties > new
mv new nifi.properties

# start nifi
../bin/nifi.sh start
