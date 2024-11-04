package domain;

public class Machine {
    private final String hostname;
    private final String requestor;
    private final int numberOfCPUs;
    private final int RAMSize;
    private final int hardDiskSize;


    public Machine(String hostname, String requestor, int numberOfCPUs, int ramSize, int hardDiskSize) {
        this.hostname = validateHostname(hostname);
        this.requestor = requestor;

        if(numberOfCPUs <= 0)
            throw new IllegalArgumentException("number of CPU must be greater than 0");
        this.numberOfCPUs = numberOfCPUs;
        if(ramSize <= 0)
            throw new IllegalArgumentException("number of RAM must be greater than 0");
        RAMSize = ramSize;
        if(hardDiskSize <= 0)
            throw new IllegalArgumentException("hard disk size must be greater than 0");
        this.hardDiskSize = hardDiskSize;
    }

    private static String validateHostname(String hostname){
        boolean isMatch = hostname.matches("^host\\d{8}\\d{3}$");
        if(!isMatch)
            throw new IllegalArgumentException("The hostname must be of the form: host<date><build_request_number>. Eg: host20230328005");
        return hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public int getHardDiskSize() {
        return hardDiskSize;
    }

    public int getRAMSize() {
        return RAMSize;
    }

    public int getNumberOfCPUs() {
        return numberOfCPUs;
    }

    public String getRequestor() {
        return requestor;
    }
}
