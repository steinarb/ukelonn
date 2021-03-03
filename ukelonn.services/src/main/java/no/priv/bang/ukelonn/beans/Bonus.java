/*
 * Copyright 2020-2021 Steinar Bang
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

    private Bonus() { }

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

    public static BonusBuilder with() {
        return new BonusBuilder();
    }

    public static class BonusBuilder {

        private int bonusId;
        private boolean enabled;
        private String iconurl;
        private String title;
        private String description;
        private double bonusFactor;
        private Date startDate;
        private Date endDate;

        private BonusBuilder() { }

        public Bonus build() {
            Bonus bonus = new Bonus();
            bonus.bonusId = this.bonusId;
            bonus.enabled = this.enabled;
            bonus.iconurl = this.iconurl;
            bonus.title = this.title;
            bonus.description = this.description;
            bonus.bonusFactor = this.bonusFactor;
            bonus.startDate = this.startDate;
            bonus.endDate = this.endDate;
            return bonus;
        }

        public BonusBuilder bonusId(int bonusId) {
            this.bonusId = bonusId;
            return this;
        }

        public BonusBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public BonusBuilder iconurl(String iconurl) {
            this.iconurl = iconurl;
            return this;
        }

        public BonusBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BonusBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BonusBuilder bonusFactor(double bonusFactor) {
            this.bonusFactor = bonusFactor;
            return this;
        }

        public BonusBuilder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public BonusBuilder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }
    }
}
