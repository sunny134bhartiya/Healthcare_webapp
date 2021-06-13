package com.karkinos.webapp.repository;


import java.util.List;

import com.karkinos.webapp.model.Doctor;

import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;

public interface DoctorRepository extends MongoRepository<Doctor, String> {


    List<Doctor> findBySpecializationAndCity(String specialization, String city);
    List<Doctor> findAll();
  
}

