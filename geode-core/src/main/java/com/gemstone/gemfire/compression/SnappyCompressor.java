/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gemstone.gemfire.compression;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;


import org.iq80.snappy.CorruptionException;
import org.iq80.snappy.Snappy;

import com.gemstone.gemfire.internal.i18n.LocalizedStrings;

/**
 * An implementation of {@link Compressor} for Google's Snappy compression
 * codec. Utilizes the xerial java-snappy wrapper.
 * 
 * @since GemFire 8.0
 */
public final class SnappyCompressor implements Compressor, Serializable {
  private static final long serialVersionUID = 496609875302446099L;
  
  // It's possible to create more than one, but there only needs to be a single
  // instance in the VM.
  private static final AtomicReference<SnappyCompressor> defaultInstance = new AtomicReference<SnappyCompressor>();
  
  /**
   * Create a new instance of the SnappyCompressor.
   * @throws IllegalStateException when the Snappy native library is unavailable
   */
  public SnappyCompressor() {
  }
  
  /**
   * Get the single, default instance of the SnappyCompressor.
   */
  public static final SnappyCompressor getDefaultInstance() {
    SnappyCompressor instance = defaultInstance.get();
    if (instance != null) {
      return instance;
    }

    defaultInstance.compareAndSet(null, new SnappyCompressor());
    return defaultInstance.get();
  }

  @Override
  public byte[] compress(byte[] input) {
      return Snappy.compress(input);
  }

  @Override
  public byte[] decompress(byte[] input) {
    try {
      return Snappy.uncompress(input, 0, input.length);
    } catch (CorruptionException e) {
      throw new CompressionException(e);
    }
  }
  
  @Override
  public int hashCode() {
    return this.getClass().getName().hashCode();
  }
  
  @Override
  public boolean equals (final Object other) {
    if (other == null) {
      return false;
    }
    
    return this.getClass().getName().equals(other.getClass().getName());
  }
}
