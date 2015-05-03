package qnd;

import com.github.btnguyen2k.id.jclient.IIdClient;
import com.github.btnguyen2k.id.jclient.impl.ThriftIdClient;
import com.github.btnguyen2k.id.jclient.impl.ThriftIdClientFactory;

public class QndThriftIdClient {

    public static void main(String[] args) {
        IIdClient idClient = new ThriftIdClient().setIdServerHost("localhost")
                .setIdServerPort(9090).init();
        System.out.println(idClient.nextId("default"));

        idClient = ThriftIdClientFactory.newIdClient("localhost:9090");
        System.out.println(idClient.nextId("default"));
    }

}
