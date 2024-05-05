/*
 * Copyright 2020-2024 Steinar Bang
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

public record Bonus(
    int bonusId,
    boolean enabled,
    String iconurl,
    String title,
    String description,
    double bonusFactor,
    Date startDate,
    Date endDate)
{

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int bonusId;
        private boolean enabled;
        private String iconurl;
        private String title;
        private String description;
        private double bonusFactor;
        private Date startDate;
        private Date endDate;

        private Builder() { }

        public Bonus build() {
            return new Bonus(bonusId, enabled, iconurl, title, description, bonusFactor, startDate, endDate);
        }

        public Builder bonusId(int bonusId) {
            this.bonusId = bonusId;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder iconurl(String iconurl) {
            this.iconurl = iconurl;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder bonusFactor(double bonusFactor) {
            this.bonusFactor = bonusFactor;
            return this;
        }

        public Builder startDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }
    }
}
