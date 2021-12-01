package com.jht.filemanagement

class IncludeFileFilter implements FilenameFilter {
    private String[] filters
    private boolean isPattern

    IncludeFileFilter(String[] filters) {
        this(filters, true)
    }

    IncludeFileFilter(String[] filters, boolean isPattern) {
        this.filters = filters
        this.isPattern = isPattern
    }

    boolean accept(File f, String filename) {
        boolean matches = false

        for (String filter : filters) {
            if ((isPattern && filename.matches(filter)) || (!isPattern && filename.compareToIgnoreCase(filter) == 0)) {
                matches = true
            }
        }

        return matches
    }
}
