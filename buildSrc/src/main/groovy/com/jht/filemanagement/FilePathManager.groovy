package com.jht.filemanagement;

class FilePathManager {
    /**
     * This Deploy project path.
     */
    public String projectPath

    /**
     * The root project path
     */
    public String rootPath
    public String networkPath
    public String releasePath

    /**
     * Usually releasePath + "properties"
     */
    public String propertiesPath

    /**
     * Usually releasePath + "properties" + flavorDetail.localProperties.
     */
    public String propertiesFile

    String processTags(String path) {
        if (path == null) {
            return path
        }

        // FIXME(ARR) - Determine this is supposed to be doing.
        //path = path.replaceAll(java.util.regex.Pattern.quote("${ZDRIVE}"), zDrivePath)
        path = path.replace("${PROJECT}", projectPath)
        path = path.replace("${ROOT}", rootPath)
        path = path.replace("${NETWORK}", networkPath)
        path = path.replace("${RELEASE}", releasePath)
        return path;
    }
}
