package com.karkinos.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.karkinos.webapp.model.Patient;
import com.karkinos.webapp.repository.PatientRepository;

import javax.servlet.ServletOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@EnableAutoConfiguration
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @RequestMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");

        return modelAndView;
    }

    @RequestMapping("/new_patient")
    public ModelAndView new_patient() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("new_patient");

        return modelAndView;
    }

    @RequestMapping(path="/create_new_patient",method=RequestMethod.POST)
    public ModelAndView create_new_patient(@Valid @ModelAttribute("patient") Patient patient, BindingResult bindingResult,
    @RequestParam String firstName,
    @RequestParam String lastName,
    @RequestParam Integer age,
    @RequestParam String gender,
    @RequestParam String city,
    @RequestParam Integer pincode) {
        if (bindingResult.hasErrors()) {       
            System.out.println(bindingResult);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("new_patient");
            return modelAndView;
        }
        patientRepository.save(new Patient(patient.getFirstName(), patient.getLastName(), patient.getAge(), patient.getGender(), patient.getCity(), patient.getPincode(), patient.getPhotos(), patient.getDocs()));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submit_patient");
        modelAndView.addObject("firstName", firstName);
        modelAndView.addObject("lastName", lastName);
        modelAndView.addObject("age", age);
        modelAndView.addObject("gender", gender);
        modelAndView.addObject("city", city);
        modelAndView.addObject("pincode", pincode);

        return modelAndView;
    }

    @RequestMapping(path="/search_patient_form",method=RequestMethod.GET)
    public ModelAndView search_patient_form() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search_patient_form");

        return modelAndView;
    }

    @RequestMapping(path="/search_patient",method=RequestMethod.GET)
    public ModelAndView search_patient(@RequestParam String firstName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search_patient_result");
        modelAndView.addObject("patients", patientRepository.findByFirstName(firstName));

        return modelAndView;
    }

    @RequestMapping(path = "/view_all_patient/{page}", method = RequestMethod.GET)
    public ModelAndView view_all_patient(@PathVariable("page") Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        ModelAndView modelAndView = new ModelAndView();
        Page<Patient> patients = patientRepository.findAll(pageable);
        modelAndView.setViewName("view_all_patient");
        modelAndView.addObject("patients", patients.getContent());
        modelAndView.addObject("number", patients.getNumber()+1);
        modelAndView.addObject("totalPages", patients.getTotalPages());
        modelAndView.addObject("currentPage" , page);
    
        return modelAndView;
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditPatientPage(@PathVariable(name = "id") String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit_patient");
        modelAndView.addObject("patient", patientRepository.findById(id));
        modelAndView.addObject("id", id);

        return modelAndView;
    }

    @RequestMapping(path = "/update/{id}",method=RequestMethod.POST)
    public ModelAndView updatePatient(@ModelAttribute("patient") Patient patient, @PathVariable String id) {
        Optional<Patient> patientData = patientRepository.findById(id);
        Patient _patient = patientData.get();
        _patient.setFirstName(patient.getFirstName());
        _patient.setLastName(patient.getLastName());
        _patient.setAge(patient.getAge());
        _patient.setGender(patient.getGender());
        _patient.setCity(patient.getCity());
        _patient.setPincode(patient.getPincode());
        patientRepository.save(_patient);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submit_patient");
        modelAndView.addObject("firstName", _patient.getFirstName());
        modelAndView.addObject("lastName", _patient.getLastName());
        modelAndView.addObject("age", _patient.getAge());
        modelAndView.addObject("gender", _patient.getGender());
        modelAndView.addObject("city", _patient.getCity());
        modelAndView.addObject("pincode", _patient.getPincode());
    
        return modelAndView;
    }
    
    @RequestMapping("/delete/{id}")
    public String deletePatient(@PathVariable(name = "id") String id) {
        patientRepository.deleteById(id);

        return "redirect:/"; 
    } 
    
    @RequestMapping(path="/upload_image/{id}",method = RequestMethod.GET)
    public ModelAndView showupload_image_page(@PathVariable(name = "id") String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upload_image");
        modelAndView.addObject("patient", patientRepository.findById(id));
        modelAndView.addObject("id", id);

        return modelAndView;
    }

    @RequestMapping(path="/photos/add/{id}",method=RequestMethod.POST)
    public String savePatientpic(Patient patient, @RequestParam("image") MultipartFile multipartFile, @PathVariable(name = "id") String id, Model model) 
    throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Optional<Patient> patientData = patientRepository.findById(id);
        Patient _patient = patientData.get();
        _patient.setPhotos(fileName);
        patient.setFirstName(_patient.getFirstName());
        _patient.setLastName(_patient.getLastName());
        _patient.setAge(_patient.getAge());
        _patient.setGender(_patient.getGender());
        _patient.setCity(_patient.getCity());
        _patient.setPincode(_patient.getPincode());
        Patient savedPatient = patientRepository.save(_patient);
        String uploadDir = "./patient-photos/" + savedPatient.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
        return "image_upload_message";
    }

    @RequestMapping(path="/docs/{id}",method = RequestMethod.GET)
    public ModelAndView upload_doc(@PathVariable(name = "id") String id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("upload_doc");
        modelAndView.addObject("patient", patientRepository.findById(id));
        modelAndView.addObject("id", id);

        return modelAndView;
    }
    
    @RequestMapping(path="/docs/add/{id}",method=RequestMethod.POST)
    public String savePatientdoc(Patient patient, @RequestParam("document") MultipartFile[] multipartFiles, @PathVariable(name = "id") String id, Model model) 
    throws IOException {
        Optional<Patient> patientData = patientRepository.findById(id);
        Patient _patient = patientData.get();
        _patient.setFirstName(_patient.getFirstName());
        _patient.setLastName(_patient.getLastName());
        _patient.setAge(_patient.getAge());
        _patient.setGender(_patient.getGender());
        _patient.setCity(_patient.getCity());
        _patient.setPincode(_patient.getPincode());
        _patient.setPhotos(_patient.getPhotos());

        List<String> fileNames = new ArrayList<String>();
        for(MultipartFile multipartFile: multipartFiles) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            fileNames.add(fileName);
        }
        if(_patient.getDocs() == null) {
            _patient.setDocs(fileNames);
        }
        else {
            for(String name : fileNames) {
                _patient.getDocs().add(name);
            }
            _patient.setDocs(_patient.getDocs());
        }
        Patient savedPatient = patientRepository.save(_patient);
        String uploadDir = "./patient-docs/" + savedPatient.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try {
            int count = 0;
            for (String file : fileNames) {
                InputStream inputStream = multipartFiles[count++].getInputStream();
                Path filePath = uploadPath.resolve(file);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ioe) {        
            throw new IOException("Could not save file: " + ioe);
        }
        return "file_upload_message";
    }

    @RequestMapping(path = "/patient_details/{id}",method=RequestMethod.GET)
    public ModelAndView patient_detaits(@ModelAttribute("patient") Patient patient, @PathVariable String id) {
        Optional<Patient> patientData = patientRepository.findById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("patient_details");
        modelAndView.addObject("PhotosImagePath",patientData.get().getPhotosImagePath());
        modelAndView.addObject("photos", patientData.get().getPhotos());
        modelAndView.addObject("firstName", patientData.get().getFirstName());
        modelAndView.addObject("lastName",patientData.get().getLastName());
        modelAndView.addObject("age", patientData.get().getAge());
        modelAndView.addObject("gender", patientData.get().getGender());
        modelAndView.addObject("city", patientData.get().getCity());
        modelAndView.addObject("pincode", patientData.get().getPincode());
        modelAndView.addObject("id", patientData.get().getId());
        modelAndView.addObject("docs", patientData.get().getDocs());
        System.out.println("final view");
        System.out.println(patient.getId());

        return modelAndView;
        
    }

    @RequestMapping(path = "/downloads/{id}/{doc}",method=RequestMethod.GET)
    public void downloadDoc(HttpServletResponse response,@PathVariable String id, @PathVariable String doc) 
    throws Exception {
        Optional<Patient> result = patientRepository.findById(id);
        Patient patient = result.get();
        File file = new File("/workspace/webapp/." + patient.getDocsFilePath() + doc);
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + doc;
        response.setHeader(headerKey,headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[8192];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outputStream.close();
    }
}