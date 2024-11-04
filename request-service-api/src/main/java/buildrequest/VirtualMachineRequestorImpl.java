package buildrequest;

import auth.AuthorisingService;
import build.SystemBuildService;
import domain.Machine;
import exceptions.MachineNotCreatedException;
import exceptions.UserNotEntitledException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VirtualMachineRequestorImpl implements VirtualMachineRequestor{

    private final AuthorisingService authorisingService;
    private final SystemBuildService systemBuildService;
    private int totalFailures;
    private LocalDate lastResetDay;
    private Map<String, Map<String, Integer>> totalBuildsByUserForDay = new HashMap<>();

    public VirtualMachineRequestorImpl(AuthorisingService authorisingService, SystemBuildService systemBuildService) {
        this.authorisingService = authorisingService;
        this.systemBuildService = systemBuildService;
        this.lastResetDay = LocalDate.now();
    }

    @Override
    public void createNewRequest(Machine machine) throws UserNotEntitledException, MachineNotCreatedException {
        if (!authorisingService.isAuthorised(machine.getRequestor()))
            throw new UserNotEntitledException(String.format("Sorry, %s is not entitled to request a new virtual machine", machine.getRequestor()));
        String result = systemBuildService.createNewMachine(machine);

        if (result.isEmpty()) {
            totalFailures++;
            throw new MachineNotCreatedException("Sorry machine build was not successful");
        }

        LocalDate today = LocalDate.now();
        if (!today.isEqual(lastResetDay)) {
            totalFailures = 0;
            totalBuildsByUserForDay = new HashMap<>();
            lastResetDay = today;
        }

        String user = machine.getRequestor();
        String machineType = machine.getClass().getSimpleName();
        if (totalBuildsByUserForDay.containsKey(user)) {
            Map<String, Integer> buildsByUser = totalBuildsByUserForDay.get(user);
            buildsByUser.put(machineType, buildsByUser.getOrDefault(machineType, 0) + 1);
        } else {
            totalBuildsByUserForDay.put(user, new HashMap<>(Map.of(machineType, 1)));
        }
    }


    @Override
    public Map<String, Map<String, Integer>> totalBuildsByUserForDay() {
        return totalBuildsByUserForDay;
    }

    @Override
    public int totalFailedBuildsForDay() {
        return totalFailures;
    }
}
