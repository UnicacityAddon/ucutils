package de.rettichlp.ucutils.common.models;

import java.util.UUID;

public record TeamMember(int id, String username, UUID uuid, String rank, boolean isOnline) {

}
