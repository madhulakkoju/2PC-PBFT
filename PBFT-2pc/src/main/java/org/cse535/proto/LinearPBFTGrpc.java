package org.cse535.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: pbft-2pc.proto")
public final class LinearPBFTGrpc {

  private LinearPBFTGrpc() {}

  public static final String SERVICE_NAME = "LinearPBFT";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig,
      org.cse535.proto.TxnResponse> getRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Request",
      requestType = org.cse535.proto.TransactionInputConfig.class,
      responseType = org.cse535.proto.TxnResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig,
      org.cse535.proto.TxnResponse> getRequestMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig, org.cse535.proto.TxnResponse> getRequestMethod;
    if ((getRequestMethod = LinearPBFTGrpc.getRequestMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getRequestMethod = LinearPBFTGrpc.getRequestMethod) == null) {
          LinearPBFTGrpc.getRequestMethod = getRequestMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.TransactionInputConfig, org.cse535.proto.TxnResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "Request"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.TransactionInputConfig.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.TxnResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("Request"))
                  .build();
          }
        }
     }
     return getRequestMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LinearPBFTStub newStub(io.grpc.Channel channel) {
    return new LinearPBFTStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LinearPBFTBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new LinearPBFTBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LinearPBFTFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new LinearPBFTFutureStub(channel);
  }

  /**
   */
  public static abstract class LinearPBFTImplBase implements io.grpc.BindableService {

    /**
     */
    public void request(org.cse535.proto.TransactionInputConfig request,
        io.grpc.stub.StreamObserver<org.cse535.proto.TxnResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.TransactionInputConfig,
                org.cse535.proto.TxnResponse>(
                  this, METHODID_REQUEST)))
          .build();
    }
  }

  /**
   */
  public static final class LinearPBFTStub extends io.grpc.stub.AbstractStub<LinearPBFTStub> {
    private LinearPBFTStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LinearPBFTStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LinearPBFTStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LinearPBFTStub(channel, callOptions);
    }

    /**
     */
    public void request(org.cse535.proto.TransactionInputConfig request,
        io.grpc.stub.StreamObserver<org.cse535.proto.TxnResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRequestMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LinearPBFTBlockingStub extends io.grpc.stub.AbstractStub<LinearPBFTBlockingStub> {
    private LinearPBFTBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LinearPBFTBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LinearPBFTBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LinearPBFTBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.cse535.proto.TxnResponse request(org.cse535.proto.TransactionInputConfig request) {
      return blockingUnaryCall(
          getChannel(), getRequestMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LinearPBFTFutureStub extends io.grpc.stub.AbstractStub<LinearPBFTFutureStub> {
    private LinearPBFTFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LinearPBFTFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LinearPBFTFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LinearPBFTFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.TxnResponse> request(
        org.cse535.proto.TransactionInputConfig request) {
      return futureUnaryCall(
          getChannel().newCall(getRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LinearPBFTImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LinearPBFTImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST:
          serviceImpl.request((org.cse535.proto.TransactionInputConfig) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.TxnResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class LinearPBFTBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LinearPBFTBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.cse535.proto.Pbft2Pc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LinearPBFT");
    }
  }

  private static final class LinearPBFTFileDescriptorSupplier
      extends LinearPBFTBaseDescriptorSupplier {
    LinearPBFTFileDescriptorSupplier() {}
  }

  private static final class LinearPBFTMethodDescriptorSupplier
      extends LinearPBFTBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LinearPBFTMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (LinearPBFTGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LinearPBFTFileDescriptorSupplier())
              .addMethod(getRequestMethod())
              .build();
        }
      }
    }
    return result;
  }
}
