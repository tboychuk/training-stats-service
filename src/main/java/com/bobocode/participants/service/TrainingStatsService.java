package com.bobocode.participants.service;

import com.bobocode.participants.dto.PersonStatsRequest;
import com.bobocode.participants.dto.PersonStatsResponse;
import com.bobocode.participants.dto.Stats;
import com.bobocode.participants.dto.TeamStats;
import com.bobocode.participants.exception.PersonAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class TrainingStatsService {
    private final Map<String, PersonStatsRequest> ipToParticipantMap = new ConcurrentHashMap<>();

    public void addParticipant(String ip, PersonStatsRequest person) {
        if (ipToParticipantMap.containsKey(ip)) {
            throw new PersonAlreadyExistsException();
        } else {
            ipToParticipantMap.put(ip, person);
        }
    }

    public Map<String, PersonStatsRequest> getInternalMap() {
        return ipToParticipantMap;
    }

    public PersonStatsRequest removeByIp(String ip) {
        return ipToParticipantMap.remove(ip);
    }

    public PersonStatsRequest getByIp(String ip) {
        return ipToParticipantMap.get(ip);
    }

    public Stats computeStats() {
        if (ipToParticipantMap.isEmpty()) {
            return Stats.emptyStats();
        }
        var teamToParticipantsMap = ipToParticipantMap.values()
                .stream()
                .collect(groupingBy(PersonStatsRequest::team, toList()));
        var teamStatsList = teamToParticipantsMap.keySet()
                .stream()
                .map(createTeamStats(teamToParticipantsMap))
                .sorted(comparing(this::teamOrder))
                .toList();
        var averageDays = calculateAverage(ipToParticipantMap.values(), PersonStatsRequest::trainingDaysPerWeek);
        var averageMinutes = calculateAverage(ipToParticipantMap.values(), PersonStatsRequest::minutesPerTrainingDay);
        return new Stats(teamStatsList, ipToParticipantMap.size(), averageDays, averageMinutes);
    }

    private int teamOrder(TeamStats teamStats) {
        return switch (teamStats.team()) {
            case "Petros" -> 1;
            case "Hoverla" -> 2;
            case "Blyznytsia" -> 3;
            case "Breskul" -> 4;
            case "Svydovets" -> 5;
            default -> throw new IllegalStateException("Illegal team name");
        };
    }

    private Function<String, TeamStats> createTeamStats(Map<String, List<PersonStatsRequest>> teamToParticipantsMap) {
        return team -> {
            List<PersonStatsResponse> teamMembers = teamToParticipantsMap.get(team)
                    .stream()
                    .map(this::convertToParticipantStats)
                    .toList();
            BigDecimal averageDays = calculateAverage(teamToParticipantsMap.get(team), PersonStatsRequest::trainingDaysPerWeek);
            BigDecimal averageMinutes = calculateAverage(teamToParticipantsMap.get(team), PersonStatsRequest::minutesPerTrainingDay);
            return new TeamStats(team, teamMembers, teamMembers.size(), averageDays, averageMinutes);
        };
    }

    private PersonStatsResponse convertToParticipantStats(PersonStatsRequest p) {
        return new PersonStatsResponse(
                p.firstName(),
                p.lastName(),
                p.trainingDaysPerWeek(),
                p.minutesPerTrainingDay()
        );
    }

    private BigDecimal calculateAverage(Collection<PersonStatsRequest> teamMembers, ToIntFunction<PersonStatsRequest> mapper) {
        var averageMotivation = teamMembers.stream()
                                        .mapToInt(mapper)
                                        .sum() / (double) teamMembers.size();
        return BigDecimal.valueOf(averageMotivation).setScale(2, RoundingMode.HALF_UP);
    }
}
