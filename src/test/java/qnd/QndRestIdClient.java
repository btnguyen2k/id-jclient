package qnd;

import com.github.btnguyen2k.id.jclient.IIdClient;
import com.github.btnguyen2k.id.jclient.impl.RestIdClient;
import com.github.btnguyen2k.id.jclient.impl.RestIdClientFactory;

public class QndRestIdClient {

    public static void main(String[] args) {
        IIdClient idClient = new RestIdClient().setIdServerUrl("http://localhost:9000").init();
        System.out.println(idClient.nextId("default"));

        idClient = RestIdClientFactory.newIdClient("http://localhost:9000");
        System.out.println(idClient.nextId("default"));
    }

}
