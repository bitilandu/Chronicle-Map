/*
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.hash.serialization.internal;

import net.openhft.chronicle.hash.serialization.BytesInterop;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.hashing.Hasher;
import net.openhft.lang.io.Bytes;

public enum ByteArrayMarshaller implements BytesInterop<byte[]>, BytesReader<byte[]> {
    INSTANCE;

    @Override
    public long size(byte[] ba) {
        return ba.length;
    }

    @Override
    public boolean startsWith(Bytes bytes, byte[] ba) {
        if (bytes.capacity() - bytes.position() < (long) ba.length)
            return false;
        long pos = bytes.position();
        for (int i = 0; i < ba.length; i++) {
            if (bytes.readByte(pos + i) != ba[i])
                return false;
        }
        return true;
    }

    @Override
    public long hash(byte[] ba) {
        return Hasher.hash(ba);
    }

    @Override
    public void write(Bytes bytes, byte[] ba) {
        bytes.write(ba);
    }

    @Override
    public byte[] read(Bytes bytes, long size) {
        byte[] ba = new byte[resLen(size)];
        bytes.read(ba);
        return ba;
    }

    @Override
    public byte[] read(Bytes bytes, long size, byte[] toReuse) {
        int resLen = resLen(size);
        if (toReuse == null || resLen != toReuse.length)
            toReuse = new byte[resLen];
        bytes.read(toReuse);
        return toReuse;
    }

    private int resLen(long size) {
        if (size < 0L || size > (long) Integer.MAX_VALUE)
            throw new IllegalArgumentException("byte[] size should be non-negative int, " +
                    size + " given. Memory corruption?");
        return (int) size;
    }
}
