package com.erp.school.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class TeacherAttendanceController {
    @Autowired
    private TeacherAttendanceService attendanceService;

    @PostMapping("/mark")
    public ResponseEntity<TeacherAttendance> markAttendance(
            @RequestParam Long teacherId,
            @RequestParam String status,
            @RequestParam String date) {
        LocalDate attendanceDate = LocalDate.parse(date);
        return ResponseEntity.ok(attendanceService.markAttendance(teacherId, attendanceDate, status));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlyAttendanceSummary> getMonthlyAttendanceSummary(
            @RequestParam Long teacherId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(attendanceService.calculateMonthlyAttendance(teacherId, year, month));
    }
}