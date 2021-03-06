/*
 * This file is part of OurTube-serverj, licensed under the MIT License (MIT).
 *
 * Copyright (c) Octavia Togami (octylFractal) <https://octyl.net>
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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SongData {

    @AutoValue
    public abstract static class Thumbnail {

        public static Thumbnail of(long height, long width, String url) {
            return new AutoValue_SongData_Thumbnail(height, width, url);
        }

        Thumbnail() {
        }

        public abstract long getHeight();

        public abstract long getWidth();

        public abstract String getUrl();

    }

    public static Builder builder() {
        return new AutoValue_SongData.Builder();
    }

    @AutoValue.Builder
    public interface Builder {

        Builder id(String id);

        Builder name(String name);

        Builder thumbnail(Thumbnail thumbnail);

        Builder duration(long duration);

        SongData build();

    }

    SongData() {
    }

    public abstract String getId();

    public abstract String getName();

    public abstract Thumbnail getThumbnail();

    public abstract long getDuration();

}
