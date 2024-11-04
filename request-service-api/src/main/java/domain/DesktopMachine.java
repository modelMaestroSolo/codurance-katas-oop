package domain;

public class DesktopMachine extends Machine{
    private final String MsWindowsVersion;
    private final String buildNumber;

    public DesktopMachine(String hostname, String requestor, int numberOfCPUs, int ramSize, int hardDiskSize, String msWindowsVersion, String buildNumber) {
        super(hostname, requestor, numberOfCPUs, ramSize, hardDiskSize);
        this.MsWindowsVersion = msWindowsVersion;
        this.buildNumber = buildNumber;
    }

    public String getMsWindowsVersion() {
        return MsWindowsVersion;
    }

    public String getBuildNumber() {
        return buildNumber;
    }
}
