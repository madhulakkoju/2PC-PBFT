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

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig,
      org.cse535.proto.CrossTxnResponse> getCrossShardRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CrossShardRequest",
      requestType = org.cse535.proto.TransactionInputConfig.class,
      responseType = org.cse535.proto.CrossTxnResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig,
      org.cse535.proto.CrossTxnResponse> getCrossShardRequestMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.TransactionInputConfig, org.cse535.proto.CrossTxnResponse> getCrossShardRequestMethod;
    if ((getCrossShardRequestMethod = LinearPBFTGrpc.getCrossShardRequestMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getCrossShardRequestMethod = LinearPBFTGrpc.getCrossShardRequestMethod) == null) {
          LinearPBFTGrpc.getCrossShardRequestMethod = getCrossShardRequestMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.TransactionInputConfig, org.cse535.proto.CrossTxnResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "CrossShardRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.TransactionInputConfig.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CrossTxnResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("CrossShardRequest"))
                  .build();
          }
        }
     }
     return getCrossShardRequestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.PrePrepareRequest,
      org.cse535.proto.PrePrepareResponse> getPrePrepareMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PrePrepare",
      requestType = org.cse535.proto.PrePrepareRequest.class,
      responseType = org.cse535.proto.PrePrepareResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.PrePrepareRequest,
      org.cse535.proto.PrePrepareResponse> getPrePrepareMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.PrePrepareRequest, org.cse535.proto.PrePrepareResponse> getPrePrepareMethod;
    if ((getPrePrepareMethod = LinearPBFTGrpc.getPrePrepareMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getPrePrepareMethod = LinearPBFTGrpc.getPrePrepareMethod) == null) {
          LinearPBFTGrpc.getPrePrepareMethod = getPrePrepareMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.PrePrepareRequest, org.cse535.proto.PrePrepareResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "PrePrepare"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrePrepareRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrePrepareResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("PrePrepare"))
                  .build();
          }
        }
     }
     return getPrePrepareMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest,
      org.cse535.proto.PrepareResponse> getPrepareMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Prepare",
      requestType = org.cse535.proto.PrepareRequest.class,
      responseType = org.cse535.proto.PrepareResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest,
      org.cse535.proto.PrepareResponse> getPrepareMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest, org.cse535.proto.PrepareResponse> getPrepareMethod;
    if ((getPrepareMethod = LinearPBFTGrpc.getPrepareMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getPrepareMethod = LinearPBFTGrpc.getPrepareMethod) == null) {
          LinearPBFTGrpc.getPrepareMethod = getPrepareMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.PrepareRequest, org.cse535.proto.PrepareResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "Prepare"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrepareRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrepareResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("Prepare"))
                  .build();
          }
        }
     }
     return getPrepareMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      org.cse535.proto.CommitResponse> getCommitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Commit",
      requestType = org.cse535.proto.CommitRequest.class,
      responseType = org.cse535.proto.CommitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      org.cse535.proto.CommitResponse> getCommitMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest, org.cse535.proto.CommitResponse> getCommitMethod;
    if ((getCommitMethod = LinearPBFTGrpc.getCommitMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getCommitMethod = LinearPBFTGrpc.getCommitMethod) == null) {
          LinearPBFTGrpc.getCommitMethod = getCommitMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.CommitRequest, org.cse535.proto.CommitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "Commit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommitResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("Commit"))
                  .build();
          }
        }
     }
     return getCommitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      com.google.protobuf.Empty> getCrossShardPrepareMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "crossShardPrepare",
      requestType = org.cse535.proto.CommitRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      com.google.protobuf.Empty> getCrossShardPrepareMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest, com.google.protobuf.Empty> getCrossShardPrepareMethod;
    if ((getCrossShardPrepareMethod = LinearPBFTGrpc.getCrossShardPrepareMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getCrossShardPrepareMethod = LinearPBFTGrpc.getCrossShardPrepareMethod) == null) {
          LinearPBFTGrpc.getCrossShardPrepareMethod = getCrossShardPrepareMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.CommitRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "crossShardPrepare"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("crossShardPrepare"))
                  .build();
          }
        }
     }
     return getCrossShardPrepareMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      org.cse535.proto.CommitResponse> getCrossShardCommitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CrossShardCommit",
      requestType = org.cse535.proto.CommitRequest.class,
      responseType = org.cse535.proto.CommitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest,
      org.cse535.proto.CommitResponse> getCrossShardCommitMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.CommitRequest, org.cse535.proto.CommitResponse> getCrossShardCommitMethod;
    if ((getCrossShardCommitMethod = LinearPBFTGrpc.getCrossShardCommitMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getCrossShardCommitMethod = LinearPBFTGrpc.getCrossShardCommitMethod) == null) {
          LinearPBFTGrpc.getCrossShardCommitMethod = getCrossShardCommitMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.CommitRequest, org.cse535.proto.CommitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "CrossShardCommit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommitResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("CrossShardCommit"))
                  .build();
          }
        }
     }
     return getCrossShardCommitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest,
      org.cse535.proto.PrepareResponse> getSyncMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Sync",
      requestType = org.cse535.proto.PrepareRequest.class,
      responseType = org.cse535.proto.PrepareResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest,
      org.cse535.proto.PrepareResponse> getSyncMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.PrepareRequest, org.cse535.proto.PrepareResponse> getSyncMethod;
    if ((getSyncMethod = LinearPBFTGrpc.getSyncMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getSyncMethod = LinearPBFTGrpc.getSyncMethod) == null) {
          LinearPBFTGrpc.getSyncMethod = getSyncMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.PrepareRequest, org.cse535.proto.PrepareResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "Sync"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrepareRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.PrepareResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("Sync"))
                  .build();
          }
        }
     }
     return getSyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.ExecutionReply,
      com.google.protobuf.Empty> getExecReplyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecReply",
      requestType = org.cse535.proto.ExecutionReply.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.ExecutionReply,
      com.google.protobuf.Empty> getExecReplyMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.ExecutionReply, com.google.protobuf.Empty> getExecReplyMethod;
    if ((getExecReplyMethod = LinearPBFTGrpc.getExecReplyMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getExecReplyMethod = LinearPBFTGrpc.getExecReplyMethod) == null) {
          LinearPBFTGrpc.getExecReplyMethod = getExecReplyMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.ExecutionReply, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "ExecReply"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.ExecutionReply.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("ExecReply"))
                  .build();
          }
        }
     }
     return getExecReplyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      org.cse535.proto.ReShardingInitData> getReShardingInitiationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReShardingInitiation",
      requestType = com.google.protobuf.Empty.class,
      responseType = org.cse535.proto.ReShardingInitData.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      org.cse535.proto.ReShardingInitData> getReShardingInitiationMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, org.cse535.proto.ReShardingInitData> getReShardingInitiationMethod;
    if ((getReShardingInitiationMethod = LinearPBFTGrpc.getReShardingInitiationMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getReShardingInitiationMethod = LinearPBFTGrpc.getReShardingInitiationMethod) == null) {
          LinearPBFTGrpc.getReShardingInitiationMethod = getReShardingInitiationMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, org.cse535.proto.ReShardingInitData>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "ReShardingInitiation"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.ReShardingInitData.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("ReShardingInitiation"))
                  .build();
          }
        }
     }
     return getReShardingInitiationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.ReShardingData,
      org.cse535.proto.CommandOutput> getReShardingProcessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReShardingProcess",
      requestType = org.cse535.proto.ReShardingData.class,
      responseType = org.cse535.proto.CommandOutput.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.ReShardingData,
      org.cse535.proto.CommandOutput> getReShardingProcessMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.ReShardingData, org.cse535.proto.CommandOutput> getReShardingProcessMethod;
    if ((getReShardingProcessMethod = LinearPBFTGrpc.getReShardingProcessMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getReShardingProcessMethod = LinearPBFTGrpc.getReShardingProcessMethod) == null) {
          LinearPBFTGrpc.getReShardingProcessMethod = getReShardingProcessMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.ReShardingData, org.cse535.proto.CommandOutput>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "ReShardingProcess"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.ReShardingData.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.CommandOutput.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("ReShardingProcess"))
                  .build();
          }
        }
     }
     return getReShardingProcessMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.cse535.proto.Transaction,
      com.google.protobuf.Empty> getRelayRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "relayRequest",
      requestType = org.cse535.proto.Transaction.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.cse535.proto.Transaction,
      com.google.protobuf.Empty> getRelayRequestMethod() {
    io.grpc.MethodDescriptor<org.cse535.proto.Transaction, com.google.protobuf.Empty> getRelayRequestMethod;
    if ((getRelayRequestMethod = LinearPBFTGrpc.getRelayRequestMethod) == null) {
      synchronized (LinearPBFTGrpc.class) {
        if ((getRelayRequestMethod = LinearPBFTGrpc.getRelayRequestMethod) == null) {
          LinearPBFTGrpc.getRelayRequestMethod = getRelayRequestMethod = 
              io.grpc.MethodDescriptor.<org.cse535.proto.Transaction, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LinearPBFT", "relayRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.cse535.proto.Transaction.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new LinearPBFTMethodDescriptorSupplier("relayRequest"))
                  .build();
          }
        }
     }
     return getRelayRequestMethod;
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

    /**
     */
    public void crossShardRequest(org.cse535.proto.TransactionInputConfig request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CrossTxnResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCrossShardRequestMethod(), responseObserver);
    }

    /**
     */
    public void prePrepare(org.cse535.proto.PrePrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrePrepareResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPrePrepareMethod(), responseObserver);
    }

    /**
     */
    public void prepare(org.cse535.proto.PrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPrepareMethod(), responseObserver);
    }

    /**
     */
    public void commit(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCommitMethod(), responseObserver);
    }

    /**
     * <pre>
     *follower shard send to Coordinator shard leader
     * </pre>
     */
    public void crossShardPrepare(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getCrossShardPrepareMethod(), responseObserver);
    }

    /**
     */
    public void crossShardCommit(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCrossShardCommitMethod(), responseObserver);
    }

    /**
     */
    public void sync(org.cse535.proto.PrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSyncMethod(), responseObserver);
    }

    /**
     */
    public void execReply(org.cse535.proto.ExecutionReply request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getExecReplyMethod(), responseObserver);
    }

    /**
     */
    public void reShardingInitiation(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<org.cse535.proto.ReShardingInitData> responseObserver) {
      asyncUnimplementedUnaryCall(getReShardingInitiationMethod(), responseObserver);
    }

    /**
     */
    public void reShardingProcess(org.cse535.proto.ReShardingData request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommandOutput> responseObserver) {
      asyncUnimplementedUnaryCall(getReShardingProcessMethod(), responseObserver);
    }

    /**
     */
    public void relayRequest(org.cse535.proto.Transaction request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getRelayRequestMethod(), responseObserver);
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
          .addMethod(
            getCrossShardRequestMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.TransactionInputConfig,
                org.cse535.proto.CrossTxnResponse>(
                  this, METHODID_CROSS_SHARD_REQUEST)))
          .addMethod(
            getPrePrepareMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.PrePrepareRequest,
                org.cse535.proto.PrePrepareResponse>(
                  this, METHODID_PRE_PREPARE)))
          .addMethod(
            getPrepareMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.PrepareRequest,
                org.cse535.proto.PrepareResponse>(
                  this, METHODID_PREPARE)))
          .addMethod(
            getCommitMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.CommitRequest,
                org.cse535.proto.CommitResponse>(
                  this, METHODID_COMMIT)))
          .addMethod(
            getCrossShardPrepareMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.CommitRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_CROSS_SHARD_PREPARE)))
          .addMethod(
            getCrossShardCommitMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.CommitRequest,
                org.cse535.proto.CommitResponse>(
                  this, METHODID_CROSS_SHARD_COMMIT)))
          .addMethod(
            getSyncMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.PrepareRequest,
                org.cse535.proto.PrepareResponse>(
                  this, METHODID_SYNC)))
          .addMethod(
            getExecReplyMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.ExecutionReply,
                com.google.protobuf.Empty>(
                  this, METHODID_EXEC_REPLY)))
          .addMethod(
            getReShardingInitiationMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                org.cse535.proto.ReShardingInitData>(
                  this, METHODID_RE_SHARDING_INITIATION)))
          .addMethod(
            getReShardingProcessMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.ReShardingData,
                org.cse535.proto.CommandOutput>(
                  this, METHODID_RE_SHARDING_PROCESS)))
          .addMethod(
            getRelayRequestMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.cse535.proto.Transaction,
                com.google.protobuf.Empty>(
                  this, METHODID_RELAY_REQUEST)))
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

    /**
     */
    public void crossShardRequest(org.cse535.proto.TransactionInputConfig request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CrossTxnResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCrossShardRequestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void prePrepare(org.cse535.proto.PrePrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrePrepareResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrePrepareMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void prepare(org.cse535.proto.PrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPrepareMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void commit(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCommitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     *follower shard send to Coordinator shard leader
     * </pre>
     */
    public void crossShardPrepare(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCrossShardPrepareMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void crossShardCommit(org.cse535.proto.CommitRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCrossShardCommitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sync(org.cse535.proto.PrepareRequest request,
        io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSyncMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void execReply(org.cse535.proto.ExecutionReply request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExecReplyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reShardingInitiation(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<org.cse535.proto.ReShardingInitData> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReShardingInitiationMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reShardingProcess(org.cse535.proto.ReShardingData request,
        io.grpc.stub.StreamObserver<org.cse535.proto.CommandOutput> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReShardingProcessMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void relayRequest(org.cse535.proto.Transaction request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRelayRequestMethod(), getCallOptions()), request, responseObserver);
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

    /**
     */
    public org.cse535.proto.CrossTxnResponse crossShardRequest(org.cse535.proto.TransactionInputConfig request) {
      return blockingUnaryCall(
          getChannel(), getCrossShardRequestMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.PrePrepareResponse prePrepare(org.cse535.proto.PrePrepareRequest request) {
      return blockingUnaryCall(
          getChannel(), getPrePrepareMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.PrepareResponse prepare(org.cse535.proto.PrepareRequest request) {
      return blockingUnaryCall(
          getChannel(), getPrepareMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.CommitResponse commit(org.cse535.proto.CommitRequest request) {
      return blockingUnaryCall(
          getChannel(), getCommitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     *follower shard send to Coordinator shard leader
     * </pre>
     */
    public com.google.protobuf.Empty crossShardPrepare(org.cse535.proto.CommitRequest request) {
      return blockingUnaryCall(
          getChannel(), getCrossShardPrepareMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.CommitResponse crossShardCommit(org.cse535.proto.CommitRequest request) {
      return blockingUnaryCall(
          getChannel(), getCrossShardCommitMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.PrepareResponse sync(org.cse535.proto.PrepareRequest request) {
      return blockingUnaryCall(
          getChannel(), getSyncMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty execReply(org.cse535.proto.ExecutionReply request) {
      return blockingUnaryCall(
          getChannel(), getExecReplyMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.ReShardingInitData reShardingInitiation(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getReShardingInitiationMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.cse535.proto.CommandOutput reShardingProcess(org.cse535.proto.ReShardingData request) {
      return blockingUnaryCall(
          getChannel(), getReShardingProcessMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty relayRequest(org.cse535.proto.Transaction request) {
      return blockingUnaryCall(
          getChannel(), getRelayRequestMethod(), getCallOptions(), request);
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

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.CrossTxnResponse> crossShardRequest(
        org.cse535.proto.TransactionInputConfig request) {
      return futureUnaryCall(
          getChannel().newCall(getCrossShardRequestMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.PrePrepareResponse> prePrepare(
        org.cse535.proto.PrePrepareRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPrePrepareMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.PrepareResponse> prepare(
        org.cse535.proto.PrepareRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPrepareMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.CommitResponse> commit(
        org.cse535.proto.CommitRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCommitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     *follower shard send to Coordinator shard leader
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> crossShardPrepare(
        org.cse535.proto.CommitRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCrossShardPrepareMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.CommitResponse> crossShardCommit(
        org.cse535.proto.CommitRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCrossShardCommitMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.PrepareResponse> sync(
        org.cse535.proto.PrepareRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSyncMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> execReply(
        org.cse535.proto.ExecutionReply request) {
      return futureUnaryCall(
          getChannel().newCall(getExecReplyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.ReShardingInitData> reShardingInitiation(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getReShardingInitiationMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.cse535.proto.CommandOutput> reShardingProcess(
        org.cse535.proto.ReShardingData request) {
      return futureUnaryCall(
          getChannel().newCall(getReShardingProcessMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> relayRequest(
        org.cse535.proto.Transaction request) {
      return futureUnaryCall(
          getChannel().newCall(getRelayRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST = 0;
  private static final int METHODID_CROSS_SHARD_REQUEST = 1;
  private static final int METHODID_PRE_PREPARE = 2;
  private static final int METHODID_PREPARE = 3;
  private static final int METHODID_COMMIT = 4;
  private static final int METHODID_CROSS_SHARD_PREPARE = 5;
  private static final int METHODID_CROSS_SHARD_COMMIT = 6;
  private static final int METHODID_SYNC = 7;
  private static final int METHODID_EXEC_REPLY = 8;
  private static final int METHODID_RE_SHARDING_INITIATION = 9;
  private static final int METHODID_RE_SHARDING_PROCESS = 10;
  private static final int METHODID_RELAY_REQUEST = 11;

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
        case METHODID_CROSS_SHARD_REQUEST:
          serviceImpl.crossShardRequest((org.cse535.proto.TransactionInputConfig) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.CrossTxnResponse>) responseObserver);
          break;
        case METHODID_PRE_PREPARE:
          serviceImpl.prePrepare((org.cse535.proto.PrePrepareRequest) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.PrePrepareResponse>) responseObserver);
          break;
        case METHODID_PREPARE:
          serviceImpl.prepare((org.cse535.proto.PrepareRequest) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse>) responseObserver);
          break;
        case METHODID_COMMIT:
          serviceImpl.commit((org.cse535.proto.CommitRequest) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse>) responseObserver);
          break;
        case METHODID_CROSS_SHARD_PREPARE:
          serviceImpl.crossShardPrepare((org.cse535.proto.CommitRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_CROSS_SHARD_COMMIT:
          serviceImpl.crossShardCommit((org.cse535.proto.CommitRequest) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.CommitResponse>) responseObserver);
          break;
        case METHODID_SYNC:
          serviceImpl.sync((org.cse535.proto.PrepareRequest) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.PrepareResponse>) responseObserver);
          break;
        case METHODID_EXEC_REPLY:
          serviceImpl.execReply((org.cse535.proto.ExecutionReply) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_RE_SHARDING_INITIATION:
          serviceImpl.reShardingInitiation((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.ReShardingInitData>) responseObserver);
          break;
        case METHODID_RE_SHARDING_PROCESS:
          serviceImpl.reShardingProcess((org.cse535.proto.ReShardingData) request,
              (io.grpc.stub.StreamObserver<org.cse535.proto.CommandOutput>) responseObserver);
          break;
        case METHODID_RELAY_REQUEST:
          serviceImpl.relayRequest((org.cse535.proto.Transaction) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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
              .addMethod(getCrossShardRequestMethod())
              .addMethod(getPrePrepareMethod())
              .addMethod(getPrepareMethod())
              .addMethod(getCommitMethod())
              .addMethod(getCrossShardPrepareMethod())
              .addMethod(getCrossShardCommitMethod())
              .addMethod(getSyncMethod())
              .addMethod(getExecReplyMethod())
              .addMethod(getReShardingInitiationMethod())
              .addMethod(getReShardingProcessMethod())
              .addMethod(getRelayRequestMethod())
              .build();
        }
      }
    }
    return result;
  }
}
