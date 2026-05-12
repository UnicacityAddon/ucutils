package de.rettichlp.ucutils.common.models;

import java.util.List;
import java.util.Map;

public record TeamResponse(List<TeamMember> ucTeam, Map<String, List<GroupMember>> groups) {

}
