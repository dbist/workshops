## Remove Oozie service using API
##### tested on Ambari 2.2.1.0

##### get a list of all host components
```
curl -u admin:admin -H "X-Requested-By: ambari" -X GET  http://localhost:8080/api/v1/clusters/u1204/services/OOZIE
```

##### ensure the service is stopped
```
curl -u admin:admin -H "X-Requested-By: ambari" -X PUT -d '{"RequestInfo":{"context":"Stop Service"},"Body":{"ServiceInfo":{"state":"INSTALLED"}}}' http://localhost:8080/api/v1/clusters/u1204/services/OOZIE
```

##### stop any related service components, repeat for any server that has service components
```
curl -u admin:admin -H "X-Requested-By: ambari" -X PUT -d '{"RequestInfo":{"context":"Stop Component"},"Body":{"HostRoles":{"state":"INSTALLED"}}}' http://localhost:8080/api/v1/clusters/u1204/hosts/u1201.ambari.apache.org/host_components/OOZIE_CLIENT
curl -u admin:admin -H "X-Requested-By: ambari" -X PUT -d '{"RequestInfo":{"context":"Stop Component"},"Body":{"HostRoles":{"state":"INSTALLED"}}}' http://localhost:8080/api/v1/clusters/u1204/hosts/u1203.ambari.apache.org/host_components/OOZIE_CLIENT
```

##### stop all service components in one shot
```
curl -u admin:admin -H "X-Requested-By: ambari" -X PUT -d '{"RequestInfo":{"context":"Stop All Components"},"Body":{"ServiceComponentInfo":{"state":"INSTALLED"}}}' http://localhost:8080/api/v1/clusters/u1204/services/OOZIE/components/OOZIE_CLIENT
```

##### delete service
```
curl -u admin:admin -H "X-Requested-By: ambari" -X DELETE  http://localhost:8080/api/v1/clusters/u1204/services/OOZIE
```
