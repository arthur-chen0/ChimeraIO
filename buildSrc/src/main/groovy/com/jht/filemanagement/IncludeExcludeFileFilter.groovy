package com.jht.filemanagement

class IncludeExcludeFileFilter implements FilenameFilter {
    private String[] includeFilters
    private String[] excludeFilters

    IncludeExcludeFileFilter(String[] includeFilters, String[] excludeFilters) {
        this.includeFilters = includeFilters
        this.excludeFilters = excludeFilters
    }

    private boolean inFilter(String fileName, String[] filters) {
        boolean matches = false

        for (String filter : filters) {
            if (fileName.matches(filter)) {
                matches = true
            }
        }

        return matches
    }

    @Override
    boolean accept(File f, String filename) {
//        FIXME(ARR) - Remove because references function that does not exist.
//        return inFilter(filename, includeFilters) && !found(filename, excludeFilters)
        throw new RuntimeException("Cannot use this because not correctly implemented.")
    }
}
