/*
 * Copyright 2019-2024 Steinar Bang
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

public class SumYearMonth {

    private double sum;
    private int year;
    private int month;

    private SumYearMonth() {}

    public double getSum() {
        return sum;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private double sum = 0.0;
        private int year = -1;
        private int month = -1;

        public SumYearMonth build() {
            SumYearMonth sumYearMonth = new SumYearMonth();
            sumYearMonth.sum = this.sum;
            sumYearMonth.year = this.year;
            sumYearMonth.month = this.month;
            return sumYearMonth;
        }

        public Builder sum(double sum) {
            this.sum = sum;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder month(int month) {
            this.month = month;
            return this;
        }

    }

}
