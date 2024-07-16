package com.osaebros.eventplanner.repository.mapping;

import com.osaebros.eventplanner.entity.Link;
import com.osaebros.eventplanner.entity.ServiceProvider;
import com.osaebros.eventplanner.model.ServiceProviderListEntryModel;
import com.osaebros.eventplanner.repository.dto.ServiceProviderDto;
import com.osaebros.eventplanner.model.ServiceProviderProfileModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {

    ServiceProviderMapper INSTANCE = Mappers.getMapper(ServiceProviderMapper.class);

    @Named("linksToMap")
    default Map<String, String> linksToMap(List<Link> links) {
        if (links == null) {
            return null;
        }
        return links.stream()
                .collect(Collectors.toMap(
                        Link::getType,
                        Link::getUrl,
                        (existing, replacement) -> existing // In case of duplicate keys, keep the existing value
                ));

    }

    @Mappings({
            @Mapping(source = "links", target = "links", qualifiedByName = "linksToMap")
    })
    ServiceProviderProfileModel serviceProviderToProfile(ServiceProvider serviceProvider);

    ServiceProviderListEntryModel serviceProviderToListEntry(ServiceProvider serviceProvider);

    ServiceProviderDto serviceProviderToDto(ServiceProvider savedAccount);
}