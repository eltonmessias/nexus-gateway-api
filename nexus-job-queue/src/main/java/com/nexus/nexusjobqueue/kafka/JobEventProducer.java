package com.nexus.nexusjobqueue.kafka;

import com.nexus.nexuscommons.event.JobCompletedEvent;
import com.nexus.nexuscommons.event.JobCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public JobEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishJobCreatedEvent(JobCreatedEvent event) {
        kafkaTemplate.send("job.created", event);
    }

    public void publishJobCompleted(JobCompletedEvent event) {
        kafkaTemplate.send("job.completed", event);
    }
}
