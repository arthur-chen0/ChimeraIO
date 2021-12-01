package com.jht.filemanagement

/**
 * Used in the file management routines to exclude files during copy.
 */
class ExcludeFileFilter implements FilenameFilter {
    private String[] filters
    private boolean isPattern = true

    ExcludeFileFilter(String[] filters) {
        this.filters = filters
    }

    ExcludeFileFilter(String[] filters, boolean isPattern) {
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

        return !matches
    }
}
