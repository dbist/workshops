import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;
import java.nio.charset.StandardCharsets;
import java.util.Random;

ZabbixSender zabbixSender;
try {
    String host = "centos7.example.com";
    int port = 10051;
    zabbixSender = new ZabbixSender(host, port);

    DataObject dataObject = new DataObject();
    dataObject.setHost(host);
    dataObject.setKey("nifitrap");
    Random random = new Random();
    int value = Math.abs(random.nextInt() % 1000) + 1;
    dataObject.setValue(String.valueOf(value));
    
    Runtime.getRuntime().addShutdownHook(new ZabbixThread(zabbixSender, dataObject));
    
    try {
        flowFile = session.create()
        flowFile = session.putAttribute(flowFile, "zabbix.result" as String, dataObject.getValue() as String)
        session.transfer(flowFile, REL_SUCCESS)
    } catch (Exception e) {
        log.error("something is wrong", ex);
        throw new Exception("something wrong in flow block");
    }
    
} catch (IOException ex) {
    log.error("something is wrong", ex);
    session.transfer(flowFile, REL_FAILURE);
}

class ZabbixThread extends Thread {
    private static ZabbixSender zabbixSender;
    private static DataObject dataObject;
    
    public ZabbixThread(ZabbixSender zabbixSender, DataObject dataObject) {
        this.zabbixSender = zabbixSender;
        this.dataObject = dataObject;
    }
    
    public void run() {
        try {
            SenderResult result = zabbixSender.send(dataObject, dataObject.getClock());
            if(result.success()) {
                System.out.println(result.success());
                System.out.println(dataObject.getValue());
                System.out.println("All done!");
            }    
        } catch (IOException ex) {
            Logger.getLogger(ZabbixThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
