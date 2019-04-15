package de.mat2095.detunes;


public class RunConfiguration {

    Integer sleepPeriodicity;
    long sleepDuration;

    Integer startPC;

    boolean debugCpuPrintGeneralInfo;
    int[] debugCpuPrintMem;
    Integer debugCpuPrintMemText;

    boolean debugPpuPrintAccesses;

    Integer stopAddr;
    byte stopValueUnequal;


    public RunConfiguration() {
        this.sleepPeriodicity = null;

        this.startPC = null;

        this.debugCpuPrintGeneralInfo = false;
        this.debugCpuPrintMem = null;
        this.debugCpuPrintMemText = null;

        this.debugPpuPrintAccesses = false;

        this.stopAddr = null;
    }
}
