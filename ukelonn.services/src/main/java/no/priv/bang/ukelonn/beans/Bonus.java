/*
 * Copyright 2020 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ukelonn.beans;

import java.util.Date;

import no.priv.bang.beans.immutable.Immutable;

public class Bonus extends Immutable { // NOSONAR Immutable handles added fields
    int bonusId;
    private boolean enabled;
    private String iconurl;
    private String title;
    private String description;
    private double bonusFactor;
    private Date startDate;
    private Date endDate;

    public Bonus(int bonusId, boolean enabled, String iconurl, String title, String description, double bonusFactor, Date startDate, Date endDate) { // NOSONAR
        this.bonusId = bonusId;
        this.enabled = enabled;
        this.iconurl = iconurl;
        this.title = title;
        this.description = description;
        this.bonusFactor = bonusFactor;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Bonus() {
        // jackson require a no-args constructor
    }

    public int getBonusId() {
        return bonusId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getIconurl() {
        return iconurl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getBonusFactor() {
        return bonusFactor;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

}
