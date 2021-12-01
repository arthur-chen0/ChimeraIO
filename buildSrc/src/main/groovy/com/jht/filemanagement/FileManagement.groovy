package com.jht.filemanagement

import org.apache.tools.ant.taskdefs.condition.Os

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class FileManagement {
    public static String binPath = ""

    static void move(File src, File dest, FilenameFilter filter) {
        if (src.exists()) {
            if (src.isDirectory()) {
                dest.mkdirs()
                println("move dir ${src} ${dest}")
                List<File> folders = new ArrayList<File>()
                folders.add(src)

                while (folders.size() > 0) {
                    File folder = folders.remove(0)
                    String relativePath = ""

                    if (folder.absolutePath != src.absolutePath) {
                        relativePath = folder.absolutePath.substring(src.absolutePath.length() + 1)
                    }

                    File destPath = new File(dest, relativePath)
                    destPath.mkdirs()
                    File[] files = filter == null ? folder.listFiles() : folder.listFiles(filter)
                    if (files != null) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                folders.add(f)
                            } else {
                                File destFile = new File(destPath, f.name)
                                println("move ${f} ${destFile} ")
                                Files.move(Paths.get("${f}"), Paths.get("${destFile}"), StandardCopyOption.REPLACE_EXISTING)
                            }
                        }
                    }
                }
            } else {
                println("move ${src} ${dest}")
                Files.move(Paths.get("${src}"), Paths.get("${dest}"), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    static long getFolderSize(File f) {
        long size = 0
        List<File> folders = new ArrayList<File>()
        folders.add(f)

        while (folders.size() > 0) {
            File folder = folders.remove(0)
            File[] subFiles = folder.listFiles()

            if (subFiles != null) {
                for (File subFile : folder.listFiles()) {
                    if (subFile.isFile()) {
                        size += subFile.length()
                    } else {
                        folders.add(subFile)
                    }
                }
            }
        }

        return size
    }

    static void copy(File src, File dest, FilenameFilter filter) {
        if (src.exists()) {
            if (src.isDirectory()) {
                dest.mkdirs()
                println("copy dir ${src} ${dest}")
                List<File> folders = new ArrayList<File>()
                folders.add(src)

                while (folders.size() > 0) {
                    File folder = folders.remove(0)
                    String relativePath = ""
                    if (folder.absolutePath != src.absolutePath) {
                        relativePath = folder.absolutePath.substring(src.absolutePath.length() + 1)
                    }

                    File destPath = new File(dest, relativePath)
                    destPath.mkdirs()

                    File[] files = filter == null ? folder.listFiles() : folder.listFiles(filter)
                    if (files != null) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                folders.add(f)
                            } else {
                                File destFile = new File(destPath, f.name)
                                println("copy ${f} ${destFile} ")
                                Files.copy(Paths.get("${f}"), Paths.get("${destFile}"), StandardCopyOption.REPLACE_EXISTING)
                            }
                        }
                    }
                }
            } else {
                println("copy ${src} ${dest}")
                Files.copy(Paths.get("${src}"), Paths.get("${dest}"), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    /**
     * Move file functions to help us with moving files. We need to do this because the
     * filemanagement task cannot find the variables scoped in this class, but it can find local
     * variables in the function.
     *
     * @param src
     * @param dest
     */
    static void move(File src, File dest) {
        move(src, dest, null)
    }

    /**
     * Copy file functions to help us with copying files. We need to do this because the
     * filemanagement task cannot find the variables scoped in this class, but it can find local
     * variables in the function.
     *
     * @param src
     * @param dest
     */
    static void copy(File src, File dest) {
        copy(src, dest, null)
    }

    /**
     * Return the MD5 for the file input.
     * @param file the given file.
     * @return the MD5.
     */
    static String getMD5(File file) {
        println("Get md5 2  " + file)
        MessageDigest md = MessageDigest.getInstance("MD5")

        if (file.isDirectory()) {
            List<File> folders = new ArrayList<>()
            folders.add(file)

            while (folders.size() > 0) {
                File folder = folders.remove(0)
                File[] files = folder.listFiles()

                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            folders.add(f)
                        } else {
                            md.update(f.readBytes())
                        }
                    }
                }
            }
        } else {
            md.update(file.readBytes())
        }

        return md.digest().collect { String.format "%02x", it }.join()
    }

    static String getMD5(List<File> files) {
        MessageDigest md = MessageDigest.getInstance("MD5")

        for (File file : files) {
            if (file.isDirectory()) {
                List<File> folders = new ArrayList<>()
                folders.add(file)

                while (folders.size() > 0) {
                    File folder = folders.remove(0)
                    File[] children = folder.listFiles()

                    if (children != null) {
                        for (File f : children) {

                            if (f.isDirectory()) {
                                folders.add(f)
                            } else {
                                md.update(f.readBytes())
                            }
                        }
                    }
                }
            } else {
                md.update(file.readBytes())
            }
        }

        return md.digest().collect { String.format "%02x", it }.join()
    }

    static void zipFiles(File zipDir, String output, boolean usePassword) {
        String program = "7z"

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            program = binPath + "7za.exe"
        }

        if (usePassword) {
            println([program, "-r", "a", "-PDsUVP+%A4M<2uy6ZRs{Z", output, "*"].execute(null, zipDir).text)
        } else {
            println([program, "-r", "a", output, "*"].execute(null, zipDir).text)
        }
    }

    static void unzipFile(File zipFile, File destination, boolean usePassword) {
        String program = "7z"

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            program = binPath + "7za.exe"
        }

        // x -PDsUVP+%A4M<2uy6ZRs{Z -odeploy ${this.getCacheLocationLinux()
        if (usePassword) {
            println([program, "x", "-PDsUVP+%A4M<2uy6ZRs{Z", "-o${destination.absolutePath}", "${zipFile.absolutePath}"].execute().text)
        } else {
            println([program, "x", "-o${destination.absolutePath}", "${zipFile.absolutePath}"].execute().text)
        }
    }

    static void unzipFile(File zipFile, File dest) {
        ZipFile zip = new ZipFile(zipFile)
        dest.mkdirs()

        for (ZipEntry z : zip.entries()) {
            if (!z.isDirectory()) {
                File out = new File(dest, z.name)
                out.parentFile.mkdirs()

                out.withOutputStream { fos ->
                    zip.getInputStream(z).with { inputStream ->
                        byte[] buffer = new byte[4096]
                        int length

                        while ((length = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, length)
                        }
                    }
                }
            }
        }
    }

    static void createScriptFile(File file, String bytes) {
        if (file.exists()) {
            file.delete()
        }

        file.parentFile.mkdirs()
        file.createNewFile()
        file.withOutputStream { it.write(bytes.bytes) }
    }

    static void downloadFile(String url, File file) {
        while (url) {
            new URL(url).openConnection().with { conn ->
                conn.instanceFollowRedirects = false
                url = conn.getHeaderField( "Location" )

                if (!url) {
                    file.withOutputStream { out ->
                        conn.inputStream.with { inp ->
                            out << inp
                            inp.close()
                        }
                    }
                }
            }
        }
    }
}
