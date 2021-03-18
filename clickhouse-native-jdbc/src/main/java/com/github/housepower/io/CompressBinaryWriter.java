/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.io;

import com.github.housepower.misc.ClickHouseCityHash;
import com.github.housepower.misc.CodecHelper;
import io.airlift.compress.Compressor;

import static com.github.housepower.settings.ClickHouseDefines.CHECKSUM_LENGTH;
import static com.github.housepower.settings.ClickHouseDefines.COMPRESSION_HEADER_LENGTH;

public class CompressBinaryWriter implements BinaryWriter, CodecHelper {

    private final int capacity;
    private final byte[] writtenBuf;
    private final BinaryWriter writer;
    private final Compressor compressor;

    private int position;

    public CompressBinaryWriter(int capacity, BinaryWriter writer, Compressor compressor) {
        this.capacity = capacity;
        this.writtenBuf = new byte[capacity];
        this.writer = writer;
        this.compressor = compressor;
    }

    @Override
    public void writeByte(byte byt) {
        writtenBuf[position++] = byt;
        flush(false);
    }

    @Override
    public void writeBytes(byte[] bytes) {
        writeBytes(bytes, 0, bytes.length);
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length) {
        while (remaining() < length) {
            int num = remaining();
            System.arraycopy(bytes, offset, writtenBuf, position, remaining());
            position += num;

            flush(false);
            offset += num;
            length -= num;
        }

        System.arraycopy(bytes, offset, writtenBuf, position, length);
        position += length;
        flush(false);
    }

    @Override
    public void flush(boolean force) {
        if (position > 0 && (force || !hasRemaining())) {
            int maxLen = compressor.maxCompressedLength(position);

            byte[] compressedBuffer = new byte[maxLen + COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH];
            int compressedDataLen = compressor.compress(writtenBuf, 0, position, compressedBuffer, COMPRESSION_HEADER_LENGTH + CHECKSUM_LENGTH, compressedBuffer.length);

            compressedBuffer[CHECKSUM_LENGTH] = (byte) (0x82 & 0xFF); // TODO not sure if it works for zstd
            int compressedSize = compressedDataLen + COMPRESSION_HEADER_LENGTH;
            System.arraycopy(getBytesLE(compressedSize), 0, compressedBuffer, CHECKSUM_LENGTH + 1, Integer.BYTES);
            System.arraycopy(getBytesLE(position), 0, compressedBuffer, CHECKSUM_LENGTH + Integer.BYTES + 1, Integer.BYTES);

            long[] checksum = ClickHouseCityHash.cityHash128(compressedBuffer, CHECKSUM_LENGTH, compressedSize);
            System.arraycopy(getBytesLE(checksum[0]), 0, compressedBuffer, 0, Long.BYTES);
            System.arraycopy(getBytesLE(checksum[1]), 0, compressedBuffer, Long.BYTES, Long.BYTES);

            writer.writeBytes(compressedBuffer, 0, compressedSize + CHECKSUM_LENGTH);
            position = 0;
        }
    }

    private boolean hasRemaining() {
        return position < capacity;
    }

    private int remaining() {
        return capacity - position;
    }
}
