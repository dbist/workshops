
import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;
import java.nio.charset.StandardCharsets;

try {
    String host = "centos7.example.com";
    int port = 10051;
    ZabbixSender zabbixSender = new ZabbixSender(host, port);

    DataObject dataObject = new DataObject();
    dataObject.setHost("172.17.42.1");
    dataObject.setKey("test_item");
    dataObject.setValue("10");
    // TimeUnit is SECONDS.
    //dataObject.setClock(System.currentTimeMillis()/1000);
    
    SenderResult result = zabbixSender.send(dataObject);
    
    if (result.success()) {
        System.out.println("send success.");
    } else {
        System.err.println("sned fail!");
    }
    
    flowFile = session.create()
    flowFile = session.write(flowFile, new org.apache.nifi.processor.io.OutputStreamCallback() {
            @Override
            public void process(final OutputStream out) throws IOException {
                out.write(result.toString().getBytes(StandardCharsets.UTF_8));
            }
        });
    
    
    session.transfer(flowFile, REL_SUCCESS)
      
} catch (IOException ex) {
    log.error("something is wrong", ex);
    session.transfer(flowFile, REL_FAILURE)
}

