package com.castlery.oms.common.abc;

import com.castlery.oms.generated.Abc.EchoRequest;
import com.castlery.oms.generated.Abc.EchoResponse;
import com.castlery.oms.generated.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class EchoGrpcServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

  @Override
  public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    String message = request.getMessage();
    EchoResponse response = EchoResponse.newBuilder().setMessage("From server -> " + message)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
