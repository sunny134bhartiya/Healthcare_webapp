package com.karkinos.webapp.controllerMysql;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.google.gson.Gson;
import com.karkinos.webapp.modelMysql.Documents;
import com.karkinos.webapp.modelMysql.Patient;
import com.karkinos.webapp.repositoryMysql.DocumentsRepository;
import com.karkinos.webapp.repositoryMysql.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

 
@Controller
public class PatientController {
 
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DocumentsRepository documentsRepository;

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
        @RequestParam String pincode) {

            if (bindingResult.hasErrors()) {       
        
                System.out.println(bindingResult);
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("new_patient");
                return modelAndView;
            }
            
        patientRepository.save(new Patient(patient.getFirstName(), patient.getLastName(), patient.getAge(), patient.getGender(), patient.getCity(), patient.getPincode(),patient.getPhotos(), patient.getDocuments()));
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

    @RequestMapping(path="/search_patient",method=RequestMethod.GET)
    public ModelAndView search_patient(@RequestParam String firstName) 
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search_patient_result");
        modelAndView.addObject("patients", patientRepository.findByFirstName(firstName));
        return modelAndView;
    }
//*******************************************************//
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditPatientPage(@PathVariable(name = "id") Long id) {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("edit_patient");
    modelAndView.addObject("patient", patientRepository.findById(id));
    modelAndView.addObject("id", id);
    return modelAndView;
    }

    @RequestMapping(path = "/update/{id}",method=RequestMethod.POST)
    public ModelAndView updatePatient(@ModelAttribute("patient") Patient patient, @PathVariable Long id)
        {
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
 //*******************************************************//   
    @RequestMapping("/delete/{id}")
    public String deletePatient(@PathVariable(name = "id") Long id) {
    patientRepository.deleteById(id);
    return "redirect:/";       
    }
//*******************************************************//
    @RequestMapping(path="/upload_image/{id}",method = RequestMethod.GET)
    public ModelAndView showupload_pic_page(@PathVariable(name = "id") Long id) {
   
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("upload_image");
    modelAndView.addObject("patient", patientRepository.findById(id));
    modelAndView.addObject("id", id);
    return modelAndView;
    }

    @RequestMapping(path="/photos/add/{id}",method=RequestMethod.POST)
    public String savePatientpic(Patient patient,
    @RequestParam("image") MultipartFile multipartFile, 
    RedirectAttributes ra,
    @PathVariable(name = "id") Long id,
    Model model) 
    
    throws IOException {
    
    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    Optional<Patient> patientData = patientRepository.findById(id);
    Patient _patient = patientData.get();
        _patient.setPhotos(fileName);


        _patient.setFirstName(_patient.getFirstName());
        _patient.setLastName(_patient.getLastName());
        _patient.setAge(_patient.getAge());
        _patient.setGender(_patient.getGender());
        _patient.setCity(_patient.getCity());
        _patient.setPincode(_patient.getPincode());


        Patient savedPatient = patientRepository.save(_patient);
 
        String uploadDir = "./patient-photos/" + savedPatient.getId();
        
       Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);}

        try(InputStream inputStream = multipartFile.getInputStream()){
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    
        return "image_upload_message";
     }
//*******************************************************//    
    @RequestMapping(path="/docs/{id}",method = RequestMethod.GET)
    public ModelAndView doc_upload(@PathVariable(name = "id") Long id) {
   
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("upload_doc");
    modelAndView.addObject("patient", patientRepository.findById(id));
    modelAndView.addObject("id", id);
    return modelAndView;
    }
//*******************************************************//    
    @RequestMapping(path="/docs/add/{id}",method=RequestMethod.POST)
    public ModelAndView savedoc(@ModelAttribute Documents documents,
    @RequestParam("document") MultipartFile multipartFile,
    @PathVariable (name = "id") Patient id) 
    throws IOException{
 
   
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        documentsRepository.save(new Documents(fileName, id));
       List<Documents> documentsData = documentsRepository.findByPatients(id);
     
        Documents _documents = documentsData.get(0);
        Documents savedDocuments = documentsRepository.save(_documents);

        String uploadDir = "./patient-docs/" + savedDocuments.getPatients().getId();

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);}

        try(InputStream inputStream = multipartFile.getInputStream()){
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new IOException("Could not save file: " + fileName, ioe);
        }

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("file_upload_message");

              
        return modelAndView;
    }
    //***********************************************************************************/

    @RequestMapping(path = "/preview/{id}",method=RequestMethod.GET)
    public String previewProfile(@ModelAttribute("documents") Documents documents, @PathVariable("id") Patient id, @ModelAttribute("patient") Patient patient)
        {
        
            List<Documents> documentsData = documentsRepository.findByPatients(id);
           
            if (documentsData.isEmpty()){
                return "redirect:/patient_details/{id}";
        }   
        else{
            return "redirect:/view_patient_doc/{id}";
       }       
          
    }
    //************************************************ */
    @RequestMapping(path = "/view_patient_doc/{id}",method=RequestMethod.GET)
    public ModelAndView viewProfileDoc(@ModelAttribute("documents") Documents documents, @PathVariable Patient id,@ModelAttribute("patient") Patient patient)
        {
            ModelAndView modelAndView = new ModelAndView();
            List<Documents> documentsData = documentsRepository.findByPatients(id);
           
                Optional<Patient> patientData = patientRepository.findById(documentsData.get(0).getPatients().getId());

        modelAndView.setViewName("view_patient_doc");
        modelAndView.addObject("PhotosImagePath",patientData.get().getPhotosImagePath());
        modelAndView.addObject("photos", patientData.get().getPhotosImagePath());
        modelAndView.addObject("firstName", patientData.get().getFirstName());
        modelAndView.addObject("lastName",patientData.get().getLastName());
        modelAndView.addObject("age", patientData.get().getAge());
        modelAndView.addObject("gender", patientData.get().getGender());
        modelAndView.addObject("city", patientData.get().getCity());
        modelAndView.addObject("pincode", patientData.get().getPincode());
        modelAndView.addObject("id", patientData.get().getId());
        List<String> docs = new ArrayList<>();
        for(Documents docList : documentsData ) 
        {
            docs.add(docList.getDocName());    
        modelAndView.addObject("docs", docs);
        }
            return modelAndView;
    }

    @RequestMapping(path = "/patient_details/{id}",method=RequestMethod.GET)
    public ModelAndView viewProfile(@PathVariable Long id,@ModelAttribute("patient") Patient patient)
        {
            ModelAndView modelAndView = new ModelAndView();

           
                Optional<Patient> patientData = patientRepository.findById(id);
        
        modelAndView.setViewName("patient_details");
        modelAndView.addObject("PhotosImagePath",patientData.get().getPhotosImagePath());
        modelAndView.addObject("photos", patientData.get().getPhotosImagePath());
        modelAndView.addObject("firstName", patientData.get().getFirstName());
        modelAndView.addObject("lastName",patientData.get().getLastName());
        modelAndView.addObject("age", patientData.get().getAge());
        modelAndView.addObject("gender", patientData.get().getGender());
        modelAndView.addObject("city", patientData.get().getCity());
        modelAndView.addObject("pincode", patientData.get().getPincode());
        modelAndView.addObject("id", patientData.get().getId());
            return modelAndView;
    }

    
    //******************************************************** */
    @RequestMapping(path = "/downloads/{id}/{doc}",method=RequestMethod.GET)
    public void downloadDoc(HttpServletResponse response,@PathVariable Patient id,@PathVariable String doc,@ModelAttribute("patient") Patient patient) 
    throws Exception{
              List<Documents> result = documentsRepository.findByPatients(id);
        Documents documents = result.get(0);
       
        File file = new File("/workspace/webapp_with_mysql/." + documents.getDocsFilePath() + doc);

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

    @RequestMapping("/upload")
    public void upload() throws IOException {
        FileReader reader = new FileReader("/workspace/Healthcare_webapp/src/main/resources/Patient.json");
        BufferedReader br = new BufferedReader(reader);
        StringBuffer sbr = new StringBuffer();
        String line;
        
        while((line = br.readLine()) != null){
          Gson gson = new Gson();
          Patient patient = gson.fromJson(line, Patient.class);
          patientRepository.save(patient);

        }
    }

}
