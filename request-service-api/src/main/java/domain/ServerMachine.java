package domain;

public class ServerMachine extends Machine{
    private final String distro;
    private final String distroMajorNumber;
    private final String kernelVersion;
    private final String adminTeam;



    public ServerMachine(String hostname, String requestor, int numberOfCPUs, int ramSize, int hardDiskSize, String distro, String distroMajorNumber, String kernelVersion, String adminTeam) {
        super(hostname, requestor, numberOfCPUs, ramSize, hardDiskSize);
        this.distro = distro;
        this.distroMajorNumber = distroMajorNumber;
        this.kernelVersion = kernelVersion;
        this.adminTeam = adminTeam;
    }

    public String getDistro() {
        return distro;
    }

    public String getDistroMajorNumber() {
        return distroMajorNumber;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public String getAdminTeam() {
        return adminTeam;
    }
}
