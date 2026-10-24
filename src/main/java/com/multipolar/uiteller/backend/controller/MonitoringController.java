package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.MonitoringLog;
import com.multipolar.uiteller.backend.service.MonitoringLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/apiv2")
public class MonitoringController {

    @Autowired
    private MonitoringLogService monitoringLogService;

    @GetMapping("monitoring/{user}")
    public List<MonitoringLog> monitoringLogList(@PathVariable String user) {
        return monitoringLogService.userMonitoringLog(user);
    }

    @GetMapping("monitoringAdmin")
    public List<MonitoringLog> monitoringLogAdminList() {
        return monitoringLogService.adminMonitoringLog();
    }

    @GetMapping("waitingUpdate/{id}/{user}")
    public List<MonitoringLog> waitingUpdate(@PathVariable Long id, @PathVariable String user) {
        monitoringLogService.waitingStatus(id);
        return monitoringLogService.userMonitoringLog(user);
    }
}
