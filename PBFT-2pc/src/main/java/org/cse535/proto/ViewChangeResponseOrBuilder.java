// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pbft-2pc.proto

package org.cse535.proto;

public interface ViewChangeResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:ViewChangeResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 view = 1;</code>
   */
  int getView();

  /**
   * <code>string processId = 2;</code>
   */
  java.lang.String getProcessId();
  /**
   * <code>string processId = 2;</code>
   */
  com.google.protobuf.ByteString
      getProcessIdBytes();

  /**
   * <code>bool success = 3;</code>
   */
  boolean getSuccess();
}
