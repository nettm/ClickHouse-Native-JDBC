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

import com.github.housepower.serde.BinarySerializer;
import com.github.housepower.serde.LegacyBinarySerializer;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class ColumnWriterBuffer implements AutoCloseable {

    private final ByteBufBinaryWriter columnWriter;

    public BinarySerializer column;

    public ColumnWriterBuffer() {
        this.columnWriter = new ByteBufBinaryWriter();
        this.column = new LegacyBinarySerializer(columnWriter, false, null);
    }

    public void writeTo(BinarySerializer serializer) throws IOException {
        ByteBuf buf = columnWriter.getBuf();
        serializer.writeBytes(buf);
    }

    @Override
    public void close() {
        column.close();
    }
}
