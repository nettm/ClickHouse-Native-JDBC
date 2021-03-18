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

package com.github.housepower.protocol;

import com.github.housepower.client.NativeContext;
import com.github.housepower.exception.NotImplementedException;
import com.github.housepower.misc.ByteBufHelper;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

public interface Response extends ByteBufHelper {

    ByteBufHelper helper = new ByteBufHelper() {
    };

    ProtoType type();

    static Response readFrom(ByteBuf buf, @Nullable NativeContext.ServerContext info) {
        switch ((int) helper.readVarInt(buf)) {
            case 0:
                return HelloResponse.readFrom(buf);
            case 1:
                return DataResponse.readFrom(buf, info);
            case 2:
                return ExceptionResponse.readFrom(buf);
            case 3:
                return ProgressResponse.readFrom(buf);
            case 4:
                return PongResponse.readFrom(buf);
            case 5:
                return EOFStreamResponse.readFrom(buf);
            case 6:
                return ProfileInfoResponse.readFrom(buf);
            case 7:
                return TotalsResponse.readFrom(buf, info);
            case 8:
                return ExtremesResponse.readFrom(buf, info);
            case 9:
                throw new NotImplementedException("RESPONSE_TABLES_STATUS_RESPONSE");
            default:
                throw new IllegalStateException("Accept the id of response that is not recognized by Client.");
        }
    }

    enum ProtoType {
        RESPONSE_HELLO(0),
        RESPONSE_DATA(1),
        RESPONSE_EXCEPTION(2),
        RESPONSE_PROGRESS(3),
        RESPONSE_PONG(4),
        RESPONSE_END_OF_STREAM(5),
        RESPONSE_PROFILE_INFO(6),
        RESPONSE_TOTALS(7),
        RESPONSE_EXTREMES(8),
        RESPONSE_TABLES_STATUS_RESPONSE(9);

        private final int id;

        ProtoType(int id) {
            this.id = id;
        }

        public long id() {
            return id;
        }
    }
}
