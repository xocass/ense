package gal.usc.etse.sharecloud.client.clientModel.dto;

import java.util.Date;

public record UserProfile(String email, String username, Date birthdate, String country, String city, String image){}
