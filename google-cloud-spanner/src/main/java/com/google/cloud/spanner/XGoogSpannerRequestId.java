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

import java.security.SecureRandom;

public class XGoogSpannerRequestId {
  // 1. Generate the random process Id singleton.
  public static long RAND_PROCESS_ID = XGoogSpannerRequestId.generateRandProcessId();
  public static long VERSION = 1; // The version of the specification being implemented.

  private static long generateRandProcessId() {
    byte[] rBytes = new byte[8];
    SecureRandom srng = new SecureRandom();
    srng.nextBytes(rBytes);
    long result =
        rBytes[7]
            | rBytes[6] << 8
            | rBytes[5] << 16
            | rBytes[4] << 24
            | rBytes[3] << 32
            | rBytes[2] << 40
            | rBytes[1] << 48
            | rBytes[0] << 56;
    return result;
  }

  /*
   * format joins the respective fields with the "." separator.
   */
  public String format(long nthClientId, long nthChannelId, long nthRequest, long attempt) {
    return String.format(
        "%d.%d.%d.%d.%d.%d",
        this.VERSION, this.RAND_PROCESS_ID, nthClientId, nthChannelId, nthRequest, attempt);
  }
}
