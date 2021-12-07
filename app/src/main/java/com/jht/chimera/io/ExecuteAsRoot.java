package com.jht.chimera.io;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NonNls;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExecuteAsRoot {
    private static final String TAG = ExecuteAsRoot.class.getSimpleName();
    private static final String ROOT_ACCESS_CODE = "su 548"; //NON-NLS
    private static final boolean DEBUG = false;
    protected ArrayList<String> mCommandList = new ArrayList<String>();

    /**
     * @return the root access code.
     */
    public static String getRootAccessCode() {
        return ROOT_ACCESS_CODE;
    }

    // FIXME - Unclosed Streams
    public static boolean canRunRootCommands() {
        boolean retval = false;
        Process suProcess;
        
        try {
            suProcess = Runtime.getRuntime().exec(ROOT_ACCESS_CODE);

            DataOutputStream outputStream = new DataOutputStream(suProcess.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));

            if (outputStream != null && bufferedReader != null) {
                // Getting the id of the current user to check if this is root
                outputStream.writeBytes("id\n"); //NON-NLS
                outputStream.flush();

                String currUid = bufferedReader.readLine();
                boolean exitSu = false;
                if (null == currUid) {
                    retval = false;
                    exitSu = false;
                    Log.d(TAG, "Can't get root access or denied by user");
                } else if (true == currUid.contains("uid=0")) {
                    retval = true;
                    exitSu = true;
                    Log.d(TAG, "Root access granted");
                } else {
                    retval = false;
                    exitSu = true;
                    Log.d(TAG, "Root access rejected: " + currUid);
                }

                if (exitSu) {
                    outputStream.writeBytes("exit\n"); //NON-NLS
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output
            // stream (os) after su failed, meaning that the device is not
            // rooted

            retval = false;
            Log.d(TAG, "Root access rejected [" + e.getClass().getName() + "]", e);
        }

        return retval;
    }

    public static void runSyncCommand() {
        ExecuteAsRoot exec = new ExecuteAsRoot();
        exec.addCommand("sync");
        exec.execute();
    }

    public static void copyFilePreserve(@NonNls String srcPath, @NonNls String destPath) {
        ExecuteAsRoot exec = new ExecuteAsRoot();
        @NonNls String cmd = "cp -rfp \"" + srcPath + "\" \"" + destPath + "\"";
        Log.d(TAG, "copyFilePreserve> " + cmd);
        exec.addCommand(cmd);
        //exec.addCommand("chmod 777 \"" + destPath + "\""); // avoid issue of file only having root access (or use cp "p" to preserve ownership?)
        //exec.addCommand("sync");
        exec.execute();
    }
    public static void copyFileIfDifferent(@NonNls String srcPath, @NonNls String destPath) {
        ExecuteAsRoot exec = new ExecuteAsRoot();
        @NonNls String cmd = "cmp -s \"" + srcPath + "\" \"" + destPath + "\" || cp -rf \"" + srcPath + "\" \"" + destPath + "\"";
        //Log.d(TAG, "copyFileIfDifferent> " + cmd);
        exec.addCommand(cmd);
        exec.execute();
    }

    // Possible alternative to GrantPermission.grant() reflection method?
    public static void grantPermission(@NonNls String packageName, @NonNls String permission) {
        ExecuteAsRoot exec = new ExecuteAsRoot();
        @NonNls String cmd = "pm grant " + packageName + " " + permission;
        //Log.d(TAG, "grantPermission> " + cmd);
        exec.addCommand(cmd);
        exec.execute();
    }

    public ExecuteAsRoot addCommand(@NonNls String command) {
        mCommandList.add(command);
        return this;
    }
    public ExecuteAsRoot addCommand(@NonNls String[] commands) {
        for (String command : commands) mCommandList.add(command);
        return this;
    }
    public void clear() { mCommandList.clear(); }

    public void setCommandList(ArrayList<String> commandList) {
        mCommandList = commandList;
    }

    public final boolean execute() {
        return execute(false, true, null, null);
    }
    public final boolean execute(boolean asynchronous) {
        return execute(asynchronous, true, null, null);
    }

    // FIXME - Unclosed Streams
    public final boolean execute(boolean asynchronous, boolean exit, StringBuilder output, ArrayList outputArray) {
        boolean retval = false;

        try {
            if (null != mCommandList && mCommandList.size() > 0) {
                Process suProcess = Runtime.getRuntime().exec(ROOT_ACCESS_CODE);

                DataOutputStream os = new DataOutputStream(
                        suProcess.getOutputStream());


                // Execute commands that require root access
                for (String currCommand : mCommandList) {
                    if (DEBUG)
                        Log.d(TAG, currCommand);
                    os.writeBytes(currCommand + (asynchronous ? " &" : "") + "\n");
                    os.flush();
                }

                if(exit) {
                    os.writeBytes("exit\n"); //NON-NLS
                }
                os.flush();

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(suProcess.getInputStream()));

                // Grab the results
                if(output != null) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                if(outputArray != null){
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        outputArray.add(line);
                    }
                }

                retval = true;
                if(exit) {
                    try {
                        int suProcessRetval = suProcess.waitFor();
                        if (255 != suProcessRetval) {
                            // Root access granted
                            retval = true;
                        } else {
                            // Root access denied
                            retval = false;
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Error executing root action", ex);
                    }
                }
            }
        } catch (IOException ex) {
            Log.w(TAG, "Can't get root access", ex);
        } catch (SecurityException ex) {
            Log.w(TAG, "Can't get root access", ex);
        } catch (Exception ex) {
            Log.w(TAG, "Error executing internal operation", ex);
        }

        return retval;
    }

    public static void chmod(@NonNls String mode, @NonNls String path) {
        ExecuteAsRoot chmod = new ExecuteAsRoot();
        chmod.addCommand("chmod " + mode + " " + path);
        chmod.execute();
    }

    public static void chmodRecursive(@NonNls String mode, @NonNls String path) {
        ExecuteAsRoot chmod = new ExecuteAsRoot();
        chmod.addCommand("chmod -R -f " + mode + " " + path);
        chmod.execute();
    }

    public static void touch(@NonNls String path) {
        ExecuteAsRoot chmod = new ExecuteAsRoot();
        chmod.addCommand("touch " + path);
        chmod.execute();
    }

    public static void run(@NonNls String command) {
        ExecuteAsRoot chmod = new ExecuteAsRoot();
        chmod.addCommand(command);
        chmod.execute();
    }

    public static void fixFilesInPackage(@NonNls String packageName) {

        ExecuteAsRoot exec = new ExecuteAsRoot();
        exec.addCommand("find /data/data/" + packageName + " -not -name \"lib\" -print0 | xargs -0 chown `ls -l /data/data | grep " + packageName + " | tr -s ' ' | cut -d' ' -f3`");
        exec.addCommand("find /data/data/" + packageName + " -not -name \"lib\" -print0 | xargs -0 chgrp `ls -l /data/data | grep " + packageName + " | tr -s ' ' | cut -d' ' -f4`");
        exec.addCommand("find /data/data/" + packageName + " -not -name \"lib\" -print0 | xargs -0 restorecon ");
        exec.execute();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String command : mCommandList) {
            sb.append(command + "\n");
        }
        return sb.toString();
    }
}
