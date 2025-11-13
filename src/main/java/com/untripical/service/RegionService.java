package com.untripical.service;

import com.untripical.dto.region.RegionRequestDTO;
import com.untripical.dto.region.RegionResponseDTO;
import com.untripical.enums.RegionType;
import com.untripical.exception.region.RegionDoesNotExist;
import com.untripical.mapper.region.RegionMapper;
import com.untripical.model.Region;
import com.untripical.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private RegionMapper regionMapper;

    public RegionResponseDTO getRegionById(Long regionId){
         Region region = findRegionById(regionId);
         return regionMapper.toResponse(region);
    }

    public RegionResponseDTO getByRegionType(RegionType type){
        Region region = regionRepository.findByType(type)
                .orElseThrow(()-> new RegionDoesNotExist("Region with this type does not exist"));
        if(!checkIsActive(region)){
            throw new RegionDoesNotExist("This region is not active");
        }
        return regionMapper.toResponse(region);
    }

    public RegionResponseDTO getByClosestBigCity(String closestBigCity){
        Region region = regionRepository.findByClosestBigCity(closestBigCity)
                .orElseThrow(()-> new RegionDoesNotExist("Region with this closestBigCity does not exist"));
        if(!checkIsActive(region)){
            throw new RegionDoesNotExist("This region is not active");
        }
        return regionMapper.toResponse(region);
    }

    public List<RegionResponseDTO> getAllRegions(){
        return regionRepository.findAll()
                .stream()
                .filter(re -> checkIsActive(re))
                .map(regionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public RegionResponseDTO addRegion(RegionRequestDTO dto){
        Region saved = regionRepository.save(regionMapper.toEntity(dto));
        return regionMapper.toResponse(saved);
    }

    public void deleteRegion(Long regionId){
        Region region = findRegionById(regionId);
        region.setIsActive(false);
    }

    public RegionResponseDTO updateRegion(Long regionId, RegionRequestDTO dto){
        Region region = findRegionById(regionId);
        region.setType(dto.getType());
        region.setClosestBigCity(dto.getClosestBigCity());

        Region updatedRegion = regionRepository.save(region);
        return regionMapper.toResponse(updatedRegion);
    }

    public boolean checkIsActive(Region region){
        return region.getIsActive();
    }

    public Region findRegionById(Long regionId){
        Region region = regionRepository.findById(regionId)
                .orElseThrow(()-> new RegionDoesNotExist("This region does not exist"));
        if(!checkIsActive(region)){
            throw new RegionDoesNotExist("This region is not active");
        }
        return region;
    }
}
