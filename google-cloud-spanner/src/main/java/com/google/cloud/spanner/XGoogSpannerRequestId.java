/*
 * Copyright 2024 Google LLC
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

import java.security.SecureRandom;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;


public class XGoogSpannerRequestId implements ClientInterceptor {
    // 1. Generate the random process Id singleton.
    public static long RAND_PROCESS_ID = XGoogSpannerRequestId.generateRandProcessId();
    public static long VERSION = 1; // The version of the specification being implemented.

    private static long generateRandProcessId() {
        byte[] rBytes = new byte[8];
        SecureRandom srng = new SecureRandom();
        srng.nextBytes(rBytes);
        long result = 0L;
        result |= long(rBytes[7]) | long(rBytes[6])<<8 | long(rBytes[5])<<16 | long(rBytes[4])<<24 | long(rBytes[3])<<32 | long(rBytes[2])<<40 | long(rBytes[1])<<48 | long(rBytes[0])<<56;
        return result;
    }

    /*
    * format joins the respective fields with the "." separator.
    */
    public String format(long nthClientId, long nthChannelId, long nthRequest, long attempt) {
        return String.format("%d.%d.%d.%d.%d.%d", this.VERSION, this.RAND_PROCESS_ID, nthClientId, nthChannelId, nthRequest, attempt);
    }

    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel next) {
        return next.newCall(methodDescriptor, callOptions);
    }
}
