package de.mat2095.detunes;


public class RunConfiguration {

    Integer sleepPeriodicity;
    long sleepDuration;

    Integer startPC;

    boolean debugPrintGeneralInfo;
    int[] debugPrintMem;
    Integer debugPrintMemText;

    Integer stopAddr;
    byte stopValueUnequal;


    public RunConfiguration() {
        this.sleepPeriodicity = null;

        this.startPC = null;

        this.debugPrintGeneralInfo = false;
        this.debugPrintMem = null;
        this.debugPrintMemText = null;

        this.stopAddr = null;
    }
}
