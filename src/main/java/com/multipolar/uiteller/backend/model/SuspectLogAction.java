package com.multipolar.uiteller.backend.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class SuspectLogAction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;

    private Date actionDate;

    private String userid;

    private String action;

    private String status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suspect_log_monitoring_id")
    private SuspectLogMonitoring suspectLogMonitoring;

    public SuspectLogAction() {}

    public SuspectLogAction(Long id, Date actionDate, String userid, String action, String status, SuspectLogMonitoring suspectLogMonitoring) {
        this.id = id;
        this.actionDate = actionDate;
        this.userid = userid;
        this.action = action;
        this.status = status;
        this.suspectLogMonitoring = suspectLogMonitoring;
    }
}
