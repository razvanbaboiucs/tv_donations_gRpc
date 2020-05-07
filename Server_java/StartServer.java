import repos.repositories.CaseDBRepository;
import repos.repositories.DonationDBRepository;
import repos.repositories.DonorDBRepository;
import repos.repositories.VolunteerDBRepository;
import repos.utils.ICaseRepository;
import repos.utils.IDonationRepository;
import repos.utils.IDonorRepository;
import repos.utils.IVolunteerRepository;
import services.*;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort = 7777;
    public static void main(String[] args) throws IOException, InterruptedException {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("/tvdonationsprojectserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find bd.config "+e);
            return;
        }

        IVolunteerRepository volunteerRepository = new VolunteerDBRepository(serverProps);
        ICaseRepository caseRepository = new CaseDBRepository(serverProps);
        IDonorRepository donorRepository = new DonorDBRepository(serverProps);
        IDonationRepository donationRepository = new DonationDBRepository(serverProps);

        VolunteerService volunteerService = new VolunteerService(volunteerRepository);
        CaseService caseService = new CaseService(caseRepository);
        DonorService donorService = new DonorService(donorRepository);
        DonationService donationService = new DonationService(donationRepository);

        ServerService serverImpl = new ServerService(donorService, donationService, caseService, volunteerService);
        ObserverImplementation observerImplementation = new ObserverImplementation(donorService, caseService, donationService, volunteerService);
        ProtoServer server = new ProtoServer(defaultPort, serverImpl, observerImplementation);
        server.start();
        server.blockUntilShutdown();
    }
}
