package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
	Job findByJobCode(String s);
	Job findByCoreJob(String s);
}
