package services;

import io.grpc.stub.StreamObserver;
import proto.services.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ObserverImplementation extends IObserverGrpc.IObserverImplBase {
    private DonorService donorService;
    private CaseService caseService;
    private DonationService donationService;
    private VolunteerService volunteerService;

    private static LinkedHashSet<StreamObserver<proto.services.Response>> observers = new LinkedHashSet<>();

    public ObserverImplementation(DonorService donorService, CaseService caseService, DonationService donationService, VolunteerService volunteerService) {
        this.donorService = donorService;
        this.caseService = caseService;
        this.donationService = donationService;
        this.volunteerService = volunteerService;
    }

    public StreamObserver<proto.services.Donation> updateDonationAdded(StreamObserver<proto.services.Response> responseStreamObserver) {
        observers.add(responseStreamObserver);
        System.out.println("New observer added ...");
        return new StreamObserver<Donation>() {
            @Override
            public void onNext(Donation value) {
                System.out.println("New donation added ...");
                try {
                    donationService.add(value.getDid(), value.getCid(), value.getSumDonated());
                    caseService.updateCase(value.getCid(), value.getSumDonated());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                List<proto.services.Donor> donors = new ArrayList<>();
                donorService.findAll().forEach(
                        x -> donors.add(Donor.newBuilder()
                                .setId(x.getId())
                                .setPhoneNumber(x.getPhoneNumber())
                                .setAddress(x.getAddress())
                                .setName(x.getName())
                                .build())
                );
                List<proto.services.Case> cases = new ArrayList<>();
                caseService.findAll().forEach(
                        x -> cases.add(Case.newBuilder()
                                .setName(x.getName())
                                .setTotalSumDonated(x.getTotalSumDonated())
                                .setId(x.getId())
                                .build())
                );
                for (StreamObserver<Response> observer : observers) {
                    observer.onNext(Response.newBuilder().setType(Response.Type.Update).addAllCases(cases).addAllDonors(donors).build());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Connection error ...");
                observers.remove(responseStreamObserver);
                responseStreamObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                observers.remove(responseStreamObserver);
                System.out.println("Observer terminated ...");
            }
        };
    }
}
