package de.rettichlp.pkutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlacklistReason {

    private final String reason;
    private final boolean outlaw;
    private int kills;
    private int price;
}
