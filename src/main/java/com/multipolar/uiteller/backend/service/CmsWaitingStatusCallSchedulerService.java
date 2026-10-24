package com.multipolar.uiteller.backend.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multipolar.uiteller.backend.model.SuspectLogMonitoring;
import com.multipolar.uiteller.backend.model.SysParam;
import com.multipolar.uiteller.backend.repository.SuspectLogMonitoringRepository;
import com.multipolar.uiteller.backend.repository.SysParamRepository;

@Service
public class CmsWaitingStatusCallSchedulerService implements SchedulingConfigurer {

	@Autowired
	private ThreadPoolTaskScheduler taskScheduler;

	@Autowired
	private SysParamRepository sysParamRepository;
	
	@Autowired
	private SuspectLogMonitoringService suspectService;
	
	@Autowired
    private SuspectLogMonitoringRepository suspectLogMonitoringRepository;

	private static final String CALL_STATUS_SCHEDULE_PARAM = "webteller.teller.cms.statusCall.schedule";

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskScheduler);
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				CmsWaitingStatusCallSchedulerService.this.refreshCmsWaitingStatus();
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Calendar nextExecutionTime = new GregorianCalendar();
				Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
				nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
				nextExecutionTime.add(Calendar.MILLISECOND,
						CmsWaitingStatusCallSchedulerService.this.getConfigSchedulerValue());
				return nextExecutionTime.getTime();
			}
		});
	}

	@Transactional
	private void refreshCmsWaitingStatus() {
		List<SuspectLogMonitoring> suspectLogs = suspectLogMonitoringRepository.findByStatusAndToday();
		for (SuspectLogMonitoring log : suspectLogs) {
			suspectService.waitingStatusSuspect(log.getId());
		}
	}


	@Bean
	public Integer getConfigSchedulerValue() {
		Integer scheduleValue = 10000;
		SysParam delayStatusCallParam = sysParamRepository.findByPropertyName(CALL_STATUS_SCHEDULE_PARAM);
		if (delayStatusCallParam == null || !StringUtils.isNumeric(delayStatusCallParam.getPropertyValue()))
			Log.warn("CMS call schedule value not set in System Parameter Table, default to 10s");
		else
			scheduleValue = Integer.parseInt(delayStatusCallParam.getPropertyValue());

		return scheduleValue;
	}

}
