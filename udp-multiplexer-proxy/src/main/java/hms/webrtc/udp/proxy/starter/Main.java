package hms.webrtc.udp.proxy.starter;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by isuru on 3/29/15.
 */

public class Main {
    public static void main(String[] args) {
        @SuppressWarnings("resource")
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
                SpringConfig.class);

        ctx.registerShutdownHook();
    }
}
