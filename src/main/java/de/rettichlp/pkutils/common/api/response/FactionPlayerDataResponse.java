package de.rettichlp.pkutils.common.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class FactionPlayerDataResponse {

    private final String minecraftName;

    private final UUID minecraftUuid;

    private final String faction;

    private final int rank;

    private final Map<String, Long> activityCount = new HashMap<>();

    private final Map<String, Long> equipCount = new HashMap<>();
}
