// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pbft-2pc.proto

package org.cse535.proto;

/**
 * Protobuf type {@code PrepareRequest}
 */
public  final class PrepareRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:PrepareRequest)
    PrepareRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use PrepareRequest.newBuilder() to construct.
  private PrepareRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private PrepareRequest() {
    ballotNumber_ = 0;
    processId_ = "";
    latestCommittedBallotNumber_ = 0;
    clusterId_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private PrepareRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            ballotNumber_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            processId_ = s;
            break;
          }
          case 26: {
            org.cse535.proto.Transaction.Builder subBuilder = null;
            if (transaction_ != null) {
              subBuilder = transaction_.toBuilder();
            }
            transaction_ = input.readMessage(org.cse535.proto.Transaction.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(transaction_);
              transaction_ = subBuilder.buildPartial();
            }

            break;
          }
          case 34: {
            org.cse535.proto.Transaction.Builder subBuilder = null;
            if (latestCommittedTransaction_ != null) {
              subBuilder = latestCommittedTransaction_.toBuilder();
            }
            latestCommittedTransaction_ = input.readMessage(org.cse535.proto.Transaction.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(latestCommittedTransaction_);
              latestCommittedTransaction_ = subBuilder.buildPartial();
            }

            break;
          }
          case 40: {

            latestCommittedBallotNumber_ = input.readInt32();
            break;
          }
          case 48: {

            clusterId_ = input.readInt32();
            break;
          }
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.cse535.proto.Pbft2Pc.internal_static_PrepareRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.cse535.proto.Pbft2Pc.internal_static_PrepareRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.cse535.proto.PrepareRequest.class, org.cse535.proto.PrepareRequest.Builder.class);
  }

  public static final int BALLOTNUMBER_FIELD_NUMBER = 1;
  private int ballotNumber_;
  /**
   * <code>int32 ballotNumber = 1;</code>
   */
  public int getBallotNumber() {
    return ballotNumber_;
  }

  public static final int PROCESSID_FIELD_NUMBER = 2;
  private volatile java.lang.Object processId_;
  /**
   * <pre>
   * processId is Server Name
   * </pre>
   *
   * <code>string processId = 2;</code>
   */
  public java.lang.String getProcessId() {
    java.lang.Object ref = processId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      processId_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * processId is Server Name
   * </pre>
   *
   * <code>string processId = 2;</code>
   */
  public com.google.protobuf.ByteString
      getProcessIdBytes() {
    java.lang.Object ref = processId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      processId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TRANSACTION_FIELD_NUMBER = 3;
  private org.cse535.proto.Transaction transaction_;
  /**
   * <code>.Transaction transaction = 3;</code>
   */
  public boolean hasTransaction() {
    return transaction_ != null;
  }
  /**
   * <code>.Transaction transaction = 3;</code>
   */
  public org.cse535.proto.Transaction getTransaction() {
    return transaction_ == null ? org.cse535.proto.Transaction.getDefaultInstance() : transaction_;
  }
  /**
   * <code>.Transaction transaction = 3;</code>
   */
  public org.cse535.proto.TransactionOrBuilder getTransactionOrBuilder() {
    return getTransaction();
  }

  public static final int LATESTCOMMITTEDTRANSACTION_FIELD_NUMBER = 4;
  private org.cse535.proto.Transaction latestCommittedTransaction_;
  /**
   * <code>.Transaction latestCommittedTransaction = 4;</code>
   */
  public boolean hasLatestCommittedTransaction() {
    return latestCommittedTransaction_ != null;
  }
  /**
   * <code>.Transaction latestCommittedTransaction = 4;</code>
   */
  public org.cse535.proto.Transaction getLatestCommittedTransaction() {
    return latestCommittedTransaction_ == null ? org.cse535.proto.Transaction.getDefaultInstance() : latestCommittedTransaction_;
  }
  /**
   * <code>.Transaction latestCommittedTransaction = 4;</code>
   */
  public org.cse535.proto.TransactionOrBuilder getLatestCommittedTransactionOrBuilder() {
    return getLatestCommittedTransaction();
  }

  public static final int LATESTCOMMITTEDBALLOTNUMBER_FIELD_NUMBER = 5;
  private int latestCommittedBallotNumber_;
  /**
   * <code>int32 latestCommittedBallotNumber = 5;</code>
   */
  public int getLatestCommittedBallotNumber() {
    return latestCommittedBallotNumber_;
  }

  public static final int CLUSTERID_FIELD_NUMBER = 6;
  private int clusterId_;
  /**
   * <code>int32 clusterId = 6;</code>
   */
  public int getClusterId() {
    return clusterId_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (ballotNumber_ != 0) {
      output.writeInt32(1, ballotNumber_);
    }
    if (!getProcessIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, processId_);
    }
    if (transaction_ != null) {
      output.writeMessage(3, getTransaction());
    }
    if (latestCommittedTransaction_ != null) {
      output.writeMessage(4, getLatestCommittedTransaction());
    }
    if (latestCommittedBallotNumber_ != 0) {
      output.writeInt32(5, latestCommittedBallotNumber_);
    }
    if (clusterId_ != 0) {
      output.writeInt32(6, clusterId_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (ballotNumber_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, ballotNumber_);
    }
    if (!getProcessIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, processId_);
    }
    if (transaction_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, getTransaction());
    }
    if (latestCommittedTransaction_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(4, getLatestCommittedTransaction());
    }
    if (latestCommittedBallotNumber_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, latestCommittedBallotNumber_);
    }
    if (clusterId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(6, clusterId_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.cse535.proto.PrepareRequest)) {
      return super.equals(obj);
    }
    org.cse535.proto.PrepareRequest other = (org.cse535.proto.PrepareRequest) obj;

    boolean result = true;
    result = result && (getBallotNumber()
        == other.getBallotNumber());
    result = result && getProcessId()
        .equals(other.getProcessId());
    result = result && (hasTransaction() == other.hasTransaction());
    if (hasTransaction()) {
      result = result && getTransaction()
          .equals(other.getTransaction());
    }
    result = result && (hasLatestCommittedTransaction() == other.hasLatestCommittedTransaction());
    if (hasLatestCommittedTransaction()) {
      result = result && getLatestCommittedTransaction()
          .equals(other.getLatestCommittedTransaction());
    }
    result = result && (getLatestCommittedBallotNumber()
        == other.getLatestCommittedBallotNumber());
    result = result && (getClusterId()
        == other.getClusterId());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + BALLOTNUMBER_FIELD_NUMBER;
    hash = (53 * hash) + getBallotNumber();
    hash = (37 * hash) + PROCESSID_FIELD_NUMBER;
    hash = (53 * hash) + getProcessId().hashCode();
    if (hasTransaction()) {
      hash = (37 * hash) + TRANSACTION_FIELD_NUMBER;
      hash = (53 * hash) + getTransaction().hashCode();
    }
    if (hasLatestCommittedTransaction()) {
      hash = (37 * hash) + LATESTCOMMITTEDTRANSACTION_FIELD_NUMBER;
      hash = (53 * hash) + getLatestCommittedTransaction().hashCode();
    }
    hash = (37 * hash) + LATESTCOMMITTEDBALLOTNUMBER_FIELD_NUMBER;
    hash = (53 * hash) + getLatestCommittedBallotNumber();
    hash = (37 * hash) + CLUSTERID_FIELD_NUMBER;
    hash = (53 * hash) + getClusterId();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.cse535.proto.PrepareRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.cse535.proto.PrepareRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.cse535.proto.PrepareRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.cse535.proto.PrepareRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.cse535.proto.PrepareRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code PrepareRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:PrepareRequest)
      org.cse535.proto.PrepareRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.cse535.proto.Pbft2Pc.internal_static_PrepareRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.cse535.proto.Pbft2Pc.internal_static_PrepareRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.cse535.proto.PrepareRequest.class, org.cse535.proto.PrepareRequest.Builder.class);
    }

    // Construct using org.cse535.proto.PrepareRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      ballotNumber_ = 0;

      processId_ = "";

      if (transactionBuilder_ == null) {
        transaction_ = null;
      } else {
        transaction_ = null;
        transactionBuilder_ = null;
      }
      if (latestCommittedTransactionBuilder_ == null) {
        latestCommittedTransaction_ = null;
      } else {
        latestCommittedTransaction_ = null;
        latestCommittedTransactionBuilder_ = null;
      }
      latestCommittedBallotNumber_ = 0;

      clusterId_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.cse535.proto.Pbft2Pc.internal_static_PrepareRequest_descriptor;
    }

    @java.lang.Override
    public org.cse535.proto.PrepareRequest getDefaultInstanceForType() {
      return org.cse535.proto.PrepareRequest.getDefaultInstance();
    }

    @java.lang.Override
    public org.cse535.proto.PrepareRequest build() {
      org.cse535.proto.PrepareRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.cse535.proto.PrepareRequest buildPartial() {
      org.cse535.proto.PrepareRequest result = new org.cse535.proto.PrepareRequest(this);
      result.ballotNumber_ = ballotNumber_;
      result.processId_ = processId_;
      if (transactionBuilder_ == null) {
        result.transaction_ = transaction_;
      } else {
        result.transaction_ = transactionBuilder_.build();
      }
      if (latestCommittedTransactionBuilder_ == null) {
        result.latestCommittedTransaction_ = latestCommittedTransaction_;
      } else {
        result.latestCommittedTransaction_ = latestCommittedTransactionBuilder_.build();
      }
      result.latestCommittedBallotNumber_ = latestCommittedBallotNumber_;
      result.clusterId_ = clusterId_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return (Builder) super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.cse535.proto.PrepareRequest) {
        return mergeFrom((org.cse535.proto.PrepareRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.cse535.proto.PrepareRequest other) {
      if (other == org.cse535.proto.PrepareRequest.getDefaultInstance()) return this;
      if (other.getBallotNumber() != 0) {
        setBallotNumber(other.getBallotNumber());
      }
      if (!other.getProcessId().isEmpty()) {
        processId_ = other.processId_;
        onChanged();
      }
      if (other.hasTransaction()) {
        mergeTransaction(other.getTransaction());
      }
      if (other.hasLatestCommittedTransaction()) {
        mergeLatestCommittedTransaction(other.getLatestCommittedTransaction());
      }
      if (other.getLatestCommittedBallotNumber() != 0) {
        setLatestCommittedBallotNumber(other.getLatestCommittedBallotNumber());
      }
      if (other.getClusterId() != 0) {
        setClusterId(other.getClusterId());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.cse535.proto.PrepareRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.cse535.proto.PrepareRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int ballotNumber_ ;
    /**
     * <code>int32 ballotNumber = 1;</code>
     */
    public int getBallotNumber() {
      return ballotNumber_;
    }
    /**
     * <code>int32 ballotNumber = 1;</code>
     */
    public Builder setBallotNumber(int value) {
      
      ballotNumber_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 ballotNumber = 1;</code>
     */
    public Builder clearBallotNumber() {
      
      ballotNumber_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object processId_ = "";
    /**
     * <pre>
     * processId is Server Name
     * </pre>
     *
     * <code>string processId = 2;</code>
     */
    public java.lang.String getProcessId() {
      java.lang.Object ref = processId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        processId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * processId is Server Name
     * </pre>
     *
     * <code>string processId = 2;</code>
     */
    public com.google.protobuf.ByteString
        getProcessIdBytes() {
      java.lang.Object ref = processId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        processId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * processId is Server Name
     * </pre>
     *
     * <code>string processId = 2;</code>
     */
    public Builder setProcessId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      processId_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * processId is Server Name
     * </pre>
     *
     * <code>string processId = 2;</code>
     */
    public Builder clearProcessId() {
      
      processId_ = getDefaultInstance().getProcessId();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * processId is Server Name
     * </pre>
     *
     * <code>string processId = 2;</code>
     */
    public Builder setProcessIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      processId_ = value;
      onChanged();
      return this;
    }

    private org.cse535.proto.Transaction transaction_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder> transactionBuilder_;
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public boolean hasTransaction() {
      return transactionBuilder_ != null || transaction_ != null;
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public org.cse535.proto.Transaction getTransaction() {
      if (transactionBuilder_ == null) {
        return transaction_ == null ? org.cse535.proto.Transaction.getDefaultInstance() : transaction_;
      } else {
        return transactionBuilder_.getMessage();
      }
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public Builder setTransaction(org.cse535.proto.Transaction value) {
      if (transactionBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        transaction_ = value;
        onChanged();
      } else {
        transactionBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public Builder setTransaction(
        org.cse535.proto.Transaction.Builder builderForValue) {
      if (transactionBuilder_ == null) {
        transaction_ = builderForValue.build();
        onChanged();
      } else {
        transactionBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public Builder mergeTransaction(org.cse535.proto.Transaction value) {
      if (transactionBuilder_ == null) {
        if (transaction_ != null) {
          transaction_ =
            org.cse535.proto.Transaction.newBuilder(transaction_).mergeFrom(value).buildPartial();
        } else {
          transaction_ = value;
        }
        onChanged();
      } else {
        transactionBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public Builder clearTransaction() {
      if (transactionBuilder_ == null) {
        transaction_ = null;
        onChanged();
      } else {
        transaction_ = null;
        transactionBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public org.cse535.proto.Transaction.Builder getTransactionBuilder() {
      
      onChanged();
      return getTransactionFieldBuilder().getBuilder();
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    public org.cse535.proto.TransactionOrBuilder getTransactionOrBuilder() {
      if (transactionBuilder_ != null) {
        return transactionBuilder_.getMessageOrBuilder();
      } else {
        return transaction_ == null ?
            org.cse535.proto.Transaction.getDefaultInstance() : transaction_;
      }
    }
    /**
     * <code>.Transaction transaction = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder> 
        getTransactionFieldBuilder() {
      if (transactionBuilder_ == null) {
        transactionBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder>(
                getTransaction(),
                getParentForChildren(),
                isClean());
        transaction_ = null;
      }
      return transactionBuilder_;
    }

    private org.cse535.proto.Transaction latestCommittedTransaction_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder> latestCommittedTransactionBuilder_;
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public boolean hasLatestCommittedTransaction() {
      return latestCommittedTransactionBuilder_ != null || latestCommittedTransaction_ != null;
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public org.cse535.proto.Transaction getLatestCommittedTransaction() {
      if (latestCommittedTransactionBuilder_ == null) {
        return latestCommittedTransaction_ == null ? org.cse535.proto.Transaction.getDefaultInstance() : latestCommittedTransaction_;
      } else {
        return latestCommittedTransactionBuilder_.getMessage();
      }
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public Builder setLatestCommittedTransaction(org.cse535.proto.Transaction value) {
      if (latestCommittedTransactionBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        latestCommittedTransaction_ = value;
        onChanged();
      } else {
        latestCommittedTransactionBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public Builder setLatestCommittedTransaction(
        org.cse535.proto.Transaction.Builder builderForValue) {
      if (latestCommittedTransactionBuilder_ == null) {
        latestCommittedTransaction_ = builderForValue.build();
        onChanged();
      } else {
        latestCommittedTransactionBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public Builder mergeLatestCommittedTransaction(org.cse535.proto.Transaction value) {
      if (latestCommittedTransactionBuilder_ == null) {
        if (latestCommittedTransaction_ != null) {
          latestCommittedTransaction_ =
            org.cse535.proto.Transaction.newBuilder(latestCommittedTransaction_).mergeFrom(value).buildPartial();
        } else {
          latestCommittedTransaction_ = value;
        }
        onChanged();
      } else {
        latestCommittedTransactionBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public Builder clearLatestCommittedTransaction() {
      if (latestCommittedTransactionBuilder_ == null) {
        latestCommittedTransaction_ = null;
        onChanged();
      } else {
        latestCommittedTransaction_ = null;
        latestCommittedTransactionBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public org.cse535.proto.Transaction.Builder getLatestCommittedTransactionBuilder() {
      
      onChanged();
      return getLatestCommittedTransactionFieldBuilder().getBuilder();
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    public org.cse535.proto.TransactionOrBuilder getLatestCommittedTransactionOrBuilder() {
      if (latestCommittedTransactionBuilder_ != null) {
        return latestCommittedTransactionBuilder_.getMessageOrBuilder();
      } else {
        return latestCommittedTransaction_ == null ?
            org.cse535.proto.Transaction.getDefaultInstance() : latestCommittedTransaction_;
      }
    }
    /**
     * <code>.Transaction latestCommittedTransaction = 4;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder> 
        getLatestCommittedTransactionFieldBuilder() {
      if (latestCommittedTransactionBuilder_ == null) {
        latestCommittedTransactionBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            org.cse535.proto.Transaction, org.cse535.proto.Transaction.Builder, org.cse535.proto.TransactionOrBuilder>(
                getLatestCommittedTransaction(),
                getParentForChildren(),
                isClean());
        latestCommittedTransaction_ = null;
      }
      return latestCommittedTransactionBuilder_;
    }

    private int latestCommittedBallotNumber_ ;
    /**
     * <code>int32 latestCommittedBallotNumber = 5;</code>
     */
    public int getLatestCommittedBallotNumber() {
      return latestCommittedBallotNumber_;
    }
    /**
     * <code>int32 latestCommittedBallotNumber = 5;</code>
     */
    public Builder setLatestCommittedBallotNumber(int value) {
      
      latestCommittedBallotNumber_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 latestCommittedBallotNumber = 5;</code>
     */
    public Builder clearLatestCommittedBallotNumber() {
      
      latestCommittedBallotNumber_ = 0;
      onChanged();
      return this;
    }

    private int clusterId_ ;
    /**
     * <code>int32 clusterId = 6;</code>
     */
    public int getClusterId() {
      return clusterId_;
    }
    /**
     * <code>int32 clusterId = 6;</code>
     */
    public Builder setClusterId(int value) {
      
      clusterId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 clusterId = 6;</code>
     */
    public Builder clearClusterId() {
      
      clusterId_ = 0;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:PrepareRequest)
  }

  // @@protoc_insertion_point(class_scope:PrepareRequest)
  private static final org.cse535.proto.PrepareRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.cse535.proto.PrepareRequest();
  }

  public static org.cse535.proto.PrepareRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<PrepareRequest>
      PARSER = new com.google.protobuf.AbstractParser<PrepareRequest>() {
    @java.lang.Override
    public PrepareRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new PrepareRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<PrepareRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<PrepareRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.cse535.proto.PrepareRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

