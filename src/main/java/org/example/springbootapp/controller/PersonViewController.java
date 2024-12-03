package org.example.springbootapp.controller;

import org.example.springbootapp.domain.entity.Person;
import org.example.springbootapp.service.PersonService;
import org.example.springbootapp.service.FileService;
import org.example.springbootapp.validator.AddGroup;
import org.example.springbootapp.validator.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class PersonViewController {
    private final PersonService personService;
    private final FileService fileService;

    @Autowired
    public PersonViewController(PersonService personService, FileService fileService) {
        this.personService = personService;
        this.fileService = fileService;
    }

    @ModelAttribute("totalEmployees")
    public int populateEmployeeCount() {
        return personService.getAllEmployees().size();
    }

    @ModelAttribute("employeeCountries")
    public List<String> populateEmployeeCountries() {
        return personService.getEmployeeCountries();
    }

    @ModelAttribute("currencySummary")
    public Map<String, BigDecimal> populateCurrencySummary() {
        return personService.getCurrencySalaryPairs();
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "filterCountry", required = false) String country, Model model) {
        List<Person> employees;
        if (country != null && !country.isEmpty()) {
            employees = personService.getEmployeesFromCountry(country);
        } else {
            employees = personService.getAllEmployees();
        }
        model.addAttribute("employees", employees);
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Person());
        }
        model.addAttribute("country", country);
        return "employees/list";
    }

    @GetMapping("/details/{id}")
    public String employeeDetails(@PathVariable int id, Model model) {
        Optional<Person> employee = personService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
        } else {
            model.addAttribute("error", "Employee not found");
            return "employees/error";
        }
        return "employees/details";
    }

    @PostMapping("/add")
    public String addEmployee(@Validated(AddGroup.class) @ModelAttribute("employee") Person employee,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @RequestParam("image") MultipartFile image) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Adding new employee failed");
            return "employees/list";
        }

        try {
            if (!image.isEmpty()) {
                String fileName = fileService.saveFile(image);
                employee.setImagePath(fileName);
            }
            personService.addEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "New employee has been added successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Invalid image format. Only JPG/PNG are allowed.");
        } catch (Exception e) {
            model.addAttribute("employees", personService.getAllEmployees());
            redirectAttributes.addFlashAttribute("errorMessage", "Adding new employee failed");
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable int id, Model model) {
        Optional<Person> employee = personService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("employee", employee.get());
        } else {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Employee not found");
        }
        return "employees/list";
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") Integer id,
                                 @Validated(UpdateGroup.class) @ModelAttribute("employee") Person employee,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam("image") MultipartFile image) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Failed to update employee");
            return "employees/list";
        }
        try {
            employee.setId(id);
            if (!image.isEmpty()) {
                String fileName = fileService.saveFile(image);
                employee.setImagePath(fileName);
            }
            personService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Invalid image format. Only JPG/PNG are allowed.");
        } catch (Exception e) {
            model.addAttribute("employees", personService.getAllEmployees());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update employee");
        }
        return "redirect:/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            personService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee has been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deleting employee failed");
        }
        return "redirect:/employees";
    }

    @GetMapping("/image/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Resource file = fileService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/images/zip")
    public ResponseEntity<Resource> exportPhotos(@RequestParam (value = "format", required = false) String format) {
        try {
            // Generate the ZIP file and return its path
            Path zipFilePath = personService.exportPhotosToZip();

            // Load the ZIP file as a Resource
            Resource zipResource = fileService.loadFileAsResource(zipFilePath.getFileName().toString());

            // Set headers to indicate a file download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employee_photos.zip\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipResource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/upload")
    public String uploadCsvWithEmployees(
            @RequestParam("csvFile") MultipartFile csvFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            RedirectAttributes redirectAttributes) {
        try {
            // Ensure the CSV file is not empty
            if (csvFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No CSV file provided");
                return "redirect:/employees";
            }

            // Save the CSV file
            String csvFileName = fileService.saveCSVFile(csvFile);

            // Process image files, if provided
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile imageFile : imageFiles) {
                    if (!imageFile.isEmpty()) {
                        fileService.saveFile(imageFile);
                    }
                }
            }

            // Reload employees from the uploaded CSV
            personService.reloadEmployeesFromCsv(csvFileName);

            if (!personService.getValidationErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Importing employees failed due to errors in CSV file format");
                return "redirect:/employees/validation-errors";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Employees and images have been imported successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual error
            redirectAttributes.addFlashAttribute("errorMessage", "Importing employees failed: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    @PostMapping("/export/csv")
    public ResponseEntity<Resource> exportEmployeesToCSV(@RequestParam List<String> columns) {
        try {
            String csvContent = personService.exportEmployeesToCSV(columns);
            ByteArrayResource resource = new ByteArrayResource(csvContent.getBytes(StandardCharsets.UTF_8));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employees.csv\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
            return ResponseEntity.ok().headers(headers).body(resource);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/validation-errors")
    public ResponseEntity<InputStreamResource> downloadValidationErrors() {
        // Collect validation errors into a string
        String validationResults = personService.collectValidationResults(personService.getValidationErrors());

        // Convert the string into an InputStream
        InputStream inputStream = new ByteArrayInputStream(validationResults.getBytes());

        // Set up headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=validation_errors.txt");

        // Return the file as a response
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(inputStream));
    }
}
