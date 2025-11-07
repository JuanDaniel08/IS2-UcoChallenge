package co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.TextHelper;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;

public record RegisterUserInputDTO(String idType, UUID idNumber, String firstName, String secondName, String firstSurname,
		String secondSurname, UUID homeCity, String email, String mobileNumber) {
	
	public static RegisterUserInputDTO normalize(final String idType, final UUID idNumber,final String firstName,final String secondName,final String firstSurname,
			final String secondSurname,final UUID homeCity,final String email,final String mobileNumber) {
		 var  idTypeNormalized = TextHelper.getDefaultWithTrim(idType);
		var idNumberNormalized = UUIDHelper.getDefault(idNumber);
		var firstNameNormalized = TextHelper.getDefaultWithTrim(firstName);
		 var secondNameNormalized = TextHelper.getDefaultWithTrim(secondName);
		 var firstSurnameNormalized = TextHelper.getDefaultWithTrim(firstSurname);
		 var secondSurnameNormalized = TextHelper.getDefaultWithTrim(secondSurname);
		 var homeCityNormalized = UUIDHelper.getDefault(homeCity);
		 var emailNormalized = TextHelper.getDefaultWithTrim(email);
		 var mobileNumberNormalized = TextHelper.getDefaultWithTrim(mobileNumber);
		 
		 return new RegisterUserInputDTO(idTypeNormalized, idNumberNormalized, firstNameNormalized, secondNameNormalized, firstSurnameNormalized, secondSurnameNormalized, homeCityNormalized, emailNormalized, mobileNumberNormalized);
	}
}
