package com.nexus.nexusgateway.controller;

import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexusfeatureflags.model.Flag;
import com.nexus.nexusfeatureflags.service.FlagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flags")
public class FlagController {
    private final FlagService flagService;

    @Autowired
    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }

    @GetMapping("/{flagKey}/{environment}")
    public ResponseEntity<FlagEvaluateResponse> evaluateFlag(
            @PathVariable String flagKey,
            @PathVariable String environment){
        return ResponseEntity.ok(flagService.evaluate(flagKey, environment));
    }

    @GetMapping
    public ResponseEntity<List<Flag>> getAllFlags(){
        return ResponseEntity.ok(flagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flag> getFlagById(@PathVariable UUID id){
        return ResponseEntity.ok(flagService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Flag> createFlag(@Valid @RequestBody Flag flag){
        return ResponseEntity.status(HttpStatus.CREATED).body(flagService.create(flag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flag> updateFlag(@PathVariable UUID id, @Valid @RequestBody Flag flag){
        return ResponseEntity.ok(flagService.update(id, flag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlag(@PathVariable UUID id){
        flagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
