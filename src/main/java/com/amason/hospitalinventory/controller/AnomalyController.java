package com.amason.hospitalinventory.controller;

import com.amason.hospitalinventory.dto.AnomalyResult;
import com.amason.hospitalinventory.service.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyController {

    @Autowired
    private AnomalyDetectionService anomalyDetectionService;

    @GetMapping
    public List<AnomalyResult> getAnomalies() {
        return anomalyDetectionService.detectAnomalies();
    }
}
