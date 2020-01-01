package com.castlery.oms.common.abc;

import com.castlery.oms.generated.EchoServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class EchoClient {

  @GrpcClient()
  private EchoServiceGrpc.EchoServiceBlockingStub echoStub;

}
