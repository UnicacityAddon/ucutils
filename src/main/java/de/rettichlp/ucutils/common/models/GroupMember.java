package de.rettichlp.ucutils.common.models;

import java.util.UUID;

public record GroupMember(int id, String username, UUID uuid, boolean isLeader, boolean isOnline) {

}
