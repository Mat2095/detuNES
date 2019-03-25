package de.mat2095.detunes;


public class RunConfiguration {

    long sleepDuration;
    int sleepPeriodicity;
    Integer startPC;
    boolean debugPrintGeneralInfo;
    int[] debugPrintMem;
    Integer debugPrintMemText;

    public RunConfiguration(long sleepDuration, int sleepPeriodicity) {
        this.sleepDuration = sleepDuration;
        this.sleepPeriodicity = sleepPeriodicity;
        this.startPC = null;
        this.debugPrintGeneralInfo = false;
        this.debugPrintMem = null;
        this.debugPrintMemText = null;
    }
}
