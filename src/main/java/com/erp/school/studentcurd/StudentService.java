package com.erp.school.studentcurd;


import com.erp.school.model.Teacher;
import com.erp.school.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student employee) {
        // Check if the employee already exists
        if (employee.getId() != null && studentRepository.existsById(employee.getId())) {
            // Fetch the existing employee to retain the current password
            Student existingEmployee = studentRepository.findById(employee.getId()).orElseThrow(() -> new RuntimeException("Employee not found"));
            if (employee.getPassword().equals(existingEmployee.getPassword())) {
                employee.setPassword(existingEmployee.getPassword());
            } else {
                // Encode the password for new employee
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            }
        }else {
            // Encode the password for new employee
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        employee = studentRepository.save(employee);

        return employee;
    }
    public List<Student> getStudentsByClassAndTenant(String studentClass, String tenantId) {
        return studentRepository.findByCurrentClassAndTenantId(studentClass, tenantId);
    }


    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Object getUserDetails(String role, Long id, String tenantId) {
        if ("teacher".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role)) {
            return teacherRepository.findById(id)
                    .filter(t -> t.getTenantId().equals(tenantId))
                    .orElse(null);
        } else if ("student".equalsIgnoreCase(role)) {
            return studentRepository.findById(id)
                    .filter(s -> s.getTenantId().equals(tenantId))
                    .orElse(null);
        } else {
            return "Invalid role";
        }
    }

}