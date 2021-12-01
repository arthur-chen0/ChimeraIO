import com.jht.filemanagement.FileManagement
import groovy.json.JsonSlurper

class AWSPackageObject {
    private String packageMD5 = ""
    private String sourceMD5 = null

    AWSPackageObject(String bucket, String key) {
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
            print(headerInfo)
            if(headerInfo != null && headerInfo.Metadata != null) {
                sourceMD5 = headerInfo.Metadata.sourcemd5
                packageMD5 = headerInfo.ETag
                packageMD5 = packageMD5.substring(1, packageMD5.length() - 1)
            }
        }

    }


    boolean sourceMd5Match(String sourceMD5) {

        return sourceMD5 != null && sourceMD5 == this.sourceMD5
    }

    boolean md5Match(String md5) {
        return packageMD5 == md5
    }

    String packageMD5() {
        return this.packageMD5
    }
}