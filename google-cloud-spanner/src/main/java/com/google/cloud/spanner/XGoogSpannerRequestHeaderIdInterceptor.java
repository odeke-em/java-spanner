/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusException;

public class XGoogSpannerRequestHeaderIdInterceptor implements ClientInterceptor {
  public static final Metadata.Key<String> X_GOOG_SPANNER_REQUEST_ID_KEY =
      Metadata.Key.of("x-goog-spanner-request-id", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    // Examine the call options for the availability of the request-id.
    return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        String reqID =
            headers.get(XGoogSpannerRequestHeaderIdInterceptor.X_GOOG_SPANNER_REQUEST_ID_KEY);
        boolean reqIdIsEmpty = reqID == null || reqID.isEmpty();
        if (reqIdIsEmpty) {
          super.start(responseListener, headers);
          return;
        }

        // Since we've now received the injected header
        // we can now inject it into any returned errors.
        try {
          super.start(responseListener, headers);
        } catch (StatusException e) {
          // Add the request-id to the Exception.
        }
      }
    };
  }

  public void injectHeader(String requestID) {}
}

/*
 * Notes:
 * + Inject headers with an interceptor: https://grpc.github.io/grpc-java/javadoc/io/grpc/stub/MetadataUtils.html#newAttachHeadersInterceptor(io.grpc.Metadata)
 */
