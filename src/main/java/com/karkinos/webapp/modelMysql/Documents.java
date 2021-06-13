package com.karkinos.webapp.modelMysql;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity 
@Table(name = "document")
public class Documents {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long docId;
  private String docName;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("patient_id")
  private Patient patients;

  
    
  protected Documents() {
    
  }
 
    
    public Documents(String docName,Patient patients){
      this.docName=docName;
      this.patients = patients;

    }

    public Long getDocId() {
      return docId;
    }
    public void setDocId(Long docId) {
      this.docId = docId;
    }
    public String getDocName() {
      return docName;
    }
    public void setDocName(String docName) {
      this.docName = docName;
    }
    public Patient getPatients() {
      return patients;
    }
    public void setPatients(Patient patients) {
      this.patients = patients;
    }
    
    @Transient
    public String getDocsFilePath() {
        if (docName == null || docId == 0) return null;
        return "/patient-docs/" + patients.getId() + "/" ;
    }


    public Object getId() {
        return null;
    }


    public String getId(Patient id) {
        return null;
    }


    public String getByPatients(Patient id) {
        return null;
    }
}


