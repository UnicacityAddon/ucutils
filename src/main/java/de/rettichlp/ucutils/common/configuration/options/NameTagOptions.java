package de.rettichlp.ucutils.common.configuration.options;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class NameTagOptions {

    private boolean additionalBlacklist = true;
    private boolean additionalContract = true;
    private boolean additionalHouseban = true;
    private boolean additionalAfk = true;
    private boolean additionalMedicalInformation = true;
}
