package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PaymentMethodDto;
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class PaymentMethodMapperImpl implements PaymentMethodMapper {

    @Override
    public PaymentMethodDto toDto(PaymentMethod paymentMethod) {
        if ( paymentMethod == null ) {
            return null;
        }

        PaymentMethodDto paymentMethodDto = new PaymentMethodDto();

        paymentMethodDto.setId( paymentMethod.getId() );
        paymentMethodDto.setIsDefault( paymentMethod.getIsDefault() );

        return paymentMethodDto;
    }

    @Override
    public PaymentMethod toEntity(PaymentMethodDto paymentMethodDto) {
        if ( paymentMethodDto == null ) {
            return null;
        }

        PaymentMethod paymentMethod = new PaymentMethod();

        paymentMethod.setId( paymentMethodDto.getId() );
        paymentMethod.setIsDefault( paymentMethodDto.getIsDefault() );

        return paymentMethod;
    }

    @Override
    public List<PaymentMethodDto> toDtoList(List<PaymentMethod> paymentMethods) {
        if ( paymentMethods == null ) {
            return null;
        }

        List<PaymentMethodDto> list = new ArrayList<PaymentMethodDto>( paymentMethods.size() );
        for ( PaymentMethod paymentMethod : paymentMethods ) {
            list.add( paymentMethodToPaymentMethodDto( paymentMethod ) );
        }

        return list;
    }

    @Override
    public List<PaymentMethod> toEntityList(List<PaymentMethodDto> paymentMethodDtos) {
        if ( paymentMethodDtos == null ) {
            return null;
        }

        List<PaymentMethod> list = new ArrayList<PaymentMethod>( paymentMethodDtos.size() );
        for ( PaymentMethodDto paymentMethodDto : paymentMethodDtos ) {
            list.add( toEntity( paymentMethodDto ) );
        }

        return list;
    }

    protected PaymentMethodDto paymentMethodToPaymentMethodDto(PaymentMethod paymentMethod) {
        if ( paymentMethod == null ) {
            return null;
        }

        PaymentMethodDto paymentMethodDto = new PaymentMethodDto();

        paymentMethodDto.setId( paymentMethod.getId() );
        paymentMethodDto.setIsDefault( paymentMethod.getIsDefault() );

        return paymentMethodDto;
    }
}
