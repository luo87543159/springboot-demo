/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camel.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ReST controller to manage user tasks
 *
 */
@Slf4j
@RestController
public class TaskManagementController {

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskAdminRuntime taskAdminRuntime;

    /**
     * 分配给当前登录用户的任务
     */
    @GetMapping("/my-tasks")
    public List<Task> getMyTasks() {
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        log.info("> My Available Tasks: " + tasks.getTotalItems());

        for (Task task : tasks.getContent()) {
            log.info("\t > My User Task: " + task);
        }

        return tasks.getContent();
    }

    /**
     * 当我们想要查看在所有活动流程实例中分配的所有任务时，我们还可以添加ReST调用。
     * 可用于支持和管理目的，例如当您想要代表某人重新分配任务或完成任务时。
     * 此调用将需要管理员凭据（即我们需要以具有ROLE_ACTIVITI_ADMIN的用户身份登录）。
     * 我们还需要使用名为TaskAdminRuntime的不同运行时API。
     */
    @GetMapping("/all-tasks")
    public List<Task> getAllTasks() {
        Page<Task> tasks = taskAdminRuntime.tasks(Pageable.of(0, 10));
        log.info("> All Available Tasks: " + tasks.getTotalItems());

        for (Task task : tasks.getContent()) {
            log.info("\t > User Task: " + task);
        }

        return tasks.getContent();
    }

    /**
     * 完成结束自己任务
     * @param taskId
     * @return
     */
    @RequestMapping("/complete-task")
    public String completeTask(@RequestParam(value="taskId") String taskId) {
        taskRuntime.complete(TaskPayloadBuilder.complete()
                .withTaskId(taskId).build());
        log.info(">>> Completed Task: " + taskId);

        return "Completed Task: " + taskId;
    }

}
