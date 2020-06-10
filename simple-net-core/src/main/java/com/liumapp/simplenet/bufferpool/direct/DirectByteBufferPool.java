/*
 * MIT License
 *
 * Copyright (c) 2019 Jacob Glickman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.liumapp.simplenet.bufferpool.direct;

import com.liumapp.simplenet.bufferpool.ByteBufferPool;

import java.nio.ByteBuffer;

/**
 * Represents a pool of {@code DirectByteBuffer}s.
 *
 * @author Jacob G.
 * @since February 23, 2019
 */
public final class DirectByteBufferPool extends ByteBufferPool {

    @Override
    protected ByteBuffer create(int n) {
        return ByteBuffer.allocateDirect(n);
    }

    @Override
    public void give(ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("A HeapByteBuffer cannot be given to a DirectByteBufferPool!");
        }

        super.give(buffer);
    }

}
