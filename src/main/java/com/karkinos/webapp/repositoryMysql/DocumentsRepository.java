package com.karkinos.webapp.repositoryMysql;

import java.util.List;

import com.karkinos.webapp.modelMysql.Documents;
import com.karkinos.webapp.modelMysql.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
 
public interface DocumentsRepository extends JpaRepository<Documents, Long> {

   List<Documents> findByPatients(Patient patient_id);

Documents findByDocId(Long id);
  


   
}