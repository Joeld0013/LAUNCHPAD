package com.launchpad.services;

import com.launchpad.dto.StartupProfileDTO;
import com.launchpad.model.Startup;
import com.launchpad.repository.StartupProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StartupProfileService {

    @Autowired
    private StartupProfileRepository startupRepository;

    public StartupProfileDTO getStartupProfile(String id) {
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with id: " + id));
        return mapToDTO(startup);
    }

    public StartupProfileDTO getStartupProfileByEmail(String email) {
        Startup startup = startupRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Startup not found with email: " + email));
        return mapToDTO(startup);
    }

    public StartupProfileDTO updateStartupProfile(String id, StartupProfileDTO dto) {
        System.out.println("DEBUG: Received DTO. Team Size: " + (dto.getTeam() != null ? dto.getTeam().size() : "null"));
        System.out.println("DEBUG: Received DTO. Milestones Size: " + (dto.getMilestones() != null ? dto.getMilestones().size() : "null"));
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with id: " + id));

        // --- Basic Info Updates ---
        if (dto.getName() != null) startup.setName(dto.getName());
        if (dto.getTitle() != null) startup.setTitle(dto.getTitle());
        if (dto.getIndustry() != null) startup.setIndustry(dto.getIndustry());
        if (dto.getStage() != null) startup.setStage(dto.getStage());
        if (dto.getLocation() != null) startup.setLocation(dto.getLocation());
        if (dto.getAbout() != null) startup.setAbout(dto.getAbout());
        if (dto.getDescription() != null) startup.setDescription(dto.getDescription());

        // --- Contact Info ---
        if (dto.getWebsite() != null) startup.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) startup.setEmail(dto.getEmail());
        if (dto.getPhone() != null) startup.setPhone(dto.getPhone());

        // --- Media ---
        if (dto.getGrowthMetrics() != null) startup.setGrowthMetrics(dto.getGrowthMetrics());
        if (dto.getPitchVideoUrl() != null) startup.setPitchVideoUrl(dto.getPitchVideoUrl());
        if (dto.getProfilePicture() != null) startup.setProfilePicture(dto.getProfilePicture());

        // --- Lists (Skills, Team, Milestones) ---

        // 1. Skills
        if (dto.getSkills() != null) {
            startup.setSkills(dto.getSkills());
        }

        // 2. Team Members
        if (dto.getTeam() != null) {
            List<Startup.TeamMember> teamEntities = dto.getTeam().stream()
                    .map(tmDTO -> new Startup.TeamMember(
                            tmDTO.getInitials(),
                            tmDTO.getName(),
                            tmDTO.getTitle(),
                            tmDTO.getDescription()
                    ))
                    .collect(Collectors.toList());
            startup.setTeam(teamEntities);
        }

        // 3. Milestones
        if (dto.getMilestones() != null) {
            List<Startup.Milestone> milestoneEntities = dto.getMilestones().stream()
                    .map(mDTO -> new Startup.Milestone(
                            mDTO.getIcon(),
                            mDTO.getTitle(),
                            mDTO.getDate()
                    ))
                    .collect(Collectors.toList());
            startup.setMilestones(milestoneEntities);
        }

        Startup updated = startupRepository.save(startup);
        return mapToDTO(updated);
    }

    private StartupProfileDTO mapToDTO(Startup startup) {
        StartupProfileDTO dto = new StartupProfileDTO();

        dto.setId(startup.getId());
        dto.setName(startup.getName());
        dto.setEmail(startup.getEmail());
        dto.setPhone(startup.getPhone());
        dto.setCountry(startup.getCountry());
        dto.setAddress(startup.getAddress());
        dto.setIndustry(startup.getIndustry());
        dto.setStage(startup.getStage());
        dto.setDescription(startup.getDescription());
        dto.setTitle(startup.getTitle());
        dto.setLocation(startup.getLocation());
        dto.setAbout(startup.getAbout());
        dto.setWebsite(startup.getWebsite());
        dto.setGrowthMetrics(startup.getGrowthMetrics());
        dto.setPitchVideoUrl(startup.getPitchVideoUrl());
        dto.setProfilePicture(startup.getProfilePicture());

        // Handle Lists safely (check for nulls in case DB has old data)
        dto.setSkills(startup.getSkills() != null ? startup.getSkills() : new ArrayList<>());

        if (startup.getTeam() != null) {
            dto.setTeam(startup.getTeam().stream()
                    .map(tm -> new StartupProfileDTO.TeamMemberDTO(
                            tm.getInitials(),
                            tm.getName(),
                            tm.getTitle(),
                            tm.getDescription()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setTeam(new ArrayList<>());
        }

        if (startup.getMilestones() != null) {
            dto.setMilestones(startup.getMilestones().stream()
                    .map(m -> new StartupProfileDTO.MilestoneDTO(
                            m.getIcon(),
                            m.getTitle(),
                            m.getDate()
                    ))
                    .collect(Collectors.toList()));
        } else {
            dto.setMilestones(new ArrayList<>());
        }

        return dto;
    }
}