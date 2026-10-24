package com.multipolar.uiteller.backend.repository;

import com.multipolar.uiteller.backend.model.JurusanPendidikan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JurusanPendidikanRepository extends JpaRepository<JurusanPendidikan, Long> {

    @Modifying
    @Query(value="INSERT INTO jurusan (created_at, kode_jurusan, nama_jurusan, pendidikan_id) VALUES (sysdate, :kode_jurusan,:nama_jurusan,:pendidikan_id)", nativeQuery = true)
    void insertJurusan(@Param("kode_jurusan") String kode_jurusan, @Param("nama_jurusan") String nama_jurusan,
                       @Param("pendidikan_id") Integer status);

    @Modifying
    @Query(value="update jurusan set updated_at=sysdate, kode_jurusan=:kode_jurusan, nama_jurusan=:nama_jurusan, pendidikan_id=:pendidikan_id where id=:id", nativeQuery = true)
    void updateJurusan(@Param("kode_jurusan") String kode_jurusan, @Param("nama_jurusan") String nama_jurusan,
                       @Param("pendidikan_id") Integer status,
                       @Param("id") Integer id);
    @Modifying
    @Query(value="delete from jurusan where id=:id", nativeQuery = true)
    void deleteJurusan(@Param("id") Integer id);


}
