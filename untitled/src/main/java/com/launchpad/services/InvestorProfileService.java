package com.launchpad.services;

import com.launchpad.dto.InvestorProfileDTO;
import com.launchpad.model.Investor;
import com.launchpad.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class InvestorProfileService {

    @Autowired
    private InvestorRepository investorRepository;

    public InvestorProfileDTO getInvestorProfile(String id) {
        Investor investor = investorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found with id: " + id));

        return mapToDTO(investor);
    }

    public InvestorProfileDTO getInvestorProfileByEmail(String email) {
        Investor investor = investorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found with email: " + email));

        return mapToDTO(investor);
    }

    public InvestorProfileDTO updateInvestorProfile(String id, InvestorProfileDTO dto) {
        Investor investor = investorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found with id: " + id));

        // Update basic info
        if (dto.getName() != null) investor.setName(dto.getName());
        if (dto.getTitle() != null) investor.setTitle(dto.getTitle());
        if (dto.getLocation() != null) investor.setLocation(dto.getLocation());
        if (dto.getAbout() != null) investor.setAbout(dto.getAbout());

        // Update contact info
        if (dto.getWebsite() != null) investor.setWebsite(dto.getWebsite());
        if (dto.getEmail() != null) investor.setEmail(dto.getEmail());
        if (dto.getPhone() != null) investor.setPhone(dto.getPhone());

        // Update profile picture
        if (dto.getProfilePicture() != null) investor.setProfilePicture(dto.getProfilePicture());

        // Update investment thesis
        if (dto.getInvestmentThesis() != null) {
            Investor.InvestmentThesis thesis = new Investor.InvestmentThesis();
            thesis.setStage(dto.getInvestmentThesis().getStage());
            thesis.setCheckSize(dto.getInvestmentThesis().getCheckSize());
            thesis.setHorizon(dto.getInvestmentThesis().getHorizon());
            investor.setInvestmentThesis(thesis);
        }

        // Update investment focus
        if (dto.getInvestmentFocus() != null) investor.setInvestmentFocus(dto.getInvestmentFocus());

        // Update team members
        if (dto.getTeam() != null) {
            investor.setTeam(dto.getTeam().stream()
                    .map(tm -> {
                        Investor.TeamMember member = new Investor.TeamMember();
                        member.setInitials(tm.getInitials());
                        member.setName(tm.getName());
                        member.setTitle(tm.getTitle());
                        member.setDescription(tm.getDescription());
                        return member;
                    })
                    .collect(Collectors.toList()));
        }

        // Update portfolio
        if (dto.getPortfolio() != null) {
            investor.setPortfolio(dto.getPortfolio().stream()
                    .map(pc -> {
                        Investor.PortfolioCompany company = new Investor.PortfolioCompany();
                        company.setInitials(pc.getInitials());
                        company.setName(pc.getName());
                        company.setSector(pc.getSector());
                        company.setDescription(pc.getDescription());
                        return company;
                    })
                    .collect(Collectors.toList()));
        }

        // Save and return
        Investor updated = investorRepository.save(investor);
        return mapToDTO(updated);
    }

    private InvestorProfileDTO mapToDTO(Investor investor) {
        InvestorProfileDTO dto = new InvestorProfileDTO();
        dto.setId(investor.getId());
        dto.setName(investor.getName());
        dto.setEmail(investor.getEmail());
        dto.setPhone(investor.getPhone());
        dto.setCountry(investor.getCountry());
        dto.setOrganization(investor.getOrganization());
        dto.setAddress(investor.getAddress());
        dto.setInvestorType(investor.getInvestorType());
        dto.setTitle(investor.getTitle());
        dto.setLocation(investor.getLocation());
        dto.setAbout(investor.getAbout());
        dto.setWebsite(investor.getWebsite());
        dto.setProfilePicture(investor.getProfilePicture());
        dto.setInvestmentFocus(investor.getInvestmentFocus());

        // Map investment thesis
        if (investor.getInvestmentThesis() != null) {
            dto.setInvestmentThesis(new InvestorProfileDTO.InvestmentThesisDTO(
                    investor.getInvestmentThesis().getStage(),
                    investor.getInvestmentThesis().getCheckSize(),
                    investor.getInvestmentThesis().getHorizon()
            ));
        }

        // Map team members
        if (investor.getTeam() != null) {
            dto.setTeam(investor.getTeam().stream()
                    .map(tm -> new InvestorProfileDTO.TeamMemberDTO(
                            tm.getInitials(),
                            tm.getName(),
                            tm.getTitle(),
                            tm.getDescription()
                    ))
                    .collect(Collectors.toList()));
        }

        // Map portfolio
        if (investor.getPortfolio() != null) {
            dto.setPortfolio(investor.getPortfolio().stream()
                    .map(pc -> new InvestorProfileDTO.PortfolioCompanyDTO(
                            pc.getInitials(),
                            pc.getName(),
                            pc.getSector(),
                            pc.getDescription()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}