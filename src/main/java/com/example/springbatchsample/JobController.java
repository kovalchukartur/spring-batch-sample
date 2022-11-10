package com.example.springbatchsample;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class JobController {

    private final Job simpleJob;
    private final SimpleJobOperator simpleJobOperator;

    public JobController(Job simpleJob,
                         SimpleJobOperator simpleJobOperator) {
        this.simpleJob = simpleJob;
        this.simpleJobOperator = simpleJobOperator;
    }

    @GetMapping("/runJob{name}")
    public long runJob(@PathVariable(required = false) String name) throws Exception {
        String jobName = Optional.ofNullable(name)
            .filter(s -> !s.isBlank())
            .orElseGet(simpleJob::getName);

        String parameter = Instant.now().toString();
        JobParameter jobParameter = new JobParameter(parameter);
        Map<String, JobParameter> parameters = Map.of("time", jobParameter);

        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();
        runIdIncrementer.setKey(parameter);
        JobParameters test = runIdIncrementer.getNext(new JobParameters(parameters));
        Long jobId = simpleJobOperator.start(jobName, test.toString());
        log.warn("Run job with id = {}", jobId);
        return jobId;
    }

    @GetMapping("/checkStatus/{jobId}")
    public String checkStatus(@PathVariable Long jobId) throws Exception {
        return simpleJobOperator.getSummary(jobId);
    }
}
