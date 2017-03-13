package com.stefan.utilities;

import com.stefan.user.User;
import junitx.util.PropertyManager;;
import org.apache.commons.lang3.StringUtils;;
import org.testng.Assert;
import org.openqa.selenium.logging.LogEntry;
import org.testng.Reporter;

import java.util.Date;

/**
 * Created by StefanB on 3/7/2017.
 */
public class ProPrint {
    private StringBuffer sBuffer;
    private static boolean debug = false;
    private boolean print = false;

    public ProPrint() {
        sBuffer = new StringBuffer();
        if(StringUtils.isNotEmpty(System.getenv("PropertyManager.file")) && Boolean.parseBoolean(PropertyManager.getProperty("DEBUG"))) {
            debug = true;
            System.out.println("DEBUG MODE ENABLED.");
        }
    }

    public ProPrint append(String str) {
        sBuffer.append(str);
        if (print) {
            this.write();
        }
        return this;
    }

    public void appendTestStart(String str) {
        sBuffer.append("\n*************************************************\n");
        sBuffer.append("[STARTING TESTCASE: ").append(str).append("]\n");
    }

    public void appendInfo(String str) {
        sBuffer.append(getInfoText()).append(str).append("\n");
    }

    public void appendDebug(String str) {
        if (debug)
            sBuffer.append(getDebugText()).append(str).append("\n");
    }

    public void appendWarn(String str) {
        sBuffer.append(getWarnText()).append(str).append("\n");
    }

    public void appendError(String str) {
        sBuffer.append(getErrorText()).append(str).append("\n");
        errorCount++;
    }

    public void appendError(String str, Throwable th) {
        appendError(str + " -- Throwable: " + condenseErrorMsg(th.getLocalizedMessage()));
    }

    public void appendErrorWithFail(String str) {
        appendError(str);
        Assert.fail(str);
    }

    public void appendErrorWithFail(String str, Throwable th) {
        String fullError = str + " - " + condenseErrorMsg(th.getLocalizedMessage());
        appendError(fullError);
        append(condenseStackTrace(th.getStackTrace()));
        Assert.fail(fullError);
    }

    public ProPrint appendTestSuccess(String str) {
        sBuffer.append(getTestSuccessText(str) + "\n*************************************************\n");
        return this;
    }

    public ProPrint appendTestSkip(String str) {
        sBuffer.append(getTestSkippedText() + " - " + str + "\n*************************************************\n");
        return this;
    }

    public void appendTestFail(String str) {
        sBuffer.append(getTestFailText(str) + "\n-----**************----------***************-----\n");
    }

    public void appendTestFail(String str, Throwable th) {
        appendTestFail(str + " -- Throwable: " + condenseErrorMsg(th.getLocalizedMessage()));
    }

    public void appendBroswerConsoleEntry(LogEntry entry) {
        sBuffer.append(String.format("[CONSOLE][%s] %s %s\n", new Date(entry.getTimestamp()), entry.getLevel(), entry.getMessage()));
    }

    public void write() {
        if (sBuffer != null && sBuffer.length() > 0) {
            Reporter.log(sBuffer.toString());
            System.out.println(sBuffer.toString() + "\n");
            sBuffer = new StringBuffer();
        }
    }

    public void clear(){
        sBuffer = new StringBuffer();
    }

    static int errorCount = 0;

    public static int getErrorCount() {
        return errorCount;
    }

    public static void setErrorCount(int errorCount) {
        ProPrint.errorCount = errorCount;
    }

    public void setPrint(boolean bool) {
        this.print = bool;
    }

    public static void testStart(String str) {
        ProPrint.divider();
        System.out.println("[STARTING TESTCASE: " + str + "]");
    }

    public static void testSuccess(String str) {
        System.out.println(getTestSuccessText(str) + "\n*************************************************\n");
    }

    public static void testFail(String str) {
        System.out.println(getTestFailText(str) + "\n");
    }

    public static void testSkip(String str) {
        System.out.println(getTestSkippedText() + " - " + str + "\n");
    }

    public static void info(String info) {
        System.out.println(getInfoText() + info);
    }

    public static void debug(int num) {
        debug(num + "");
    }

    public static void debug(String str) {
        if (debug) {
            System.out.println(getDebugText() + str);
        }
    }

    public static void error(String err) {
        System.out.println(getErrorText() + err);
        errorCount++;
    }

    public static void errorWithFail(String err) {
        Assert.fail(err);
    }

    public static void errorWithFail(String err, Throwable th) {
        String fullError = err + " - " + condenseErrorMsg(th.getLocalizedMessage());
        error(fullError);
        System.out.println(condenseStackTrace(th.getStackTrace()));
        Assert.fail(fullError);
    }

    public static void error(String err, Throwable th) {
        error(err + " -- Throwable: " + condenseErrorMsg(th.getLocalizedMessage()));
    }

    public static void warn(String warn) {
        System.out.println("[*WARN*] " + warn);
    }

    public static void divider() {
        System.out.println("\n*************************************************");
    }

    public void appendUserInfo(User user) {
        append("**********NEW USER INFORMATION**********");
        append("\nUSERNAME: " + user.getUsername());
        append("\nPASSWORD: " + user.getPassword());
        append("\nFIRST NAME: " + user.getFirstName());
        append("\nLAST NAME: " + user.getLastName());
        append("\nEMAIL: " + user.getEmail());
        append("\nAUTH TOKEN: " + user.getAuthToken());
        append("\n******************************************\n");
    }

    //Get special Text Format
    public static String getErrorText() {
        return "[**ERROR**] ";
    }

    public static String getInfoText() {
        return "[INFO] ";
    }

    public static String getWarnText() {
        return "[*WARN*] ";
    }

    public static String getDebugText() {
        return "[##DEBUG##] ";
    }

    public static String getTestFailText(String str) {
        return "--[TEST FAILED: " + str + "]";
    }

    public static String getTestSuccessText(String str) {
        return "[TEST SUCCESSFUL: " + str + "]";
    }

    public static String getTestSkippedText() {
        return "[TEST SKIPPED]";
    }

//Condense Messaging Methods

    /**
     * Takes a stackTrace and removes everything that is NOT in a 'com.yummly' package.
     *
     * @param stackTrace
     * @return
     */
    public static String condenseStackTrace(final StackTraceElement[] stackTrace) {
        StringBuilder formattedStackTrace = new StringBuilder();
        String newLine = "\n";
        int index = 0;
        for (StackTraceElement element : stackTrace) {
            // Only want info about our classes
            if (element.getClassName().contains("com.yummly.core")) {
                formattedStackTrace.append(newLine);
                formattedStackTrace.append(("Index: " + index++ + newLine));
                formattedStackTrace.append(("ClassName: " + element.getClassName() + newLine));
                formattedStackTrace.append(("MethodName: " + element.getMethodName() + newLine));
                formattedStackTrace.append(("FileName: " + element.getFileName() + newLine));
                formattedStackTrace.append(("LineNumber: " + element.getLineNumber() + newLine));
            }
        }
        return formattedStackTrace.toString();
    }

    /**
     * Removes excess error messaging and condenses it into a single line.
     *
     * @param error
     * @return
     */
    public static String condenseErrorMsg(String error) {
        int returnedLines = 3;
        String[] temp = error.split("\n");
        // Need to construct the error strings together, and remove any excess information.
        String stackTrace = "";
        for (int i = 0; i < temp.length && i < returnedLines; i++) {
            stackTrace += temp[i];
        }
        return stackTrace;
    }
}
