package com.postech.challenge.application.observability;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.postech.challenge.application.dto.StatusHistoricoPonto;
import com.postech.challenge.domain.model.StatusOrdemServico;

public final class TempoMedioStatusCalculator {

    private static final Set<StatusOrdemServico> TRACKED_STATUSES = EnumSet.of(
            StatusOrdemServico.EM_DIAGNOSTICO,
            StatusOrdemServico.EM_EXECUCAO,
            StatusOrdemServico.FINALIZADA);

    public Map<StatusOrdemServico, Double> averageMinutes(List<StatusHistoricoPonto> points, LocalDateTime now) {
        Map<StatusOrdemServico, List<Double>> samples = new EnumMap<>(StatusOrdemServico.class);
        TRACKED_STATUSES.forEach(status -> samples.put(status, new ArrayList<>()));

        Map<UUID, List<StatusHistoricoPonto>> byOrder = points.stream()
                .collect(Collectors.groupingBy(StatusHistoricoPonto::ordemServicoId, LinkedHashMap::new, Collectors.toList()));

        for (List<StatusHistoricoPonto> timeline : byOrder.values()) {
            timeline.sort(Comparator.comparing(StatusHistoricoPonto::alteradoEm));
            for (int index = 0; index < timeline.size(); index++) {
                StatusHistoricoPonto current = timeline.get(index);
                if (!TRACKED_STATUSES.contains(current.status())) {
                    continue;
                }
                LocalDateTime end = index + 1 < timeline.size() ? timeline.get(index + 1).alteradoEm() : now;
                double minutes = Duration.between(current.alteradoEm(), end).toMillis() / 60_000.0;
                if (minutes >= 0) {
                    samples.get(current.status()).add(minutes);
                }
            }
        }

        Map<StatusOrdemServico, Double> averages = new EnumMap<>(StatusOrdemServico.class);
        for (StatusOrdemServico status : TRACKED_STATUSES) {
            List<Double> values = samples.get(status);
            if (values.isEmpty()) {
                averages.put(status, null);
            } else {
                averages.put(status, values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            }
        }
        return averages;
    }
}
