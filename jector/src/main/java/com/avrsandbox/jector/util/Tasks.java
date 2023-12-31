/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, The AvrSandbox Project, Jector Framework
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.avrsandbox.jector.util;

import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.TaskExecutor;
import com.avrsandbox.jector.core.work.WorkerTask;

/**
 * Provides quick-use utilities for the jector framework.
 *
 * @author pavl_g
 */
@SuppressWarnings("unchecked")
public final class Tasks {

    private Tasks() {
    }

    /**
     * Retrieves a registered task-executor from its task binder instance.
     *
     * @param taskExecutorsManager the manager instance holding the TaskExecutors (non-nullable)
     * @param executor the represented executor to retrieve (non-nullable)
     * @return the executor object registered to this task manager (non-nullable)
     * @throws IllegalStateException if the task-executor is not found
     */
    public static TaskExecutor getTaskExecutorFromTaskBinder(TaskExecutorsManager taskExecutorsManager,
                                                             String executor) {
        TaskExecutor taskExecutor = taskExecutorsManager.getTaskExecutors()
                                                        .get(executor);
        if (taskExecutor == null) {
            throw new IllegalStateException(executor + " TaskExecutor is not found!");
        }
        return taskExecutor;
    }

    /**
     * Retrieves a worker task from its task-executor object (which is registered to
     * a task-manager).
     *
     * @param taskExecutorsManager the manager instance holding the TaskExecutors (non-nullable)
     * @param executor the represented executor holding the task to retrieve (non-nullable)
     * @param task the name of the task to retrieve (usually the name of the worker method) (non-nullable)
     * @return the executor object registered to this task-manager
     * @throws IllegalStateException if the worker-task to retrieve is not found
     */
    public static WorkerTask getWorkerTask(TaskExecutorsManager taskExecutorsManager,
                                           String executor,
                                           String task) {
        WorkerTask workerTask = getTaskExecutorFromTaskBinder(taskExecutorsManager, executor).getTasks()
                                        .get(task);
        if (workerTask == null) {
            throw new IllegalStateException(task + " WorkerTask is not found!");
        }

        return workerTask;
    }

    /**
     * Retrieves a worker task return value from its task-executor object (which is registered to
     * a task-manager).
     *
     * @param <T> a method generic to avoid further dynamic casting
     * @param taskExecutorsManager the manager instance holding the TaskExecutors
     * @param executor the represented executor holding the task to retrieve
     * @param task the name of the task to retrieve (usually the name of the worker method)
     * @return the return value of the retrieved task from its executor (nullable)
     */
    public static <T> T getWorkerTaskResult(TaskExecutorsManager taskExecutorsManager,
                                            String executor,
                                            String task) {
        return (T) getWorkerTask(taskExecutorsManager, executor, task).getResult();
    }
}
