package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.JurusanPendidikan;
import com.multipolar.uiteller.backend.model.JurusanPendidikanModify;
import com.multipolar.uiteller.backend.model.Pendidikan;
import com.multipolar.uiteller.backend.repository.JurusanPendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class JurusanPendidikanController {

    @Autowired
    private JurusanPendidikanRepository jurusanPendidikanRepository;

    @GetMapping("/jurusan-pendidikan")
    public List<JurusanPendidikan> getAll() {
        return jurusanPendidikanRepository.findAll();
    }

    @Transactional
    @PostMapping("/jurusan-pendidikan")
    public ResponseEntity create(@RequestBody JurusanPendidikanModify template) {

        jurusanPendidikanRepository.insertJurusan(template.kodeJurusan, template.namaJurusan,template.pendidikanId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/jurusan-pendidikan/{id}")
    public JurusanPendidikan findById(@PathVariable Long id) {
        return jurusanPendidikanRepository.findOne(id);
    }

    @Transactional
    @PostMapping("/jurusan-pendidikan/{id}")
    public List<JurusanPendidikan> update(@PathVariable Long id, @RequestBody JurusanPendidikanModify template) {
        jurusanPendidikanRepository.updateJurusan(template.kodeJurusan, template.namaJurusan,template.pendidikanId,template.id);
        return jurusanPendidikanRepository.findAll();
    }

    @Transactional
    @DeleteMapping("/jurusan-pendidikan/{id}")
    public List<JurusanPendidikan> delete(@PathVariable Integer id) {
        jurusanPendidikanRepository.deleteJurusan(id);
        return jurusanPendidikanRepository.findAll();
    }


}
