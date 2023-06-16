package com.subhafx.loggerservice.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class FileDeletingTasklet implements Tasklet, InitializingBean {

    private Resource[] resources;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        /*
         * Cleanup: Deleting files post job completion.
         * Ignoring if file doesn't exist.
         */
        for(Resource r: resources) {
            r.getFile().delete();
        }
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resources, "directory must be set");
    }
}
