package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.SnumUnamePwdPendidikan;
import com.multipolar.uiteller.backend.repository.SnumUnamePwdPendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snumUnamePwdPendidikan")
public class SnumUnamePwdPendidikanController {

    @Autowired
    private SnumUnamePwdPendidikanRepository snumUnamePwdPendidikanRepository;

    @GetMapping("/findById/{archiveNumber}")
    public SnumUnamePwdPendidikan findById(@PathVariable String archiveNumber){
        return snumUnamePwdPendidikanRepository.findOne(archiveNumber);
    }

}