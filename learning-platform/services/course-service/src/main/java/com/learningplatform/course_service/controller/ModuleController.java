package com.learningplatform.course_service.controller;

import com.learningplatform.course_service.dto.CreateModuleRequest;
import com.learningplatform.course_service.dto.ModuleResponse;
import com.learningplatform.course_service.dto.UpdateModuleRequest;
import com.learningplatform.course_service.service.ModuleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(
            ModuleService moduleService) {

        this.moduleService = moduleService;
    }

    /*
     * CREATE MODULE
     *
     * Instructor/Admin
     */
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<ModuleResponse> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateModuleRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    moduleService.createModule(
                        courseId,
                        request
                    )
                );
    }

    /*
     * GET MODULES FOR COURSE
     */
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleResponse>>
    getModulesByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                moduleService
                        .getModulesByCourse(courseId));
    }

    /*
     * GET MODULE
     */
    @GetMapping("/modules/{id}")
    public ResponseEntity<ModuleResponse> getModule(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                moduleService.getModule(id));
    }

    /*
     * UPDATE MODULE
     */
    @PutMapping("/modules/{id}")
    public ResponseEntity<ModuleResponse> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateModuleRequest request) {

        return ResponseEntity.ok(
                moduleService.updateModule(
                        id,
                        request));
    }

    /*
     * DELETE MODULE
     */
    @DeleteMapping("/modules/{id}")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Long id) {

        moduleService.deleteModule(id);

        return ResponseEntity.noContent().build();
    }
}