/*
 * This file is part of OurTube-serverj, licensed under the MIT License (MIT).
 *
 * Copyright (c) Kenzie Togami (kenzierocks) <https://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.kenzierocks.ourtube.netty;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableSortedSet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.kenzierocks.ourtube.OurTube;
import me.kenzierocks.ourtube.rpc.RpcClient;
import me.kenzierocks.ourtube.rpc.RpcClientImpl;
import sx.blah.discord.handle.obj.IGuild;

public class RpcClientRegistry {

    private final Map<Channel, RpcClient> clientCache = new ConcurrentHashMap<>();

    public void register(Channel channel, String userId, String token, ImmutableSortedSet<IGuild> guilds) {
        clientCache.put(channel, RpcClientImpl.builder()
                .id(channel.id().asLongText())
                .userId(userId)
                .token(token)
                .guilds(guilds)
                .callFunction(call -> {
                    String callJson;
                    try {
                        callJson = OurTube.MAPPER.writeValueAsString(call);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    channel.writeAndFlush(new TextWebSocketFrame(callJson));
                }).build());
    }

    public void remove(Channel channel) {
        clientCache.remove(channel);
    }

    public RpcClient getClient(ChannelHandlerContext ctx) {
        return checkNotNull(clientCache.get(ctx.channel()));
    }

}
