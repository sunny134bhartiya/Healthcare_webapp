package com.karkinos.webapp.repositoryMongo;


import java.util.List;

import com.karkinos.webapp.modelMongo.Doctor;

import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;

public interface DoctorRepository extends MongoRepository<Doctor, String> {


    List<Doctor> findBySpecializationAndCity(String specialization, String city);
    List<Doctor> findAll();
  
}

