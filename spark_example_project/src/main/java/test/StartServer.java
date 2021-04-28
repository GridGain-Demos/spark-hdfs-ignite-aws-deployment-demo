package test;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class StartServer {
    public static void main(String[] args) {
        Ignite ignite = Ignition.start("server.xml");

        ignite.cluster().active(true);
    }
}
