package com.jht.androiduiwidgets.helper;

import android.content.Context;
import android.content.res.Resources;

import com.jht.androidcommonalgorithms.type.JHTCommonTypes;
import com.jht.androiduiwidgets.R;

@SuppressWarnings("WeakerAccess")
public class StringHelper {
    public enum CASE {
        LOWER_CASE,
        UPPER_CASE,
        TITLE_CASE
    }


    static public String getUnitString(Context context, JHTCommonTypes.DisplayUnit unit, JHTCommonTypes.Unit unitType) {
        return getUnitString(context, unit, unitType, StringHelper.CASE.LOWER_CASE);
    }

    static public String getUnitString(Context context, JHTCommonTypes.DisplayUnit unit, JHTCommonTypes.Unit unitType, StringHelper.CASE stringCase) {
        int id = 0;
        if(unit == null) return "";
        switch (unit) {
            case DISTANCE:
                switch(unitType) {
                    case Metric:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.km_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.km_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.km_uc;
                                break;
                        }
                        break;
                    case Imperial:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.miles_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.miles_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.miles_uc;
                                break;
                        }
                        break;
                }
                break;
            case DISTANCE_SMALL:
                switch(unitType) {
                    case Metric:
                        id = R.string.m_lc;
                        break;
                    case Imperial:
                        id = R.string.ft_lc;
                        break;
                }
                break;
            case SPEED:
                switch(unitType) {
                    case Metric:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.kph_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.kph_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.kph_uc;
                                break;
                        }
                        break;
                    case Imperial:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.mph_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.mph_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.mph_uc;
                                break;
                        }
                        break;
                }
                break;
            case HEART_RATE:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.bpm_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.bpm_uc;
                        break;
                    case TITLE_CASE:
                        id = R.string.bpm_uc;
                        break;
                }
                break;
            case TIME_SECONDS:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.seconds_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.seconds_tc;
                        break;
                    case TITLE_CASE:
                        id = R.string.seconds_tc;
                        break;
                }
                break;
            case TIME_MINUTES:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.minutes_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.minutes_tc;
                        break;
                    case TITLE_CASE:
                        id = R.string.minutes_tc;
                        break;
                }
                break;
            case TIME_HOURS:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.hours_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.hours_tc;
                        break;
                    case TITLE_CASE:
                        id = R.string.hours_tc;
                        break;
                }
                break;
            case PERCENT:
                id = R.string.percent_lc;
                break;
            case CALORIES:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.cals_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.calories_uc;
                        break;
                    case TITLE_CASE:
                        id = R.string.calories_tc;
                        break;
                }
                break;
            case WEIGHT:
                switch(unitType) {
                    case Metric:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.kg_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.kg_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.kg_uc;
                                break;
                        }
                        break;
                    case Imperial:
                        switch(stringCase) {
                            case LOWER_CASE:
                                id = R.string.lbs_lc;
                                break;
                            case UPPER_CASE:
                                id = R.string.lbs_uc;
                                break;
                            case TITLE_CASE:
                                id = R.string.lbs_uc;
                                break;
                        }
                        break;
                }
                break;
            case FLOORS:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.floors_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.floors_uc;
                        break;
                    case TITLE_CASE:
                        id = R.string.floors_tc;
                        break;
                }
                break;
            case SPM:
                id = R.string.spm_uc;
                break;
            case RPM:
                id = R.string.rpm_uc;
                break;
            case HEIGHT:
                switch(unitType) {
                    case Metric:
                        id = R.string.cm_lc;
                        break;
                    case Imperial:
                        id = R.string.inches_lc;
                        break;
                }
                break;

            case TIME_YEARS:
                switch(stringCase) {
                    case LOWER_CASE:
                        id = R.string.years_lc;
                        break;
                    case UPPER_CASE:
                        id = R.string.years_uc;
                        break;
                    case TITLE_CASE:
                        id = R.string.years_tc;
                        break;
                }
                break;
            case ADC:
                switch(stringCase) {
                    case LOWER_CASE:
                        return "adc"; //NON-NLS
                    case UPPER_CASE:
                        return "ADC"; //NON-NLS
                    case TITLE_CASE:
                        return "ADC"; //NON-NLS
                }
                break;
        }
        try {
            if(id == 0) {
                return "";
            }
            return context.getResources().getString(id);
        }
        catch(Resources.NotFoundException ex) {
            return "";
        }
    }


}
