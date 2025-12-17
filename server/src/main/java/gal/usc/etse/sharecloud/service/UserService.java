package gal.usc.etse.sharecloud.service;

import gal.usc.etse.sharecloud.model.dto.AuthRequest;
import gal.usc.etse.sharecloud.model.dto.SpotifyProfile;
import gal.usc.etse.sharecloud.model.dto.UserSearchResult;
import gal.usc.etse.sharecloud.model.entity.User;
import gal.usc.etse.sharecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(String[]::new))
                .build();
    }


    public void register(AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {throw new RuntimeException("Email already registered");}

        User user = new User(request.email(), passwordEncoder.encode(request.password()), Set.of("USER"));
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public SpotifyProfile returnSpotifyProfile(String id){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SpotifyProfile(
                user.getSpotifyProfile().getSpotifyID(),
                user.getSpotifyProfile().getDisplayName(),
                user.getSpotifyProfile().getEmail(),
                user.getSpotifyProfile().getCountry(),
                user.getSpotifyProfile().getImage(),
                user.getSpotifyProfile().getnFollowers(),
                user.getSpotifyProfile().getProfileURL());
    }

    public List<UserSearchResult> searchUsers(String query, String requesterId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UsernameNotFoundException(requesterId));
        Set<String> friendIds = requester.getFriendIds() != null
                ? requester.getFriendIds()
                : Set.of();

        List<User> users =
                userRepository.findTop10BySpotifyProfile_DisplayNameContainingIgnoreCase(query.trim());

        return users
                .stream()
                .filter(user -> !user.getId().equals(requesterId)) // no te devuelves a ti mismo
                .map(user -> new UserSearchResult(
                        user.getId(),
                        user.getSpotifyProfile().getDisplayName(),
                        user.getSpotifyProfile().getImage(),
                        user.getSpotifyProfile().getCountry(),
                        friendIds.contains(user.getId())
                ))
                .toList();
    }

}
