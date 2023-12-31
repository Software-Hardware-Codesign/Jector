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

package com.avrsandbox.jector.core.thread;

import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.WorkerTask;
import com.avrsandbox.jector.core.work.TaskExecutor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the base implementation of a task executor that provides the thread-based implementation, the dependent (receiver)
 * object in this DI framework.
 * 
 * <p> Annotated methods with {@link com.avrsandbox.jector.core.command.ExecuteOn} inside a {@link com.avrsandbox.jector.core.work.Worker}
 * are submitted as {@link WorkerTask}s to be executed on the specified implementations of the {@link com.avrsandbox.jector.core.work.TaskExecutor},
 * the task executors are specified by annotating their names in the array {@link com.avrsandbox.jector.core.command.ExecuteOn#executors()}.
 *
 * @author pavl_g
 */
public class AppThread extends Thread implements TaskExecutor {

    /**
     * A Thread-Safe modifiable map of tasks wrapping the methods to be bound to their specified annotated methods.
     */
    protected final Map<String, WorkerTask> tasks = new ConcurrentHashMap<>();

    /**
     * A flag to order the executor for termination.
     */
    protected volatile boolean terminate;

    /**
     * A flag to order the executor to start running.
     */
    protected volatile boolean active = false;

    /**
     * Instantiates an app thread object. 
     * 
     * @param name the name of the thread
     */
    public AppThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (!isTerminated()){
            if (!isActive()) {
                continue;
            }
            executeTasks(0);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void startExecutorService(TaskExecutorsManager taskExecutorsManager) {
        start();
    }

    @Override
    public void destructExecutorService(TaskExecutorsManager taskExecutorsManager) {
        this.terminate = true;
        setActive(false);
        TaskExecutor.super.destructExecutorService(taskExecutorsManager);
    }

    @Override
    public void executeTasks(Object arguments) {
        try {
            for (String task : tasks.keySet()) {
                if (tasks.get(task) == null || !tasks.get(task).isActive()) {
                    continue;
                }
                /* Saves the result of the execution order! */
                tasks.get(task).setResult(tasks.get(task).call());
                /* Triggers for a single run */
                tasks.get(task).setActive(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, WorkerTask> getTasks() {
        return tasks;
    }

    @Override
    public boolean isTerminated() {
        return terminate;
    }
}
