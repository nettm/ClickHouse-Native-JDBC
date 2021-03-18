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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketBinaryReaderTest {

    @Test
    public void successfullyReadTwoIntValue() throws IOException {
        try (InputStream mockedInputStream = new ByteArrayInputStream(new byte[]{1, 0, 0, 0, 2, 0, 3, 4});
             SocketBinaryReader buffedReader = new SocketBinaryReader(mockedInputStream, 6)) {
            assertEquals(buffedReader.readByte(), 1);
            byte[] bytes = new byte[5];
            buffedReader.readBytes(bytes);
            assertEquals(bytes[0], 0);
            assertEquals(bytes[1], 0);
            assertEquals(bytes[2], 0);
            assertEquals(bytes[3], 2);
            assertEquals(bytes[4], 0);
            assertEquals(buffedReader.readByte(), 3);
            assertEquals(buffedReader.readByte(), 4);
        }
    }
}
