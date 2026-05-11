package de.rettichlp.ucutils.common.configuration.options;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class NameTagOptions {

    private boolean aDuty = true;
    private boolean afk = true;
    private boolean houseBan = true;
    private boolean outlaw = true;
    private boolean medicalInformation = true;
}
