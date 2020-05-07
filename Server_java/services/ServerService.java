package services;

import model.entities.Volunteer;
import proto.services.Case;
import proto.services.Donor;
import proto.services.IServicesGrpc;
import proto.services.Response;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ServerService extends IServicesGrpc.IServicesImplBase {
    private DonorService donorService;
    private DonationService donationService;
    private CaseService caseService;
    private VolunteerService volunteerService;
    private LinkedHashSet<model.entities.Volunteer> volunteersOnline;

    public ServerService(DonorService donorService, DonationService donationService, CaseService caseService, VolunteerService service) {
        this.donorService = donorService;
        this.donationService = donationService;
        this.caseService = caseService;
        this.volunteerService = service;
        volunteersOnline = new LinkedHashSet<>();
    }

    public void findVolunteer(proto.services.Volunteer request, io.grpc.stub.StreamObserver<proto.services.Response> responseStreamObserver) {
        System.out.println("Login ...");
        model.entities.Volunteer volunteer = volunteerService.findVolunteer(request.getUser(), request.getPassword());
        if(volunteersOnline.contains(volunteer)) {
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.Error).setMessage("Volunteer already online").build());
        }
        else {
            volunteersOnline.add(volunteer);
            System.out.println("Volunteer logged in ...");
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.Ok).setDid(volunteer.getId()).build());
        }
        responseStreamObserver.onCompleted();
    }

    public void logout(proto.services.Volunteer request, io.grpc.stub.StreamObserver<Response> responseStreamObserver) {
        System.out.println("Logout ...");
        model.entities.Volunteer volunteer = new Volunteer(request.getId(), request.getUser(), request.getPassword());
        if(!volunteersOnline.contains(volunteer)) {
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.Error).setMessage("Volunteer not online").build());
        }
        else {
            volunteersOnline.remove(volunteer);
            System.out.println("Volunteer logged out ...");
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.Ok).build());
        }
        responseStreamObserver.onCompleted();
    }

    public void addDonor(proto.services.Donor request, io.grpc.stub.StreamObserver<Response> responseStreamObserver) {
        System.out.println("Add donor ...");
        try {
            donorService.addDonor(request.getName(), request.getAddress(), request.getPhoneNumber());
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.AddDonor).build());
        }
        catch (Exception e) {
            responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.Error).setMessage(e.getMessage()).build());
        }
        responseStreamObserver.onCompleted();
    }

    public void findAllDonors(proto.services.Empty request, io.grpc.stub.StreamObserver<Response> responseStreamObserver)
    {
        List<proto.services.Donor> donors = new ArrayList<>();
        donorService.findAll().forEach(
                x -> donors.add(Donor.newBuilder()
                        .setName(x.getName())
                        .setAddress(x.getAddress())
                        .setPhoneNumber(x.getPhoneNumber())
                        .setId(x.getId())
                        .build())
        );
        responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.GetAllDonors).addAllDonors(donors).build());
        responseStreamObserver.onCompleted();
    }

    public void findAllDonorsBySubstring(proto.services.Substring request, io.grpc.stub.StreamObserver<Response> responseStreamObserver)
    {
        String substring = request.getSubstring();
        List<proto.services.Donor> donors = new ArrayList<>();
        donorService.findDonorByName(substring).forEach(
                x -> donors.add(Donor.newBuilder()
                        .setName(x.getName())
                        .setAddress(x.getAddress())
                        .setPhoneNumber(x.getPhoneNumber())
                        .setId(x.getId())
                        .build())
        );
        responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.GetAllDonors).addAllDonors(donors).build());
        responseStreamObserver.onCompleted();
    }

    public void findAllCases(proto.services.Empty request, io.grpc.stub.StreamObserver<Response> responseStreamObserver)
    {
        List<proto.services.Case> cases = new ArrayList<>();
        caseService.findAll().forEach(
                x -> cases.add(Case.newBuilder()
                        .setId(x.getId())
                        .setName(x.getName())
                        .setTotalSumDonated(x.getTotalSumDonated())
                        .build())
        );
        responseStreamObserver.onNext(Response.newBuilder().setType(Response.Type.GetAllCases).addAllCases(cases).build());
        responseStreamObserver.onCompleted();
    }

    public void addDonation(proto.services.Donation donation, io.grpc.stub.StreamObserver<Response> responseStreamObserver) {
        //TODO
    }

}
