package dev.sultanov.grpc.exception.server;

import dev.sultanov.grpc.exception.common.GreetingRequest;
import dev.sultanov.grpc.exception.common.GreetingResponse;
import dev.sultanov.grpc.exception.common.GreetingServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new GreetingService())
                .intercept(new ExceptionHandler())
                .build();

        server.start();
        System.out.println("gRPC Server started, listening on port:" + server.getPort());
        server.awaitTermination();
    }

    private static class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {
        @Override
        public void greeting(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
            String name = request.getName();
            if (name.isBlank()) {
                throw new IllegalArgumentException("Missing name");
            }
            String greeting = String.format("Hello, %s!", name);
            GreetingResponse response = GreetingResponse.newBuilder()
                    .setGreeting(greeting)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

}
