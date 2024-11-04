package buildrequest;

import auth.AuthorisingService;
import build.SystemBuildService;
import domain.DesktopMachine;
import domain.Machine;
import domain.ServerMachine;
import exceptions.MachineNotCreatedException;
import exceptions.UserNotEntitledException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirtualMachineRequestorImplTest {

    @Mock
    AuthorisingService authorisingService;

    @Mock
    SystemBuildService systemBuildService;

    @InjectMocks
    VirtualMachineRequestorImpl virtualMachineRequestor;

    @Test
    void createNewRequest_WhenUserNotEntitled_ThrowsUserNotEntitledException() {
        Machine machine = new Machine("host20230328009",
                "user1", 2, 16, 160);
        when(authorisingService.isAuthorised(machine.getRequestor())).thenReturn(false);
        assertThatThrownBy(() -> virtualMachineRequestor.createNewRequest(machine))
                .isInstanceOf(UserNotEntitledException.class)
                .hasMessage(String.format("Sorry, %s is not entitled to request a new virtual machine", machine.getRequestor()));
    }

    @Test
    void createNewRequest_WhenUserEntitled_CreateNewRequest() throws MachineNotCreatedException, UserNotEntitledException {
        Machine machine = new Machine("host20230328009",
                "user1", 2, 16, 160);
        when(authorisingService.isAuthorised(machine.getRequestor())).thenReturn(true);
        when(systemBuildService.createNewMachine(machine)).thenReturn("host20230328009");
        virtualMachineRequestor.createNewRequest(machine);
        verify(systemBuildService).createNewMachine(machine);
    }

    @Test
    void createNewRequest_WhenMachineBuildNotSuccessful_ThrowMachineNotCreatedException() throws MachineNotCreatedException, UserNotEntitledException {
        Machine machine = new Machine("host20230328009",
                "user1", 2, 16, 160);
        when(authorisingService.isAuthorised(machine.getRequestor())).thenReturn(true);
        when(systemBuildService.createNewMachine(machine)).thenReturn("");
        assertThatThrownBy(() -> virtualMachineRequestor.createNewRequest(machine))
                .isInstanceOf(MachineNotCreatedException.class)
                .hasMessage("Sorry machine build was not successful");
    }


    @Test
    void totalBuildsByUserForDay_BeforeDayEnds() throws MachineNotCreatedException, UserNotEntitledException {
        Machine machine1 = new DesktopMachine("host20230328009", "user1", 2, 16, 160, "11", "9");
        Machine machine2 = new ServerMachine( "host20230328015", "user2", 4, 32, 500, "RedHat", "9", "5.14.0-70", "Email team");
        Map<String, Map<String, Integer>> expectedMap = Map.of(
                "user1", Map.of("DesktopMachine", 1),
                "user2", Map.of("ServerMachine", 1));

        when(authorisingService.isAuthorised(machine1.getRequestor())).thenReturn(true);
        when(authorisingService.isAuthorised(machine2.getRequestor())).thenReturn(true);
        when(systemBuildService.createNewMachine(machine1)).thenReturn("host20230328009");
        when(systemBuildService.createNewMachine(machine2)).thenReturn("host20230328015");
        virtualMachineRequestor.createNewRequest(machine1);
        virtualMachineRequestor.createNewRequest(machine2);
        assertThat(virtualMachineRequestor.totalBuildsByUserForDay())
                .containsExactlyInAnyOrderEntriesOf(expectedMap);
    }


//    @Test
//    void totalFailedBuildsForDay() {
//    }
}