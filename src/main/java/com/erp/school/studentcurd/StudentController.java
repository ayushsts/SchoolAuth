package com.erp.school.studentcurd;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents(@RequestHeader("tenant") String tenant) {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id, @RequestHeader("tenant") String tenant) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-class")
    public ResponseEntity<?> getStudentsByClass(
            @RequestParam String studentClass,
            @RequestParam String role,
            @RequestHeader("tenant") String tenant) {

        if (!role.equalsIgnoreCase("teacher") && !role.equalsIgnoreCase("admin")) {
            return ResponseEntity.status(403).body("Access denied: Only teacher or admin can access this.");
        }

        List<Student> students = studentService.getStudentsByClassAndTenant(studentClass, tenant);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student, @RequestHeader("tenant") String tenant) {
        student.setTenantId(tenant);
        return studentService.saveStudent(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id, @RequestHeader("tenant") String tenant) {
        if (studentService.getStudentById(id).isPresent()) {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(
            @RequestParam String role,
            @RequestParam Long id,
            @RequestHeader("tenant") String tenant) {


        Object result = studentService.getUserDetails(role, id, tenant);

        if (result == null) {
            return ResponseEntity.status(404).body("User not found or unauthorized access");
        } else if (result instanceof String && result.equals("Invalid role")) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }
}