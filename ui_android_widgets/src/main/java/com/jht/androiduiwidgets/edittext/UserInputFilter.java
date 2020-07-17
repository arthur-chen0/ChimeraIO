package com.jht.androiduiwidgets.edittext;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

class UserInputFilter implements InputFilter {


    private boolean spacesAllowed;
    private boolean underscoresAllowed;
    private boolean digitsAllowed;
    private boolean allCaps;

    UserInputFilter(boolean spacesAllowed, boolean underscoresAllowed, boolean digitsAllowed, boolean allCaps) {
        this.spacesAllowed = spacesAllowed;
        this.underscoresAllowed = underscoresAllowed;
        this.digitsAllowed = digitsAllowed;
        this.allCaps = allCaps;
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder sb = new StringBuilder(end - start);
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (isCharAllowed(c)) {
                if(allCaps) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }

            }
        }

        if (source instanceof Spanned) {
            SpannableString sp = new SpannableString(sb);
            TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
            return sp;
        } else {
            return sb;
        }
    }

    private boolean isCharAllowed(char c) {
        if(Character.isLetter(c)) {
            return true;
        }
        if(digitsAllowed && Character.isDigit(c)) {
            return true;
        }
        if(spacesAllowed && c == ' ') {
            return true;
        }
        if(underscoresAllowed && c == '_') {
            return true;
        }
        return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
    }
}
