package co.edu.uco.ucochallenge.user.updateuser.application.usecase.domain;

import java.util.UUID;

public class UpdateUserDomain {
    private final UUID userId;
    private final UUID idType;
    private final String idNumber;
    private final String firstName;
    private final String secondName;
    private final String firstSurname;
    private final String secondSurname;
    private final UUID homeCity;
    private final String email;
    private final String mobileNumber;

    public UpdateUserDomain(UUID userId, UUID idType, String idNumber,
                            String firstName, String secondName,
                            String firstSurname, String secondSurname,
                            UUID homeCity, String email, String mobileNumber) {
        this.userId = userId;
        this.idType = idType;
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.secondName = secondName;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.homeCity = homeCity;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public UUID getUserId() { return userId; }
    public UUID getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public String getFirstName() { return firstName; }
    public String getSecondName() { return secondName; }
    public String getFirstSurname() { return firstSurname; }
    public String getSecondSurname() { return secondSurname; }
    public UUID getHomeCity() { return homeCity; }
    public String getEmail() { return email; }
    public String getMobileNumber() { return mobileNumber; }
}
