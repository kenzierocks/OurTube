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
package me.kenzierocks.ourtube;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.corundumstudio.socketio.AckRequest;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import me.kenzierocks.ourtube.response.Response;

public class AsyncService {

    private static final Logger LOGGER = Log.get();

    public static final ListeningExecutorService GENERIC = MoreExecutors.listeningDecorator(
            new ThreadPoolExecutor(
                    5,
                    50,
                    5, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(),
                    new ThreadFactoryBuilder().setDaemon(true).setNameFormat("generic-%d").build()));

    public static <T> void ackCallable(String logId, AckRequest ack, Callable<T> callable) {
        ackFuture(logId, ack, Response.from(GENERIC.submit(callable)));
    }

    public static <T> void ackFuture(String logId, AckRequest ack, ListenableFuture<Response<T>> future) {
        CompletableFuture<Response<T>> resultContainer = new CompletableFuture<>();
        Futures.addCallback(future, new FutureCallback<Response<T>>() {

            @Override
            public void onSuccess(Response<T> result) {
                resultContainer.complete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                LOGGER.warn("Unhandled error in " + logId, t);
            }
        }, GENERIC);
        // apparently we cannot call this async, so we do it now!
        Response<T> response = Futures.getUnchecked(resultContainer);
        ack.sendAckData(response);
    }

}