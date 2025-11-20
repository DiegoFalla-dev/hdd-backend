package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setRoles( mapRolesToStrings( user.getRoles() ) );
        userDto.setPaymentMethods( mapPaymentMethods( user.getPaymentMethods() ) );
        userDto.setNationalId( user.getNationalId() );
        userDto.setAvatar( user.getAvatar() );
        userDto.setBirthDate( user.getBirthDate() );
        userDto.setEmail( user.getEmail() );
        userDto.setFirstName( user.getFirstName() );
        userDto.setGender( user.getGender() );
        userDto.setId( user.getId() );
        userDto.setLastName( user.getLastName() );

        return userDto;
    }
}
