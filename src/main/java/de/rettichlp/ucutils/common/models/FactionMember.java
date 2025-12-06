package de.rettichlp.ucutils.common.models;

import java.util.UUID;

public record FactionMember(int id, String username, UUID uuid, int rankNumber, String rankName, boolean isLeader, String gender) {

}
