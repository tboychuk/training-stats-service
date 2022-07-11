package com.bobocode.participants.controller;

import com.bobocode.participants.dto.PersonStatsRequest;
import com.bobocode.participants.dto.Stats;
import com.bobocode.participants.exception.PersonAlreadyExistsException;
import com.bobocode.participants.service.TrainingStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Log4j2
@RestController
@RequestMapping("/training/stats")
@RequiredArgsConstructor
public class TrainingStatsController {
    private final TrainingStatsService trainingStatsService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPersonStats(@RequestBody @Valid PersonStatsRequest person) {
        log.info("Received request: " + person);
        trainingStatsService.addParticipant(httpServletRequest.getRemoteAddr(), person);
    }

    @GetMapping
    public Stats getStats() {
        return trainingStatsService.computeStats();
    }

    @GetMapping("/{ip}")
    public PersonStatsRequest getByIp(@PathVariable String ip) {
        return trainingStatsService.getByIp(ip);
    }

    @GetMapping("/map")
    public Map<String, PersonStatsRequest> getInternalMap() {
        return trainingStatsService.getInternalMap();
    }

    @DeleteMapping("/{ip}")
    public PersonStatsRequest removeByIp(@PathVariable String ip) {
        return trainingStatsService.removeByIp(ip);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .collect(toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(PersonAlreadyExistsException.class)
    public Error handleDuplicatePost(PersonAlreadyExistsException e) {
        return new Error(e.getMessage());
    }

    record Error(String message) {
    }

}
