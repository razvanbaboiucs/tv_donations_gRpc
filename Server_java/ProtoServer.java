import io.grpc.Server;
import io.grpc.ServerBuilder;

import services.ObserverImplementation;
import services.ServerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProtoServer {
    private final int port;
    private final Server server;

    public ProtoServer(int port, ServerService serverServiceImplementation, ObserverImplementation observer) {
        this.port = port;
        ServerBuilder serverBuilder = ServerBuilder.forPort(port);
        server = serverBuilder.addService(serverServiceImplementation)
                .addService(observer).build();
    }

    public void start() throws IOException {
        System.out.println("Server is on ...");
        server.start();
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}