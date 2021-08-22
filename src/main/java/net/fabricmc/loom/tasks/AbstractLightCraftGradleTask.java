/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2020 FabricMC
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

package net.fabricmc.loom.tasks;

import io.github.nickacpt.lightcraft.gradle.ConstantsKt;
import io.github.nickacpt.lightcraft.gradle.ExtensionsKt;
import io.github.nickacpt.lightcraft.gradle.LightCraftGradleExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;

public abstract class AbstractLightCraftGradleTask extends DefaultTask {
    public AbstractLightCraftGradleTask() {
        setGroup(ConstantsKt.LIGHTCRAFT_TASK_GROUP);
    }

    @Internal
    protected LightCraftGradleExtension getExtension() {
        return ExtensionsKt.getLightCraftExtension(getProject());
    }
}
