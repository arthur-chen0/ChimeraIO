package com.jht.filemanagement

import groovy.json.JsonSlurper

/**
 * This helps us upload packages to AWS.
 */
class AWSHelper {
    private AWSHelper() { throw AssertionError("Stop trying to instantiate helper classes") }

    /**
     * Return true if the object exists on AWS.
     *
     * @param bucket     the AWS bucket
     * @param key        the AWS key
     * @param disableAWS
     * @return true if the object exists on AWS, else false.
     */
    static boolean objectExists(String bucket, String key, boolean disableAWS) {
        String command = "aws s3 ls s3://$bucket/$key"
        println(command)

        if (disableAWS) {
            return false
        }

        // FIXME(ARR) - Convert to something that is easier to translate to a better language.
        Process p = command.execute()
        p.waitFor()
        println("object exists exit value ${p.exitValue()}")
        return p.exitValue() == 0
    }

    /**
     * Uploads the file to AWS if it does not exist.
     *
     * @param bucket     The AWS bucket.
     * @param key        The AWS key.
     * @param file       The file to upload.
     * @param disableAWS
     */
    static void putObjectIfNotExists(String bucket, String key, File file, String sourceMD5, boolean disableAWS) {
        if (!objectExists(bucket, key, disableAWS)) {
            println("put object")
            putObject(bucket, key, file, sourceMD5, disableAWS)
        }
    }

    /**
     * Uploads the file to AWS. This will override any existing file if called.
     *
     * @param bucket     The AWS bucket
     * @param key        The AWS key
     * @param file       The file to upload.
     * @param disableAWS
     */
    static void putObject(String bucket, String key, File file, String sourceMD5, boolean disableAWS) {
        String md5 = FileManagement.getMD5(file)
        long installSize = file.size() * 2

        String[] command = (String[]) [
                "aws",
                "s3api",
                "put-object",
                "--bucket",
                bucket,
                "--key",
                key,
                "--metadata",
                "Content-MD5=${md5},Install-Size=${installSize},SourceMD5=${sourceMD5}",
                "--acl",
                "public-read",
                "--body",
                file.absolutePath
        ]

        println(command)

        if (!disableAWS) {
            Process process = command.execute()
            process.waitForProcessOutput(System.out, System.err)
            println("Exit value ${process.exitValue()}")
        }
    }

    static boolean sourceMd5Match(String bucket, String key, String sourceMD5) {

        String[] command = (String[]) [
                "aws",
                "s3api",
                "head-object",
                "--bucket",
                bucket,
                "--key",
                key
        ]

        println(command)

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        Process process = command.execute()

        process.waitForProcessOutput(out, err)
        if(process.exitValue() == 0) {
            JsonSlurper jsonSlurper = new JsonSlurper()
            def headerInfo = jsonSlurper.parseText(out.toString())
            println("bdc_check " + key + " " + sourceMD5 + " " + headerInfo.Metadata.sourcemd5)
            return sourceMD5 != null && headerInfo.Metadata.sourcemd5 == sourceMD5
        }

        return false
    }

    static boolean md5Match(String bucket, String key, String md5) {

        String[] command = (String[]) [
                "aws",
                "s3api",
                "head-object",
                "--bucket",
                bucket,
                "--key",
                key
        ]

        println(command)

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        Process process = command.execute()

        process.waitForProcessOutput(out, err)
        if(process.exitValue() == 0) {
            JsonSlurper jsonSlurper = new JsonSlurper()
            def headerInfo = jsonSlurper.parseText(out.toString())
            return headerInfo.ETag == "\"" + md5 + "\""
        }

        return false
    }
}
// aws s3api head-object --bucket my-bucket --key index.html